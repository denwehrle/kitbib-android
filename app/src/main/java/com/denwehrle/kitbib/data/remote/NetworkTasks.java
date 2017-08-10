package com.denwehrle.kitbib.data.remote;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.denwehrle.kitbib.R;
import com.denwehrle.kitbib.data.model.BibFee;
import com.denwehrle.kitbib.data.model.BibSummaryItem;
import com.denwehrle.kitbib.data.model.BibSummaryItemState;
import com.denwehrle.kitbib.data.model.LearningPlace;
import com.denwehrle.kitbib.data.model.LearningPlaceOccupation;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncBookList;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncFeeList;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncLearningPlaceList;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncLearningPlaceOccupationList;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncStatus;
import com.denwehrle.kitbib.data.remote.parser.LearningPlaceOccupationParser;
import com.denwehrle.kitbib.data.remote.parser.LearningPlaceParser;
import com.denwehrle.kitbib.utils.DateUtils;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dennis Wehrle
 */
public class NetworkTasks {

    public static class Login extends AsyncTask<String, Void, Boolean> {

        private AsyncStatus delegate;
        private String sessionId = "";

        public Login(AsyncStatus delegate) {
            this.delegate = delegate;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean result = false;

            try {
                String requestStartUri = Endpoints.BaseAddress + Endpoints.StartSessionRoute;

                Connection connect = Jsoup.connect(requestStartUri);
                connect.method(Connection.Method.GET);
                Connection.Response initialResponse = connect.execute();

                Document doc = initialResponse.parse();

                Pattern pattern = Pattern.compile("\\d{20}");
                Matcher matcher = pattern.matcher(doc.html());

                if (matcher.find()) {
                    sessionId = matcher.group(0);
                }

                String loginUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute, sessionId);

                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("opacdb", "UBKA_OPAC");
                loginData.put("Maske", "Anmeldung");
                loginData.put("session", sessionId);
                loginData.put("Funktion", "Kontoinfo");
                loginData.put("Login-User-ID", params[0]);
                loginData.put("Login-Password", params[1]);
                loginData.put("submitButton", "Anmelden");

                Connection.Response res = Jsoup.connect(loginUri).data(loginData).cookies(initialResponse.cookies()).method(Connection.Method.POST).timeout(10000).execute();
                Log.d("STATUS", res.statusMessage());

                String bodyAsi = res.body();

                if (!bodyAsi.contains("Fehler"))
                    result = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {

            // send result back to calling method
            delegate.result(loggedIn);
        }

        @Override
        protected void onCancelled() {
            delegate.result(false);
        }
    }

    public static class DownloadBooks extends AsyncTask<Void, Void, List<BibSummaryItem>> {

        private AsyncBookList delegate;
        private Context mContext;

        private String sessionId = "";
        private String name = "";
        private String password = "";

        public DownloadBooks(AsyncBookList delegate, Context context, Account account) {
            if (context == null)
                return;
            mContext = context;
            this.delegate = delegate;
            name = account.name;
            AccountManager accountManager = AccountManager.get(mContext);
            password = accountManager.getPassword(account);
        }

        @Override
        protected List<BibSummaryItem> doInBackground(Void... params) {

            List<BibSummaryItem> result = new ArrayList<>();

            try {
                String requestStartUri = Endpoints.BaseAddress + Endpoints.StartSessionRoute;

                Connection connect = Jsoup.connect(requestStartUri);
                connect.method(Connection.Method.GET);
                Connection.Response initialResponse = connect.execute();

                Document doc = initialResponse.parse();

                Pattern p = Pattern.compile("\\d{20}");
                Matcher m = p.matcher(doc.html());

                if (m.find()) {
                    sessionId = m.group(0);
                }

                String loginUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute, sessionId);

                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("opacdb", "UBKA_OPAC");
                loginData.put("Maske", "Anmeldung");
                loginData.put("session", sessionId);
                loginData.put("Funktion", "Kontoinfo");
                loginData.put("Login-User-ID", name);
                loginData.put("Login-Password", password);
                loginData.put("submitButton", "Anmelden");

                Jsoup.connect(loginUri).data(loginData).cookies(initialResponse.cookies()).method(Connection.Method.POST).execute();

                String kontoauszugUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute + Endpoints.FunctionRoute, sessionId, sessionId, Endpoints.FunctionKontoauszug);

                Document doc3 = Jsoup.connect(kontoauszugUri).cookies(initialResponse.cookies()).get();

                Elements nodeCollection = doc3.getElementById("col3_content").getElementsByTag("table").get(1).getElementsByTag("tr");

                int i = 1;

                for (int n = 3; n < nodeCollection.size() - 8; n += 4) {
                    BibSummaryItem bibSummaryItem = new BibSummaryItem();

                    try {
                        bibSummaryItem.index = String.valueOf(i++);
                        bibSummaryItem.title = nodeCollection.get(n).text().trim();

                        String stateIconUri = nodeCollection.get(n + 1).getElementsByTag("td").get(0).getElementsByTag("img").get(0).attr("src");

                        if (stateIconUri.contains("weiss.gif")) {
                            bibSummaryItem.state = BibSummaryItemState.Ordered;
                        } else if (stateIconUri.contains("gruen.gif")) {
                            bibSummaryItem.state = BibSummaryItemState.MoreThanTenDays;
                        } else if (stateIconUri.contains("gelb.gif")) {
                            bibSummaryItem.state = BibSummaryItemState.LessThanTenDays;
                        } else if (stateIconUri.contains("rot.gif")) {
                            bibSummaryItem.state = BibSummaryItemState.Overdue;
                        } else {
                            bibSummaryItem.state = BibSummaryItemState.Error;
                        }

                        bibSummaryItem.originBib = nodeCollection.get(n + 1).getElementsByTag("td").get(1).text().trim().replace("&nbsp;", "");
                        bibSummaryItem.signature = nodeCollection.get(n + 1).getElementsByTag("td").get(2).text().trim().replace("&nbsp;", "");

                        bibSummaryItem.orderInfo = nodeCollection.get(n + 1).getElementsByTag("td").get(4).text().trim().replace("&nbsp;", "");
                        if (bibSummaryItem.orderInfo.contains("bestellt"))
                            bibSummaryItem.orderInfo = mContext.getString(R.string.order_info);
                        else if (bibSummaryItem.orderInfo.contains("abholbereit"))
                            bibSummaryItem.orderInfo = mContext.getString(R.string.order_info2);

                        String dueDate = nodeCollection.get(n + 1).getElementsByTag("td").get(3).getAllElements().get(1).text().trim().replace("&nbsp;", "");
                        bibSummaryItem.dueDate = DateUtils.stringToDateFormat(dueDate);
                        if (bibSummaryItem.dueDate == null)
                            bibSummaryItem.dueDate = new Date();

                        String flagged = nodeCollection.get(n + 1).getElementsByTag("td").get(5).text().trim().replace("&nbsp;", "");
                        bibSummaryItem.flagged = (!flagged.isEmpty() && !flagged.equals(""));

                        try {
                            String requestUri = String.format(Endpoints.AmazonBookCoverLookupAddress, bibSummaryItem.title);

                            Connection con = Jsoup.connect(requestUri);
                            con.method(Connection.Method.GET);
                            Connection.Response response = con.execute();

                            Document document = response.parse();
                            bibSummaryItem.bookCover = document.getElementById("item-1").attr("src");

                        } catch (Exception e) {
                            bibSummaryItem.bookCover = "";
                        }

                        result.add(bibSummaryItem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<BibSummaryItem> resultList) {
            delegate.resultList(resultList);
        }
    }

    public static class DownloadFees extends AsyncTask<Void, Void, List<BibFee>> {

        private AsyncFeeList delegate;
        private Context mContext;

        private String sessionId = "";
        private String name = "";
        private String password = "";

        public DownloadFees(AsyncFeeList delegate, Context context, Account account) {
            if (context == null)
                return;
            mContext = context;
            this.delegate = delegate;
            name = account.name;
            AccountManager accountManager = AccountManager.get(mContext);
            password = accountManager.getPassword(account);
        }

        @Override
        protected List<BibFee> doInBackground(Void... params) {

            List<BibFee> result = new ArrayList<>();

            try {
                String requestStartUri = Endpoints.BaseAddress + Endpoints.StartSessionRoute;

                Connection connect = Jsoup.connect(requestStartUri);
                connect.method(Connection.Method.GET);
                Connection.Response initialResponse = connect.execute();

                Document doc = initialResponse.parse();

                Pattern p = Pattern.compile("\\d{20}");
                Matcher m = p.matcher(doc.html());

                if (m.find()) {
                    sessionId = m.group(0);
                }

                String loginUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute, sessionId);

                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("opacdb", "UBKA_OPAC");
                loginData.put("Maske", "Anmeldung");
                loginData.put("session", sessionId);
                loginData.put("Funktion", "Kontoinfo");
                loginData.put("Login-User-ID", name);
                loginData.put("Login-Password", password);
                loginData.put("submitButton", "Anmelden");

                Jsoup.connect(loginUri).data(loginData).cookies(initialResponse.cookies()).method(Connection.Method.POST).execute();

                String gebuehrenUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute + Endpoints.FunctionRoute, sessionId, sessionId, Endpoints.FunctionKontoInfo);

                Document doc3 = Jsoup.connect(gebuehrenUri).cookies(initialResponse.cookies()).get();

                Elements nodeCollection = doc3.getElementById("col3_content").getElementsByTag("td");

                for (int n = 0; n < nodeCollection.size(); n++) {
                    BibFee bibFeeItem = new BibFee();

                    //ab n=18 sind alle 'td'-Elemente 5er Gruppen, die jeweils für eine Mahnung stehen
                    //n=18 -> Datum, n=19 -> Gebühr, n=20 -> Gebührbezeichnung, n=21 -> Artikel/Buch n=22 -> leer
                    if (n >= 18) {

                        try {
                            String dueDate = nodeCollection.get(n).text().trim().replace("&nbsp;", "");
                            bibFeeItem.dueDate = DateUtils.stringToDateFormat(dueDate);

                            bibFeeItem.fee = nodeCollection.get(n + 1).text();
                            bibFeeItem.feeTitle = nodeCollection.get(n + 2).text();

                            //Gebühr "für ... | ..."
                            String articleNode = nodeCollection.get(n + 3).text();
                            String[] articleArray = articleNode.split(" \\| ");
                            bibFeeItem.signature = articleArray[0].substring(5);
                            bibFeeItem.article = articleArray[1];

                            result.add(bibFeeItem);
                            n += 4;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<BibFee> resultList) {
            delegate.resultList(resultList);
        }
    }

    public static class SingleExtension extends AsyncTask<String, Void, Boolean> {

        private AsyncStatus delegate;
        private Context mContext;

        private String sessionId = "";
        private String name = "";
        private String password = "";

        public SingleExtension(AsyncStatus delegate, Context context, Account account) {
            if (context == null)
                return;
            mContext = context;
            this.delegate = delegate;
            name = account.name;
            AccountManager accountManager = AccountManager.get(mContext);
            password = accountManager.getPassword(account);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean result = false;

            try {
                String requestStartUri = Endpoints.BaseAddress + Endpoints.StartSessionRoute;

                Connection connect = Jsoup.connect(requestStartUri);
                connect.method(Connection.Method.GET);
                Connection.Response initialResponse = connect.execute();

                Document doc = initialResponse.parse();

                Pattern p = Pattern.compile("\\d{20}");
                Matcher m = p.matcher(doc.html());

                if (m.find()) {
                    sessionId = m.group(0);
                }

                String loginUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute, sessionId);

                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("opacdb", "UBKA_OPAC");
                loginData.put("Maske", "Anmeldung");
                loginData.put("session", sessionId);
                loginData.put("Funktion", "Kontoinfo");
                loginData.put("Login-User-ID", name);
                loginData.put("Login-Password", password);
                loginData.put("submitButton", "Anmelden");

                Jsoup.connect(loginUri).data(loginData).cookies(initialResponse.cookies()).method(Connection.Method.POST).execute();
                String kontoauszugUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute + Endpoints.FunctionRoute, sessionId, sessionId, Endpoints.FunctionKontoauszug);
                Jsoup.connect(kontoauszugUri).cookies(initialResponse.cookies()).get();

                String extendSingleUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute + Endpoints.FunctionRoute, sessionId, sessionId, Endpoints.FunctionEinzelverlaengerung) + String.format(Endpoints.ParameterZeile, params[0]);
                Log.d("URL", extendSingleUri);

                Connection.Response res = Jsoup.connect(extendSingleUri).data(loginData).cookies(initialResponse.cookies()).timeout(10000).execute();
                Log.d("STATUS", res.statusMessage());

                String bodyAsi = res.body();

                if (bodyAsi.contains("ckgabedatum"))
                    result = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            delegate.result(result);
        }
    }

    public static class GeneralExtension extends AsyncTask<Void, Void, Boolean> {

        private AsyncStatus delegate;
        private Context mContext;

        private String sessionId = "";
        private String name = "";
        private String password = "";

        public GeneralExtension(AsyncStatus delegate, Context context, Account account) {
            if (context == null)
                return;
            mContext = context;
            this.delegate = delegate;
            name = account.name;
            AccountManager accountManager = AccountManager.get(mContext);
            password = accountManager.getPassword(account);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result = false;

            try {
                String requestStartUri = Endpoints.BaseAddress + Endpoints.StartSessionRoute;

                Connection connect = Jsoup.connect(requestStartUri);
                connect.method(Connection.Method.GET);
                Connection.Response initialResponse = connect.execute();

                Document doc = initialResponse.parse();

                Pattern p = Pattern.compile("\\d{20}");
                Matcher m = p.matcher(doc.html());

                if (m.find()) {
                    sessionId = m.group(0);
                }

                String loginUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute, sessionId);

                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("opacdb", "UBKA_OPAC");
                loginData.put("Maske", "Anmeldung");
                loginData.put("session", sessionId);
                loginData.put("Funktion", "Kontoinfo");
                loginData.put("Login-User-ID", name);
                loginData.put("Login-Password", password);
                loginData.put("submitButton", "Anmelden");

                Jsoup.connect(loginUri).data(loginData).cookies(initialResponse.cookies()).method(Connection.Method.POST).execute();

                String extendUri = Endpoints.BaseAddress + String.format(Endpoints.AccountRoute + Endpoints.FunctionRoute, sessionId, sessionId, Endpoints.FunctionPauschalverlaengerung);
                Log.d("URL", extendUri);

                Connection.Response res = Jsoup.connect(extendUri).data(loginData).cookies(initialResponse.cookies()).timeout(10000).execute();
                Log.d("STATUS", res.statusMessage());

                String bodyAsi = res.body();

                if (bodyAsi.contains("Exemplare konnten"))
                    result = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            delegate.result(result);
        }
    }

    public static class DownloadLearningPlaces extends AsyncTask<Void, Void, List<LearningPlace>> {

        private AsyncLearningPlaceList delegate;

        public DownloadLearningPlaces(AsyncLearningPlaceList delegate) {
            this.delegate = delegate;
        }

        @Override
        protected List<LearningPlace> doInBackground(Void... params) {

            List<LearningPlace> result = new ArrayList<>();

            InputStream inputStream = retrieveStream(Endpoints.URL_LEARNING_PLACES);

            if (inputStream != null) {
                try {
                    String jsonString = IOUtils.toString(inputStream, "UTF-8");
                    result.addAll(LearningPlaceParser.parse(jsonString));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<LearningPlace> resultList) {
            delegate.resultList(resultList);
        }
    }

    public static class DownloadLearningPlaceOccupations extends AsyncTask<Void, Void, List<LearningPlaceOccupation>> {

        private AsyncLearningPlaceOccupationList delegate;

        public DownloadLearningPlaceOccupations(AsyncLearningPlaceOccupationList delegate) {
            this.delegate = delegate;
        }

        @Override
        protected List<LearningPlaceOccupation> doInBackground(Void... params) {

            List<LearningPlaceOccupation> result = new ArrayList<>();

            InputStream inputStream = retrieveStream(Endpoints.URL_LEARNING_PLACE_OCCUPATIONS);

            if (inputStream != null) {
                try {
                    String jsonString = IOUtils.toString(inputStream, "UTF-8");
                    result.addAll(LearningPlaceOccupationParser.parse(jsonString));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<LearningPlaceOccupation> resultList) {
            delegate.resultList(resultList);
        }
    }

    private static InputStream retrieveStream(final String requestUrl) {
        URL url;
        HttpURLConnection connection;

        try {
            url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            final int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            return connection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}