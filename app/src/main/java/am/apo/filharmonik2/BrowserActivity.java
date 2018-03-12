package am.apo.filharmonik2;

import android.os.Bundle;
import android.view.View;


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
