package am.apo.filharmonik;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class EventContentActivity extends ApoSectionActivity implements EventActionFragment.OnCalendarClickListener {

    private EventActionFragment mActionFragment;
    private ApoImageView mImageView;
    private TextView mTextView;
    private JSONObject mJSONObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_content);
        setSectionID(ApoContract.APO_EVENT);
        try{
            mJSONObject = new JSONObject(getIntent().getStringExtra(ApoContract.APO_JSON));

            mActionFragment = new EventActionFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.action_container, mActionFragment)
                    .commit();

            mImageView = new ApoImageView(this);
            mTextView = (TextView)findViewById(R.id.event_text);

            setData(mJSONObject);
        }
        catch (JSONException e){}
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
    protected JSONObject onRequestCalendarObject() {
        return mJSONObject;
    }

    @Override
    protected void onLanguageChange() {
        loadSection();
        changePreviousActivityLanguage();
    }

    @Override
    public void onSectionReady(JSONObject obj) {
        super.onSectionReady(obj);
        try {
            JSONArray jsonData = obj.getJSONArray("data");
            for (int i = 0; i < jsonData.length(); i++) {
                if (jsonData.getJSONObject(i).getInt("id") == mJSONObject.getInt("id")) {
                    mJSONObject = jsonData.getJSONObject(i);
                    setData(mJSONObject);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setData (JSONObject obj) throws JSONException {
        setSubTitle(obj.getString(ApoContract.APO_JSON_TITLE));
        mImageView.loadUrl(obj.getString(ApoContract.APO_JSON_PICTURE));
        mTextView.setText(obj.getString(ApoContract.APO_JSON_DESCRIPTION));
    }

}
