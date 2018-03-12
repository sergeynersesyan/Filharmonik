package am.apo.filharmonik2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class AboutActivity extends ApoSectionActivity {

    ApoWebView mHistoryContactText;
    final int INDEX_HISTORY = 0;
    final int INDEX_ORCHESTRA = 1;
    final int INDEX_MANAGEMENT = 2;
    final int INDEX_CONTACT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setSectionID(ApoContract.APO_ABOUT);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setMainTitle(getString(R.string.TITLE_HISTORY));
        setSubTitle(getString(R.string.SUB_TITLE_ANPO));

        mHistoryContactText = new ApoWebView(this, false, false);
        mHistoryContactText.loadUrl(getResources().getString(R.string.history_description));
        resetButtons(INDEX_HISTORY);

        final ImageButton btnHistory = (ImageButton)findViewById(R.id.about_history_button);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMainTitle(getString(R.string.TITLE_HISTORY));
                setSubTitle(getString(R.string.SUB_TITLE_ANPO));
                mHistoryContactText.loadUrl(getResources().getString(R.string.history_description));
                resetButtons(INDEX_HISTORY);
            }
        });

        ImageButton btnOrchestra = (ImageButton)findViewById(R.id.about_orchestra_button);
        btnOrchestra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent orchestraIntent = new Intent(getApplicationContext(), OrchestraActivity.class);
                startActivity(orchestraIntent);
            }
        });

        ImageButton btnManagement = (ImageButton)findViewById(R.id.about_management_button);
        btnManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent managementIntent = new Intent(getApplicationContext(), ManagementActivity.class);
                startActivity(managementIntent);
            }
        });

        ImageButton btnContact = (ImageButton)findViewById(R.id.about_contact_button);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMainTitle(getString(R.string.TITLE_CONTACT));
                setSubTitle(getString(R.string.SUB_TITLE_ANPO));
                mHistoryContactText.loadUrl(getResources().getString(R.string.contact_description));
                resetButtons(INDEX_CONTACT);
            }
        });
    }

    void resetButtons(int index)
    {
        ImageButton btnHistory = (ImageButton)findViewById(R.id.about_history_button);
        ImageButton btnOrchestra = (ImageButton)findViewById(R.id.about_orchestra_button);
        ImageButton btnManagement = (ImageButton)findViewById(R.id.about_management_button);
        ImageButton btnContact = (ImageButton)findViewById(R.id.about_contact_button);
        btnHistory.setSelected(INDEX_HISTORY==index);
        btnOrchestra.setSelected(INDEX_ORCHESTRA==index);
        btnManagement.setSelected(INDEX_MANAGEMENT==index);
        btnContact.setSelected(INDEX_CONTACT==index);
    }
}
