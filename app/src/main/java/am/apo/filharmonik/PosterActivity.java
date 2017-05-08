package am.apo.filharmonik;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ViewAnimator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PosterActivity  extends ApoSectionActivity implements SharedActionFragment.OnInfoClickListener {

    private JSONObject mJSONObject;
    private DynamicContentFlipper mFlipper;
    SharedActionFragment mActionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        setSectionID(ApoContract.APO_POSTER);
        mFlipper = new DynamicContentFlipper(this);

        mActionFragment = new SharedActionFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.action_container, mActionFragment);
        ft.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadSection();
    }

    @Override
    public void onSectionReady(JSONObject obj) {
        super.onSectionReady(obj);

        try {
            mJSONObject = obj.getJSONArray("data").getJSONObject(0);

            setSubTitle(mJSONObject.getString(ApoContract.APO_JSON_TITLE));
            mFlipper.setCardInfo(mJSONObject.getString(ApoContract.APO_JSON_PICTURE),
                    mJSONObject.getString(ApoContract.APO_JSON_DESCRIPTION));
        }
        catch(JSONException e)
        {
            //Toast.
        }
    }

    @Override
    protected String onRequestShareText() {
        try {
            return mJSONObject.getString(ApoContract.APO_JSON_SOCIAL);
        }
        catch (Exception e){}

        return null;
    }

    @Override
    public void onInfoClicked() {
        mFlipper.flip();
    }
}
