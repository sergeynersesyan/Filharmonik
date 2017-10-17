package am.apo.filharmonik;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Locale;


public class MainMenuActivity extends ApoFullScreenActivity {

    private final int FACEBOOK_LINK_INDEX = 0;
    private final int TWITTER_LINK_INDEX = 1;
    private final int INSTA_LINK_INDEX = 2;
    private final int BLOG_LINK_INDEX = 3;
    private final int AMAZON_LINK_INDEX = 4;
    private final int YOUTUBE_LINK_INDEX = 5;
    private final int CDBABY_LINK_INDEX = 6;
    private final int ALLMUSIC_LINK_INDEX = 7;
    private final int STREAM_LINK_INDEX = 8;

    private Locale mLocale;

    LinkInfo [] mLinkInfoArr;

    protected class LinkInfo{
        String mPackageName;
        int mAppURI;
        int mURL;

        public LinkInfo(String packageName, int uriID, int urlID)
        {
            mPackageName = packageName;
            mAppURI = uriID;
            mURL = urlID;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apo_menu);

        String pushSectionID = getIntent().getStringExtra(ApoContract.APO_PUSH_SECTION_ID);

        if(null != pushSectionID)
        {
            Toast.makeText(getApplicationContext(), getIntent().getStringExtra(ApoContract.APO_PUSH_BODY), Toast.LENGTH_LONG).show();
            openSection(pushSectionID);
        }

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RelativeLayout flagsPopup = (RelativeLayout) findViewById(R.id.flags_popup);
                flagsPopup.setVisibility(View.VISIBLE);
            }
        });

        ImageButton armButton = (ImageButton) findViewById(R.id.flag_arm);
        armButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApoUtils.sharedUtils(MainMenuActivity.this).setLanguage(getString(R.string.pref_lang_arm));
                fillMenu();
            }
        });
        ImageButton engButton = (ImageButton) findViewById(R.id.flag_eng);
        engButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApoUtils.sharedUtils(MainMenuActivity.this).setLanguage(getString(R.string.pref_lang_eng));
                fillMenu();
            }
        });

        ImageButton sponsorsButton = (ImageButton) findViewById(R.id.sponsors_button);
        sponsorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View sponsorsPopup = findViewById(R.id.sponsors_popup);
                sponsorsPopup.setVisibility(View.VISIBLE);
            }
        });

        ImageButton ticketsButton = (ImageButton) findViewById(R.id.tickets_button);
        ticketsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(getApplicationContext(), BrowserActivity.class);
                browserIntent.putExtra(getString(R.string.link_url), "https://www.tomsarkgh.am/hy/event/26413/Joint-Concert-of-the-Jenaer-Ph.html");
                browserIntent.putExtra(getString(R.string.link_title), "Tickets");
                startActivity(browserIntent);
            }
        });



        fillMenu();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.e(TAG, "On new intent called!!!");
        String pushSectionID = intent.getStringExtra(ApoContract.APO_PUSH_SECTION_ID);
        if(null != pushSectionID)
        {
            Log.e(TAG, "menu Push received: " + pushSectionID + ", body: " + intent.getStringExtra(ApoContract.APO_PUSH_BODY));
            Toast.makeText(getApplicationContext(), intent.getStringExtra(ApoContract.APO_PUSH_BODY), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onContentClick() {
        RelativeLayout flagsPopup = (RelativeLayout) findViewById(R.id.flags_popup);
        View sponsorsPopup = findViewById(R.id.sponsors_popup);
        if(View.VISIBLE==flagsPopup.getVisibility() || View.VISIBLE==sponsorsPopup.getVisibility())
        {
            flagsPopup.setVisibility(View.GONE);
            sponsorsPopup.setVisibility(View.GONE);
            return false;
        }

        return super.onContentClick();
    }

    void fillMenu()
    {
        String currLang = ApoUtils.sharedUtils(this).getLanguage();
        findViewById(R.id.flag_arm).setSelected(currLang.equals(getString(R.string.pref_lang_arm)));
        findViewById(R.id.flag_eng).setSelected(currLang.equals(getString(R.string.pref_lang_eng)));

        mLocale = new Locale(currLang.equals(getString(R.string.pref_lang_arm)) ? "hy" : "en");
        Locale.setDefault(mLocale);
        Configuration config = new Configuration();
        config.locale = mLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        mLinkInfoArr = new LinkInfo[] {
                new LinkInfo("com.facebook.katana", R.string.FACEBOOK_APP_URI, R.string.FACEBOOK_URL),
                new LinkInfo("com.twitter.android", R.string.TWITTER_APP_URI, R.string.TWITTER_URL),
                new LinkInfo(null, R.string.ALBUMS_APP_URI, R.string.INSTAGRAM_URL),
                new LinkInfo(null, R.string.BLOG_APP_URI, R.string.BLOG_URL),
                new LinkInfo(null, R.string.AMAZON_APP_URI, R.string.AMAZON_URL),
                new LinkInfo("com.google.android.youtube", R.string.YOUTUBE_APP_URI, R.string.YOUTUBE_URL),
                new LinkInfo(null, R.string.NAXOS_APP_URI, R.string.CDBABY_URL),
                new LinkInfo(null, R.string.ALLMUSIC_APP_URI, R.string.ALLMUSIC_URL),
                new LinkInfo("com.google.android.youtube", R.string.ALLMUSIC_APP_URI, R.string.LIVE_STREAM_URL),
        };

        ImageButton aboutButton = (ImageButton) findViewById(R.id.about_button);
        aboutButton.setImageDrawable(getResources().getDrawable(R.drawable.about_button));
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent = new Intent(MainMenuActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });

        ImageButton eventsButton = (ImageButton) findViewById(R.id.events_button);
        eventsButton.setImageDrawable(getResources().getDrawable(R.drawable.events_button));
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSection(ApoContract.APO_EVENT);
            }
        });

        ImageButton posterButton = (ImageButton) findViewById(R.id.poster_button);
        posterButton.setImageDrawable(getResources().getDrawable(R.drawable.poster_button));
        posterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSection(ApoContract.APO_POSTER);
            }
        });

        ImageButton newsletterButton = (ImageButton) findViewById(R.id.newsletter_button);
        newsletterButton.setImageDrawable(getResources().getDrawable(R.drawable.newsletter_button));
        newsletterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSection(ApoContract.APO_NEWSLETTER);
            }
        });

        ImageButton reviewsButton = (ImageButton) findViewById(R.id.reviews_button);
        reviewsButton.setImageDrawable(getResources().getDrawable(R.drawable.reviews_button));
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSection(ApoContract.APO_REVIEW);
            }
        });

        ImageButton photosButton = (ImageButton) findViewById(R.id.photos_button);
        photosButton.setImageDrawable(getResources().getDrawable(R.drawable.photos_button));
        photosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSection(ApoContract.APO_PHOTO);
            }
        });

        ImageButton musicButton = (ImageButton) findViewById(R.id.music_button);
        musicButton.setImageDrawable(getResources().getDrawable(R.drawable.music_button));
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSection(ApoContract.APO_MUSIC);
            }
        });

        ImageButton videoButton = (ImageButton) findViewById(R.id.video_button);
        videoButton.setImageDrawable(getResources().getDrawable(R.drawable.video_button));
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSection(ApoContract.APO_VIDEO);
            }
        });

        ImageButton languageButton = (ImageButton) findViewById(R.id.settings_button);
        languageButton.setImageDrawable(getResources().getDrawable(R.drawable.settings_button));

        ImageButton sponsors = (ImageButton) findViewById(R.id.sponsors_button);
        sponsors.setImageDrawable(getResources().getDrawable(R.drawable.sponsors_button));

        ImageButton tickets = (ImageButton) findViewById(R.id.tickets_button);
        tickets.setImageDrawable(getResources().getDrawable(R.drawable.buy_tickets_button));

        ImageButton onlineStream = (ImageButton) findViewById(R.id.stream_button);
        onlineStream.setImageDrawable(getResources().getDrawable(R.drawable.online_btn));

        ImageView ministry = (ImageView) findViewById(R.id.logo_img);
        ministry.setImageDrawable(getResources().getDrawable(R.drawable.ministry));

        ImageView socialLabel = (ImageView)findViewById(R.id.social_label);
        socialLabel.setImageDrawable(getResources().getDrawable(R.drawable.menu_social));

        setLinkHandler(R.id.facebook_button, FACEBOOK_LINK_INDEX, getString(R.string.TITLE_FACEBOOK));
        setLinkHandler(R.id.twitter_button, TWITTER_LINK_INDEX, getString(R.string.TITLE_TWITTER));
        setLinkHandler(R.id.instagram_button, INSTA_LINK_INDEX, getString(R.string.TITLE_INSTA));
        setLinkHandler(R.id.blog_button, BLOG_LINK_INDEX, getString(R.string.TITLE_BLOG));
        setLinkHandler(R.id.amazon_button, AMAZON_LINK_INDEX, getString(R.string.TITLE_AMAZON));
        setLinkHandler(R.id.youtube_button, YOUTUBE_LINK_INDEX, getString(R.string.TITLE_YOUTUBE));
        setLinkHandler(R.id.cdbaby_button, CDBABY_LINK_INDEX, getString(R.string.TITLE_CDBABY));
        setLinkHandler(R.id.allmusic_button, ALLMUSIC_LINK_INDEX, getString(R.string.TITLE_ALLMUSIC));
        setLinkHandler(R.id.stream_button, STREAM_LINK_INDEX, "LIVE NOW");
    }

    private void setLinkHandler(int buttonID, final int index, final String title)
    {
        ImageButton linkButton = (ImageButton) findViewById(buttonID);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink(index, title);
            }
        });
    }

    private void openLink(int index, String title)
    {

        try {
            if (isAppInstalled(mLinkInfoArr[index].mPackageName)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage(mLinkInfoArr[index].mPackageName);
                    intent.setData(Uri.parse(getString(mLinkInfoArr[index].mAppURI)));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(mLinkInfoArr[index].mURL))));
                }
            } else {
                Intent browserIntent = new Intent(getApplicationContext(), BrowserActivity.class);
                browserIntent.putExtra(getString(R.string.link_url), getString(mLinkInfoArr[index].mURL));
                browserIntent.putExtra(getString(R.string.link_title), title);
                startActivity(browserIntent);
            }
        }
        catch (Exception e){}
    }

    private void openSection(String sectionID)
    {
        Intent sectionIntent;

        if(sectionID.equals(ApoContract.APO_EVENT) || sectionID.equals(ApoContract.APO_REVIEW) || sectionID.equals(ApoContract.APO_NEWSLETTER)) {
            sectionIntent = new Intent(getApplicationContext(), getResources().getBoolean(R.bool.device_is_phone) ? DynamicActivity.class : DynamicLargeActivity.class);
        }
        else if(sectionID.equals(ApoContract.APO_POSTER) ) {
            sectionIntent = new Intent(getApplicationContext(), getResources().getBoolean(R.bool.device_is_phone) ? PosterActivity.class : PosterLargeActivity.class);
        }
        else if(sectionID.equals(ApoContract.APO_PHOTO)) {
            sectionIntent = new Intent(getApplicationContext(), PhotoActivity.class);
        }
        else if(sectionID.equals(ApoContract.APO_MUSIC) || sectionID.equals(ApoContract.APO_VIDEO)) {
            sectionIntent = new Intent(getApplicationContext(), PlayerActivity.class);
        }
        else
        {
            sectionIntent = null;
        }
        if(null != sectionIntent) {
            sectionIntent.putExtra(ApoContract.APO_SECTION_ID, sectionID);
            startActivity(sectionIntent);
        }
    }

    private boolean isAppInstalled(String packageName) {
        if(null != packageName) {
            Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);

            Log.e(TAG, packageName + " isAppInstalled: " + mIntent);
            return (null != mIntent);
        }
        Log.e(TAG, packageName + " isAppInstalled: null apriori" );

        return false;
    }

    @Override
    protected void onLanguageChange() {
        super.onLanguageChange();
        fillMenu();
    }
}
