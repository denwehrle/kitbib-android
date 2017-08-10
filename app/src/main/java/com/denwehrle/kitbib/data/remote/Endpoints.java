package com.denwehrle.kitbib.data.remote;

/**
 * @author Dennis Wehrle
 */
public class Endpoints {

    public static String BaseAddress = "https://i3vloan.ubka.uni-karlsruhe.de/";

    public static String StartSessionRoute = "i3v_library/i3v_ausleihe.cgi?start=1&opacdb=UBKA_OPAC";
    public static String AccountRoute = "%s/i3v_library/ausleihe/i3v_ausleihe.cgi";
    public static String FunctionRoute = "?opacdb=UBKA_OPAC&session=%s&Funktion=%s";

    public static String FunctionKontoInfo = "Kontoinfo";
    public static String FunctionKontoauszug = "Kontoauszug";
    public static String FunctionPauschalverlaengerung = "Pauschalverlaengerung";
    public static String FunctionEinzelverlaengerung = "Verlaengern";
    public static String ParameterZeile = "&zeile=%s";

    public static String AmazonBookCoverLookupAddress = "http://bigbooksearch.com/query.php?SearchIndex=books&Keywords=%s&ItemPage=1";

    private static final String BROKER_BASE_URL = "http://seatfinder.bibliothek.kit.edu/karlsruhe/getdata.php?location=" +
            "LSG,LST,LSW,LSM,LSN,LBS,FBC,LAF,FBW,FBP,FBI,FBM,FBA,BIB-N,FBH,FBD,BLB,WIS,TheaBib" +
            "&values=%s&after&before=now&limit=1";
    public static final String URL_LEARNING_PLACE_OCCUPATIONS = String.format(BROKER_BASE_URL, "seatestimate");
    public static final String URL_LEARNING_PLACES = String.format(BROKER_BASE_URL, "location");
}
