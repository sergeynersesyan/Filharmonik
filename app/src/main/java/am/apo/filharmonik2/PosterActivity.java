package am.apo.filharmonik2;

import android.app.FragmentTransaction;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;


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
