package com.denwehrle.kitbib.features.main;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.denwehrle.kitbib.R;
import com.denwehrle.kitbib.data.auth.AuthenticatorUtils;
import com.denwehrle.kitbib.data.remote.NetworkTasks;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncStatus;
import com.denwehrle.kitbib.data.sync.SyncUtils;
import com.denwehrle.kitbib.features.about.AboutActivity;
import com.denwehrle.kitbib.features.login.LoginActivity;
import com.denwehrle.kitbib.utils.RecentTasksStyler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Dennis Wehrle
 */
public class MainActivity extends AppCompatActivity {

    public static Account mAccount;
    private ViewPager mViewpager;
    private View mCoordinatorLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecentTasksStyler.styleRecentTasksEntry(this);

        setContentView(R.layout.activity_main);
        mCoordinatorLayoutView = findViewById(R.id.snackbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewpager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        if (mViewpager != null) {
            mViewpager.setOffscreenPageLimit(4);
        }
        setupViewPager();

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        if (tabLayout != null) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setupWithViewPager(mViewpager);
        }

        // cancel all open notifications
        NotificationManager notificationmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.cancel(0);
    }

    private void setupViewPager() {
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        adapter.addFrag(BibSummaryFragment.newInstance());
        adapter.addFrag(BibFeeFragment.newInstance());
        adapter.addFrag(LearningPlaceFragment.newInstance());
        mViewpager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // set title
            alertDialogBuilder.setTitle(R.string.action_logout);

            // set dialog message
            alertDialogBuilder.setMessage(R.string.menu_logout).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AuthenticatorUtils.removeAccount(getApplication());
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            return true;

        } else if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
            return true;
        } else if (id == R.id.action_extend) {
            generalExtension();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generalExtension() {
        if (NetworkTasks.isNetworkAvailable(this)) {
            new NetworkTasks.GeneralExtension(new AsyncStatus() {
                @Override
                public void result(boolean result) {
                    if (!result) {
                        Snackbar.make(mCoordinatorLayoutView, getApplication().getString(R.string.extention_failed), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mCoordinatorLayoutView, getApplication().getString(R.string.extention_success), Snackbar.LENGTH_LONG).show();
                        SyncUtils.triggerRefresh(mAccount, 1);
                    }
                }
            }, this, mAccount).execute();
        } else {
            Snackbar.make(mCoordinatorLayoutView, getApplication().getString(R.string.connection_failed), Snackbar.LENGTH_LONG)
                    .setAction(getApplication().getString(R.string.connection_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            generalExtension();
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAccount();
    }

    private void checkAccount() {
        Account account = AuthenticatorUtils.getAccount(this);

        if (account == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            mAccount = account;
            SyncUtils.createSyncAccount(mAccount);
            SyncUtils.triggerRefresh(mAccount, 0);
        }
    }

    @Override
    public void finish() {
        super.finish();

        // finish activity with fancy slide out animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}