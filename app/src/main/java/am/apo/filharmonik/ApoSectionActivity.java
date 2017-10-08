package am.apo.filharmonik;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by henrikgardishyan on 11/8/14.
 */
abstract public class ApoSectionActivity extends ApoFullScreenActivity implements SharedActionFragment.OnShareClickListener, EventActionFragment.OnShareClickListener, EventActionFragment.OnCalendarClickListener {

    protected String mSectionID;
    protected ImageButton mBackButton;
    protected ImageButton mHomeButton;
    protected ImageButton mSettingsButton;
    protected ImageButton mBrowserButton;
    protected ProgressBar mProgressBar;

    protected String mBrowserUrl;
    private ApoNetworking mNetworker;

    private final int[] mTitles = {R.string.TITLE_EVENTS, R.string.TITLE_NEWSLETTER,
            R.string.TITLE_REVIEWS, R.string.TITLE_POSTER,
            R.string.TITLE_PHOTOS, R.string.TITLE_VIDEO, R.string.TITLE_MUSIC};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        int sectionID = Integer.parseInt(mSectionID) - ApoContract.APO_BASE;
        if (sectionID >= 0 && sectionID < mTitles.length) {
            setMainTitle(getString(mTitles[sectionID]));
        }

        mBackButton = (ImageButton) findViewById(R.id.navbar_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final RelativeLayout flagsPopup = (RelativeLayout) findViewById(R.id.flags_popup);
        mSettingsButton = (ImageButton) findViewById(R.id.settings_button_top);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagsPopup.setVisibility(flagsPopup.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        ImageButton armButton = (ImageButton) findViewById(R.id.flag_arm);
        armButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApoUtils.sharedUtils(getApplicationContext()).setLanguage(getString(R.string.pref_lang_arm));
                setLanguageConfig();
                flagsPopup.setVisibility(View.GONE);
            }
        });
        ImageButton engButton = (ImageButton) findViewById(R.id.flag_eng);
        engButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApoUtils.sharedUtils(getApplicationContext()).setLanguage(getString(R.string.pref_lang_eng));
                setLanguageConfig();
                flagsPopup.setVisibility(View.GONE);
            }
        });


        mHomeButton = (ImageButton) findViewById(R.id.navbar_home);
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        mBrowserButton = (ImageButton) findViewById(R.id.navbar_browser);
        mBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mBrowserUrl) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBrowserUrl));
                    startActivity(browserIntent);
                }
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.apo_progress_bar);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setLanguageConfig() {
        String currLang = ApoUtils.sharedUtils(this).getLanguage();
        findViewById(R.id.flag_arm).setSelected(currLang.equals(getString(R.string.pref_lang_arm)));
        findViewById(R.id.flag_eng).setSelected(currLang.equals(getString(R.string.pref_lang_eng)));

        Locale mLocale = new Locale(currLang.equals(getString(R.string.pref_lang_arm)) ? "hy" : "en");
        Locale.setDefault(mLocale);
        Configuration config = new Configuration();
        config.locale = mLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        onLanguageChange();
    }



    public void loadSection() {
        Log.e(TAG, "Loading section with resolution: " + getString(R.string.device_res_id));
        mNetworker = new ApoNetworking(this, mSectionID, getString(R.string.device_res_id)) {

            @Override
            void onOK(JSONObject obj) {
                if (mProgressBar != null)
                mProgressBar.setVisibility(View.GONE);
                onSectionReady(obj);
            }

            @Override
            void onError(String errStr) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), errStr, Toast.LENGTH_LONG).show();

                    JSONObject lastSaved = getLastSaved();
                    if (null != lastSaved) {
                        onSectionReady(lastSaved);
                    }
                } catch (Exception e) {
                }
            }
        };

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mNetworker.requestSection();
    }

    public void onSectionReady(JSONObject obj) {
    }

    public void setSectionID(String sectionID) {
        mSectionID = sectionID;
    }

    public String getSectionID() {
        return mSectionID;
    }

    public void setMainTitle(String title) {
        TextView subTitleView = (TextView) findViewById(R.id.main_title);
        subTitleView.setText(title);
    }

    public void setSubTitle(String subTitle) {
        TextView subTitleView = (TextView) findViewById(R.id.sub_title);
        subTitleView.setText(subTitle);
    }

    @Override
    public void onShareClicked() {
        String shareText = onRequestShareText();
        String imageURL = null;
        try {
            imageURL = onRequestCalendarObject().getString(ApoContract.APO_JSON_PICTURE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != shareText) {

            Intent emailIntent = new Intent();
            emailIntent.setAction(Intent.ACTION_SEND);
            // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
            emailIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.SUB_TITLE_ANPO));
            emailIntent.setType("message/rfc822");

            PackageManager pm = getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");

            Intent openInChooser = Intent.createChooser(emailIntent, getString(R.string.SUB_TITLE_SOCIAL));

            List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
            for (int i = 0; i < resInfo.size(); i++) {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ResolveInfo ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName;
                if (packageName.contains("android.email")) {
                    emailIntent.setPackage(packageName);
                } else /*if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms"))*/ {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareText);

/*                    if(packageName.contains("twitter")) {
                        intent.putExtra(Intent.EXTRA_TEXT, "twitter text");
                    } else if(packageName.contains("facebook")) {
                        // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                        // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                        // will show the <meta content ="..."> text from that page with our link in Facebook.
                        intent.putExtra(Intent.EXTRA_TEXT, "facebook text");
                    } else if(packageName.contains("mms")) {
                        intent.putExtra(Intent.EXTRA_TEXT, "sms text");
                    }*/
                    intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                }
            }

            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            startActivity(openInChooser);

        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCalendarClicked() {

        JSONObject obj = onRequestCalendarObject();
        if (null != obj) {
            try {
                String dateStr = obj.getString(ApoContract.APO_JSON_DATE);
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                input.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date eventDate = input.parse(dateStr);                 // parse input

                Calendar beginTime = Calendar.getInstance();
                beginTime.setTimeZone(TimeZone.getDefault());
                beginTime.setTime(eventDate);
                Calendar endTime = Calendar.getInstance();
                endTime.setTimeZone(TimeZone.getDefault());
                endTime.setTime(eventDate);
                endTime.add(Calendar.HOUR, 2);

                Intent intent = new Intent(Intent.ACTION_EDIT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, obj.getString(ApoContract.APO_JSON_TITLE))
                        .putExtra(CalendarContract.Events.DESCRIPTION, obj.getString(ApoContract.APO_JSON_DESCRIPTION));

                intent.setType("vnd.android.cursor.item/event");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    protected int getGridColumns() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels - 2 * (int) getResources().getDimension(R.dimen.dynamic_thumb_grid_margin);
        int itemWidth = (int) getResources().getDimension(R.dimen.dynamic_thumb_width) + (int) getResources().getDimension(R.dimen.dynamic_thumb_inter);
        int numColumns = screenWidth / itemWidth;

        Log.e(TAG, "Screen width: " + screenWidth + ", itemWidth: " + itemWidth + ", numColumns: " + numColumns);

        return numColumns;
    }

    protected String onRequestShareText() {
        return null;
    }

    protected JSONObject onRequestCalendarObject() {
        return null;
    }

    @Override
    protected void onLanguageChange() {
        super.onLanguageChange();
        changePreviousActivityLanguage();
    }
}

