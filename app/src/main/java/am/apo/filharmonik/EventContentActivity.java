package am.apo.filharmonik;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


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
            setSubTitle(mJSONObject.getString(ApoContract.APO_JSON_TITLE));

            mActionFragment = new EventActionFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.action_container, mActionFragment)
                    .commit();

            mImageView = new ApoImageView(this);
            mTextView = (TextView)findViewById(R.id.event_text);

            mImageView.loadUrl(mJSONObject.getString(ApoContract.APO_JSON_PICTURE));
            mTextView.setText(mJSONObject.getString(ApoContract.APO_JSON_DESCRIPTION));
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
}
