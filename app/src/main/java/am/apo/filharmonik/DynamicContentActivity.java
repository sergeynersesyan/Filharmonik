package am.apo.filharmonik;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.CalendarContract;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DynamicContentActivity extends ApoSectionActivity implements SharedActionFragment.OnInfoClickListener {

    private DynamicContentFlipper mFlipper;
    SharedActionFragment mActionFragment;
    private JSONObject mJSONObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_content);
        setSectionID(getIntent().getStringExtra(ApoContract.APO_SECTION_ID));

        mFlipper = new DynamicContentFlipper(this);
        mActionFragment = new SharedActionFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.action_container, mActionFragment);
        ft.commit();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        try {
            mJSONObject = new JSONObject(getIntent().getStringExtra(ApoContract.APO_JSON));
            setData(mJSONObject);
        } catch (Exception e) {
        }
    }

    @Override
    protected String onRequestShareText() {
        try {
            return mJSONObject.getString(ApoContract.APO_JSON_SOCIAL);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onLanguageChange() {
        loadSection();
    }

    @Override
    public void onInfoClicked() {
        mFlipper.flip();
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

    private void setData(JSONObject jsonData) throws JSONException {
        setSubTitle(jsonData.getString(ApoContract.APO_JSON_TITLE));
        mFlipper.setCardInfo(jsonData.getString(ApoContract.APO_JSON_PICTURE),
                jsonData.getString(ApoContract.APO_JSON_DESCRIPTION));


    }
}
