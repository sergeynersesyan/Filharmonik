package am.apo.filharmonik;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrchestraMembersFragment extends Fragment {

    public static String MEMBER_URL = "member_url";
    private ApoWebView mBrowser;

    public OrchestraMembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orchestra_members, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e("TAG", "orchestra on activity created");

        mBrowser = new ApoWebView(getActivity(), true, false);
        mBrowser.loadUrl(getArguments().getString(MEMBER_URL));
    }

    @Override
    public void onDestroy() {
        mBrowser.finish();
        super.onDestroy();
    }
}
