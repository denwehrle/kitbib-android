package com.denwehrle.kitbib.features.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.denwehrle.kitbib.BuildConfig;
import com.denwehrle.kitbib.R;
import com.denwehrle.kitbib.utils.RecentTasksStyler;

/**
 * @author Dennis Wehrle
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecentTasksStyler.styleRecentTasksEntry(this);

        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_up);
        }

        ((TextView) findViewById(R.id.about_version)).setText("v" + BuildConfig.VERSION_NAME);
        ((TextView) findViewById(R.id.about_licence)).setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();

        // finish activity with fancy slide out animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}