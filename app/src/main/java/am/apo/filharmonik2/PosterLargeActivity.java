package am.apo.filharmonik2;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

public class PosterLargeActivity extends ApoSectionActivity implements SharedActionFragment.OnShareClickListener {

    private SharedActionFragment mActionFragment;
    private JSONObject mJSONObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        setSectionID(ApoContract.APO_POSTER);

        mActionFragment = new SharedActionFragment();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.action_container, mActionFragment)
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

        try {
            mJSONObject = obj.getJSONArray("data").getJSONObject(0);

            String imgUrl = mJSONObject.getString(ApoContract.APO_JSON_PICTURE);
            String description = mJSONObject.getString(ApoContract.APO_JSON_DESCRIPTION);

            ApoImageView posterImage = new ApoImageView(this);
            posterImage.loadUrl(imgUrl);

            TextView descriptionView = (TextView)findViewById(R.id.poster_description);
            descriptionView.setText(description);

            setSubTitle(mJSONObject.getString(ApoContract.APO_JSON_TITLE));
        }
        catch(Exception e) { }
    }

    @Override
    protected String onRequestShareText() {
        try {
            return mJSONObject.getString(ApoContract.APO_JSON_SOCIAL);
        }
        catch (Exception e){}

        return null;
    }
}
