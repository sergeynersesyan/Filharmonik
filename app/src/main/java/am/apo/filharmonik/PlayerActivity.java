package am.apo.filharmonik;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import org.json.JSONObject;


public class PlayerActivity extends ApoSectionActivity implements AdapterView.OnItemClickListener {

    PlayerGridAdapter mAdapter;
    ImageButton mAllButton;
    ImageButton mFavoritesButton;
    GridView mGridView;
    String mFilterText;
    boolean mFavoritesMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        setSectionID(getIntent().getStringExtra(ApoContract.APO_SECTION_ID));

        mGridView = (GridView)findViewById(R.id.player_grid);

        mGridView.setNumColumns(getGridColumns());

        mAllButton = (ImageButton)findViewById(R.id.filter_all_button);
        mFavoritesButton = (ImageButton)findViewById(R.id.filter_favorites_button);
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavoritesMode(false, true);

            }
        });
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavoritesMode(true, true);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mFavoritesMode = prefs.getBoolean(getString(getSectionID().equals(ApoContract.APO_MUSIC) ? R.string.favorites_mode_music : R.string.favorites_mode_video), false);
        setFavoritesMode(mFavoritesMode, false);

        mAdapter = null;
        mFilterText = null;

        EditText searchText = (EditText)findViewById(R.id.filter_text);
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (0 == start && 0 == count) {
                    mFilterText = null;
                } else {
                    mFilterText = s.toString();
                }
                if (null != mAdapter) {
                    mAdapter.setFilterText(mFilterText);
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null != mAdapter)
        {
            mAdapter.setFilterText(mFilterText);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadSection();
    }

    @Override
    public void onSectionReady(JSONObject obj) {
        super.onSectionReady(obj);

        mAdapter = new PlayerGridAdapter(PlayerActivity.this, obj, getSectionID().equals(ApoContract.APO_MUSIC));
        mAdapter.setFilterText(mFilterText);
        mGridView.setAdapter(mAdapter);
        mGridView.setClickable(true);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        JSONObject obj = (JSONObject)mAdapter.getItem(i);

        Intent playIntent = new Intent(this, PlayerPlayActivity.class);
        playIntent.putExtra(ApoContract.APO_JSON, obj.toString());
        playIntent.putExtra(ApoContract.APO_SECTION_ID, getSectionID());
        startActivity(playIntent);
    }

    void setFavoritesMode(boolean favoritesMode, boolean needUpdate)
    {
        mFavoritesMode = favoritesMode;
        mAllButton.setSelected(!mFavoritesMode);
        mFavoritesButton.setSelected(mFavoritesMode);

        if(needUpdate) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getString(getSectionID().equals(ApoContract.APO_MUSIC)  ? R.string.favorites_mode_music : R.string.favorites_mode_video), mFavoritesMode);
            editor.commit();
            if (null != mAdapter) {
                mAdapter.setFilterText(mFilterText);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
