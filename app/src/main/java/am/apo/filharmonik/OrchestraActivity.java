package am.apo.filharmonik;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class OrchestraActivity extends ApoSectionActivity {

    private ImageButton mBtnMaestro;
    private ImageButton mBtnMusicians;
    private ImageButton mBtnPostMaestros;
    private ImageButton mBtnGuests;
    private ImageButton mBtnLocals;
    private StaticContentFlipper mFlipper;
    private OrchestraMembersFragment mMembersFragment;
    private RelativeLayout mFlipperLayout;
    private FrameLayout mBrowserLayout;

    final int INDEX_MAESTRO = 0;
    final int INDEX_MUSICIANS = 1;
    final int INDEX_POST_MAESTROS = 2;
    final int INDEX_GUESTS = 3;
    final int INDEX_LOCALS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orchestra);
        setSectionID(ApoContract.APO_ABOUT);

        setMainTitle(getString(R.string.TITLE_ORCHESTRA));
        setSubTitle(getString(R.string.SUB_TITLE_MAESTRO));

        mFlipperLayout = (RelativeLayout)findViewById(R.id.static_flipper);
        mBrowserLayout = (FrameLayout)findViewById(R.id.card_container);
        mFlipper = new StaticContentFlipper(this);

        mBtnMaestro = (ImageButton)findViewById(R.id.maestro_button);
        mBtnMusicians = (ImageButton)findViewById(R.id.musicians_button);
        mBtnPostMaestros = (ImageButton)findViewById(R.id.post_maestro_button);
        mBtnGuests = (ImageButton)findViewById(R.id.guests_button);
        mBtnLocals = (ImageButton)findViewById(R.id.locals_button);


        mBtnMaestro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBrowserLayout.setVisibility(View.GONE);
                mBrowserButton.setVisibility(View.GONE);
                mFlipperLayout.setVisibility(View.VISIBLE);

                mBrowserUrl = null;
                setSubTitle(getString(R.string.SUB_TITLE_MAESTRO));
                resetButtons(INDEX_MAESTRO);
            }
        });

        mBtnMusicians.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink(getString(R.string.ORCHESTRA_URL_1), getString(R.string.SUB_TITLE_MUSICIANS), INDEX_MUSICIANS);
            }
        });

        mBtnPostMaestros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink(getString(R.string.ORCHESTRA_URL_2), getString(R.string.SUB_TITLE_POST_MAESTROS), INDEX_POST_MAESTROS);
            }
        });

        mBtnGuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink(getString(R.string.ORCHESTRA_URL_3), getString(R.string.SUB_TITLE_GUESTS), INDEX_GUESTS);
            }
        });

        mBtnLocals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink(getString(R.string.ORCHESTRA_URL_4), getString(R.string.SUB_TITLE_LOCALS), INDEX_LOCALS);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        resetButtons(INDEX_MAESTRO);
        mFlipper.setCardInfo(R.drawable.maestro, getString(R.string.maestro_description));
    }

    void resetButtons(int index)
    {
        mBtnMaestro.setSelected(INDEX_MAESTRO==index);
        mBtnMusicians.setSelected(INDEX_MUSICIANS==index);
        mBtnPostMaestros.setSelected(INDEX_POST_MAESTROS==index);
        mBtnGuests.setSelected(INDEX_GUESTS==index);
        mBtnLocals.setSelected(INDEX_LOCALS==index);
    }

    void openLink(String url, String subTitle, int index)
    {
        mBrowserUrl = url;
        mFlipperLayout.setVisibility(View.GONE);
        mBrowserButton.setVisibility(View.VISIBLE);
        setSubTitle(subTitle);
        resetButtons(index);

        boolean replace = false;
        if(null != mMembersFragment)
        {
            replace = true;
        }
        mMembersFragment = new OrchestraMembersFragment();

        Bundle memberBundle = new Bundle();
        memberBundle.putString(OrchestraMembersFragment.MEMBER_URL, url);
        mMembersFragment.setArguments(memberBundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(replace) {
            ft.replace(R.id.card_container, mMembersFragment);
        }
        else
        {
            ft.add(R.id.card_container, mMembersFragment);
        }
        ft.commit();

        mBrowserLayout.setVisibility(View.VISIBLE);
    }
}
