package am.apo.filharmonik2;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;


public class DynamicLargeActivity extends ApoSectionActivity implements ApoListFragment.OnApoListListener, ApoListFragment.OnApoListHandleListener {

    protected ApoListFragment mApoListFragment;
    protected JSONObject mJSONObject;
    protected ApoImageView mDynamicImageView;
    protected TextView mDynamicText;
    protected String mShareText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getStringExtra(ApoContract.APO_SECTION_ID).equals(ApoContract.APO_EVENT)) {
            setContentView(R.layout.activity_dynamic_large_event);
        }
        else {
            setContentView(R.layout.activity_dynamic_large);
        }

        setSectionID(getIntent().getStringExtra(ApoContract.APO_SECTION_ID));

        Fragment actionFragment = getSectionID().equals(ApoContract.APO_EVENT) ? new EventActionFragment() : new SharedActionFragment();
        mDynamicText = (TextView)findViewById(R.id.dynamic_description);
        mDynamicImageView = new ApoImageView(this);

        mApoListFragment = new ApoListFragment();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.apo_list_container, mApoListFragment)
                .add(R.id.action_container, actionFragment)
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadSection();
    }

    @Override
    public void onSectionReady(JSONObject obj) {
        super.onSectionReady(obj);

        mApoListFragment.setData(obj);
    }

    @Override
    public void onItemSelected(JSONObject obj) {

        mJSONObject = obj;

        try {
            String imgUrl = obj.getString(ApoContract.APO_JSON_PICTURE);
            String description = obj.getString(ApoContract.APO_JSON_DESCRIPTION);
            setSubTitle(obj.getString(ApoContract.APO_JSON_TITLE));

            mDynamicText.setText(description);
            mShareText = obj.getString(ApoContract.APO_JSON_SOCIAL);

            mDynamicImageView.loadUrl(imgUrl);
        }
        catch(JSONException e){}
    }

    @Override
    protected String onRequestShareText() {
        return mShareText;
    }

    @Override
    protected JSONObject onRequestCalendarObject() {
        return mJSONObject;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onHandlePressed(boolean show) {

        if(show) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(mApoListFragment);
            ft.commit();

            ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            ft.show(mApoListFragment);
            ft.commit();
        }
        else
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            ft.hide(mApoListFragment);
            ft.commit();
        }
    }

    @Override
    public void onAnimationFinished(boolean needShow) {

        if(needShow) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.show(mApoListFragment);
            ft.commit();
        }
    }
}
