package am.apo.filharmonik2;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SharedActionFragment extends Fragment {

    OnInfoClickListener mInfoClickListener;
    OnShareClickListener mShareClickListener;

    public SharedActionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shared_action, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mInfoClickListener = (OnInfoClickListener)activity;
        }
        catch(ClassCastException e)
        {
            mInfoClickListener = null;
        }
        try {
            mShareClickListener = (OnShareClickListener)activity;
        }
        catch(ClassCastException e)
        {
            mShareClickListener = null;
        }
      }

    @Override
    public void onDetach() {
        super.onDetach();
        mInfoClickListener = null;
        mShareClickListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageButton infoButton = (ImageButton)getActivity().findViewById(R.id.flip_button);
        if(null!=mInfoClickListener) {
            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInfoClickListener.onInfoClicked();
                }
            });
            infoButton.setVisibility(View.VISIBLE);
        }
        else
        {
            infoButton.setVisibility(View.GONE);
        }

        ImageButton shareButton = (ImageButton)getActivity().findViewById(R.id.share_button);
        if(null != mShareClickListener) {
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mShareClickListener.onShareClicked();
                }
            });
            shareButton.setVisibility(View.VISIBLE);
        }
        else
        {
            shareButton.setVisibility(View.GONE);
        }
    }

    public interface OnInfoClickListener{
        public void onInfoClicked();
    }

    public interface OnShareClickListener{
        public void onShareClicked();
    }
}
