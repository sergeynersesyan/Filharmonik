package am.apo.filharmonik;

import android.app.Fragment;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class ManagementActivity extends ApoSectionActivity implements StaticContentFlipper.OnArrowClickListener{
    ImageButton mBtnStructure;
    ImageButton mBtnRuzan;
    ImageButton mBtnArman;
    ImageButton mBtnDavit;
    StaticContentFlipper mFlipper;

    int mCurrentIndex;
    int INDEX_STRUCTURE = 0;
    int INDEX_RUZAN = 1;
    int INDEX_ARMAN = 2;
    int INDEX_DAVIT = 3;

    int subTitles[] = {R.string.SUB_TITLE_STRUCTURE, R.string.SUB_TITLE_RUZAN, R.string.SUB_TITLE_ARMAN, R.string.SUB_TITLE_DAVIT};
    int bigPics[] = {R.drawable.management_structure, R.drawable.management_big_1, R.drawable.management_big_2, R.drawable.management_big_3};
    int bigDesc[] = {R.string.structure_description, R.string.ruzan_description, R.string.arman_description, R.string.davit_description};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        setSectionID(ApoContract.APO_ABOUT);
        mCurrentIndex = 0;

        setMainTitle(getString(R.string.TITLE_MANAGEMENT));

        mBtnStructure = (ImageButton)findViewById(R.id.management_structure_button);
        mBtnRuzan = (ImageButton)findViewById(R.id.management_1_button);
        mBtnArman = (ImageButton)findViewById(R.id.management_2_button);
        mBtnDavit = (ImageButton)findViewById(R.id.management_3_button);

        mFlipper = new StaticContentFlipper(this);
        mFlipper.setCardInfo(bigPics[mCurrentIndex], getString(bigDesc[mCurrentIndex]));

        mBtnStructure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {             setActiveButton(INDEX_STRUCTURE);
            }
        });

        mBtnRuzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveButton(INDEX_RUZAN);
            }
        });

        mBtnArman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveButton(INDEX_ARMAN);
            }
        });

        mBtnDavit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveButton(INDEX_DAVIT);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setActiveButton(INDEX_STRUCTURE);
        mFlipper.setArrowClickListener(this);
    }

    @Override
    public void onBackClicked() {
        setActiveButton(mCurrentIndex - 1);
    }

    @Override
    public void onForwardClicked() {
        setActiveButton(mCurrentIndex + 1);
    }

    void setActiveButton(int index)
    {
        if(index < 0)
        {
            index = 0;
        }
        else if(index > INDEX_DAVIT)
        {
            index = INDEX_DAVIT;
        }
        mCurrentIndex = index;

        mBtnStructure.setSelected(INDEX_STRUCTURE==index);
        mBtnRuzan.setSelected(INDEX_RUZAN==index);
        mBtnArman.setSelected(INDEX_ARMAN==index);
        mBtnDavit.setSelected(INDEX_DAVIT==index);

        setSubTitle(getString(subTitles[index]));

        mFlipper.setCardInfo(bigPics[mCurrentIndex], getString(bigDesc[mCurrentIndex]));

    }
}
