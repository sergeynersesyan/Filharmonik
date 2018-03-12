package am.apo.filharmonik2;

import android.app.Activity;
import android.net.ParseException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by henrikgardishyan on 12/8/14.
 */
public class ApoListAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<JSONObject> mFinalList;
    private String mSectionID;
    private int mSelectedIndex;

    public ApoListAdapter(Activity activity, String sectionID) {
        mActivity = activity;
        mSectionID = sectionID;
        mFinalList = null;
        mSelectedIndex = 0;
    }

    @Override
    public int getCount() {
        return null == mFinalList ? 0 : mFinalList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {

       return null == mFinalList ? 0 : mFinalList.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        try {
            View lView = view;
            if (lView == null) {
                lView = LayoutInflater.from(mActivity).inflate(R.layout.dynamic_list_item, viewGroup, false);
            }

            final TextView titleView = (TextView) lView.findViewById(R.id.dynamic_title);
            final RelativeLayout dateArea = (RelativeLayout) lView.findViewById(R.id.date_area);

            final String title = mFinalList.get(i).getString(ApoContract.APO_JSON_TITLE);
            titleView.setText(title);

            titleView.setTextColor(mSelectedIndex==i ? mActivity.getResources().getColor(R.color.apo_yellow) : mActivity.getResources().getColor(android.R.color.white));

            if(mSectionID.equals(ApoContract.APO_EVENT)) {
                final String dateStr = mFinalList.get(i).getString(ApoContract.APO_JSON_DATE);

                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd.MM\nyyyy");
                try {
                    Date eventDate = input.parse(dateStr);                 // parse input
                    final TextView dateView = (TextView) lView.findViewById(R.id.date_view);
                    dateView.setText(output.format(eventDate));
                    dateView.setTextColor(mSelectedIndex==i ? mActivity.getResources().getColor(R.color.apo_yellow) : mActivity.getResources().getColor(android.R.color.white));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                dateArea.setVisibility(View.GONE);
            }

            return lView;

        } catch (Exception e) { }

        return null;
    }

    public JSONObject updateData(JSONObject jsonObj, String requiredSuffix, String filteredSuffix)
    {
        mSelectedIndex = 0;

        try {
            JSONArray jsonData = jsonObj.getJSONArray("data");

            mFinalList = new ArrayList<JSONObject>();

            for(int i = 0; i < jsonData.length(); ++i)
            {
                JSONObject obj = jsonData.getJSONObject(i);
                String objName = obj.getString(ApoContract.APO_JSON_NAME);
                if(((null != filteredSuffix && !objName.contains(filteredSuffix)) || null==filteredSuffix) &&
                   ((null != requiredSuffix && objName.contains(requiredSuffix)) || null==requiredSuffix))
                {
                    mFinalList.add(obj);
                }
            }

            Collections.sort(mFinalList, new ApoJsonComparator());

            notifyDataSetChanged();

            return mFinalList.get(mSelectedIndex);
        } catch (Exception e) {
            mFinalList = null;
        }
        return null;
    }

    public void selectItem(int index)
    {
        mSelectedIndex = index;
        notifyDataSetChanged();
    }

    public class ApoJsonComparator implements Comparator<JSONObject>
    {
        public int compare(JSONObject left, JSONObject right) {

            try {
                String name1 = left.getString(ApoContract.APO_JSON_NAME);
                String name2 = right.getString(ApoContract.APO_JSON_NAME);

                String tagArr1[] = name1.split("#");
                String tagArr2[] = name2.split("#");

  //              Log.e("BAB", "comparing: " + name1 + " vs " + name2 + ", tags1: " + tagArr1[0] + ", l:" + tagArr1.length + ", tags2" + tagArr2[0] + ",l: " + tagArr2.length);

                if(tagArr1.length > 1 && tagArr2.length > 1)
                {
                    try {
                        int index1 = NumberFormat.getInstance().parse(tagArr1[1]).intValue();
                        int index2 = NumberFormat.getInstance().parse(tagArr2[1]).intValue();
//                        Log.e("BAB", "comparing indexes: " + index1 + " vs " + index2);
                        return index2 - index1;
                    }
                    catch(Exception e){}
                }
                else if(tagArr1.length==tagArr2.length) {
                    return name1.compareTo(name2);
                }
                return tagArr2.length - tagArr1.length;
            }
            catch (JSONException e) {}

            return 0;
        }
    }
}