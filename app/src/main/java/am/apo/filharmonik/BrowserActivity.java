package am.apo.filharmonik;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;


public class BrowserActivity extends ApoSectionActivity {

    ApoWebView mBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        setSectionID(ApoContract.APO_BROWSER);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mBrowser = new ApoWebView(this, true, false);
        mBrowserUrl = getIntent().getStringExtra(getString(R.string.link_url));
        setMainTitle(getIntent().getStringExtra(getString(R.string.link_title)));
        setSubTitle(getString(R.string.SUB_TITLE_ANPO));

        mBrowserButton.setVisibility(View.VISIBLE);
        mBrowser.loadUrl(mBrowserUrl);
    }
}
