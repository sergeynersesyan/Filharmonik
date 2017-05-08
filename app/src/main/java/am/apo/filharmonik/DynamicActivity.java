package am.apo.filharmonik;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;


public class DynamicActivity extends ApoSectionActivity implements ApoListFragment.OnApoListListener{

    protected ApoListFragment mApoListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        setSectionID(getIntent().getStringExtra(ApoContract.APO_SECTION_ID));

        mApoListFragment = new ApoListFragment();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.apo_list_container, mApoListFragment)
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

        Intent dynamicIntent = new Intent(this, mSectionID.equals(ApoContract.APO_EVENT) ? EventContentActivity.class : DynamicContentActivity.class);
        dynamicIntent.putExtra(ApoContract.APO_SECTION_ID, mSectionID);
        dynamicIntent.putExtra(ApoContract.APO_JSON, obj.toString());
        startActivity(dynamicIntent);
    }
}
