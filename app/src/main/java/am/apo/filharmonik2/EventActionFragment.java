package am.apo.filharmonik2;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class EventActionFragment extends Fragment {

    OnShareClickListener mShareClickListener;
    OnCalendarClickListener mCalendarClickListener;

    public EventActionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_action, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mShareClickListener = (OnShareClickListener)activity;
        }
        catch(ClassCastException e)
        {
            mShareClickListener = null;
        }
        try {
            mCalendarClickListener = (OnCalendarClickListener)activity;
        }
        catch(ClassCastException e)
        {
            mCalendarClickListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCalendarClickListener = null;
        mShareClickListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

        ImageButton calendarButton = (ImageButton)getActivity().findViewById(R.id.calendar_button);

        if(null != mCalendarClickListener && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
            calendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCalendarClickListener.onCalendarClicked();
                }
            });
            calendarButton.setVisibility(View.VISIBLE);
        }
        else
        {
            calendarButton.setVisibility(View.GONE);
        }
    }

    public interface OnCalendarClickListener{
        public void onCalendarClicked();
    }

    public interface OnShareClickListener{
        public void onShareClicked();
    }
}
