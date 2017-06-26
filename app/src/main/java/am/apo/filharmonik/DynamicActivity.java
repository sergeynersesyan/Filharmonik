package am.apo.filharmonik;

import android.content.Intent;
import android.os.Bundle;

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

    @Override
    protected void onLanguageChange() {
        super.onLanguageChange();
        changePreviousActivityLanguage();
    }
}
