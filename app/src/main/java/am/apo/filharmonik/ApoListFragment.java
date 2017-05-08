package am.apo.filharmonik;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.ListFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;


import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link am.apo.filharmonik.ApoListFragment.OnApoListListener}
 * interface.
 */
public class ApoListFragment extends ListFragment {

    private final String CD_SUFFIX = "#cd";
    protected ImageButton mCDButton;
    protected ImageButton mConcertButton;
    protected boolean mCDMode;
    protected String mRequiredSuffix;
    protected String mFilteredSuffix;
    protected JSONObject mData;

    private OnApoListListener mListListener;
    private OnApoListHandleListener mHandleListener;
    private ApoListAdapter mApoListAdapter;
    private int mDisplayWidth;
    private boolean mHidden;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ApoListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_apo_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHidden = false;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListListener = (OnApoListListener) activity;
        }
        catch (ClassCastException e) {
        }
        try {
            mHandleListener = (OnApoListHandleListener) activity;
        }
        catch (ClassCastException e) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListListener = null;
        mHandleListener = null;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {

        if(0==nextAnim)
        {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }

        try {
            mDisplayWidth = getActivity().findViewById(R.id.hide_part).getWidth();
//            Log.e("FFF", "display width: " + mDisplayWidth + ",transit: " + transit + ", enter: " + enter + ", netxAnim: " + nextAnim + ", slide_in: " + R.anim.slide_in + ", slide_out: " + R.anim.slide_out);

            Animator animator = null;
            if (R.anim.slide_out == nextAnim) {
                animator = ObjectAnimator.ofFloat(this, "translationX", 0, -mDisplayWidth);
                mHidden = true;
            } else {
                animator = ObjectAnimator.ofFloat(this, "translationX", -mDisplayWidth, 0);
                mHidden = false;
            }

            animator.setDuration(400);

            if (null != animator && null != mHandleListener) {
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mHandleListener.onAnimationFinished(mHidden);
                    }
                });
            }

            return animator;
        }
        catch(Exception e){}

        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ApoSectionActivity activity = (ApoSectionActivity)getActivity();
        mApoListAdapter = new ApoListAdapter(getActivity(), activity.getSectionID());
        setListAdapter(mApoListAdapter);

        if(null != mHandleListener) {
            ImageButton handleButton = (ImageButton) getActivity().findViewById(R.id.list_handle);
            handleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHandleListener.onHandlePressed(mHidden);
                }
            });
        }

        if(activity.getSectionID().equals(ApoContract.APO_REVIEW))
        {
            RelativeLayout filterLayout = (RelativeLayout)activity.findViewById(R.id.filter_layout);
            filterLayout.setVisibility(View.VISIBLE);

            mCDButton = (ImageButton)activity.findViewById(R.id.filter_cd_button);
            mCDButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCDMode(true, true);
                }
            });
            mConcertButton = (ImageButton)activity.findViewById(R.id.filter_concert_button);
            mConcertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCDMode(false, true);
                }
            });

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            mCDMode = prefs.getBoolean(getString(R.string.review_cd_mode), false);
            setCDMode(mCDMode, false);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListListener) {
            mListListener.onItemSelected((JSONObject) mApoListAdapter.getItem(position));
            mApoListAdapter.selectItem(position);
        }
    }

    public void setData(JSONObject jsonObj)
    {
        mData = jsonObj;
        updateAdapterData();
    }

    protected void updateAdapterData()
    {
        if(null != mApoListAdapter && null != mListListener) {

            JSONObject obj = mApoListAdapter.updateData(mData, mRequiredSuffix, mFilteredSuffix);
            if(null != obj && !getResources().getBoolean(R.bool.device_is_phone)) {
                mListListener.onItemSelected(obj);
            }
        }
    }

    protected void setCDMode(boolean mode, boolean needUpdate)
    {
        mCDMode = mode;
        mCDButton.setSelected(mCDMode);
        mConcertButton.setSelected(!mCDMode);

        mRequiredSuffix = mCDMode ? CD_SUFFIX : null;
        mFilteredSuffix = mCDMode ? null : CD_SUFFIX;

        if(needUpdate) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getString(R.string.review_cd_mode), mCDMode);
            editor.commit();

            updateAdapterData();
        }
    }

    public interface OnApoListListener {
        public void onItemSelected(JSONObject obj);
    }

    public interface OnApoListHandleListener {
        public void onHandlePressed(boolean show);
        public void onAnimationFinished(boolean needShow);
    }
}
