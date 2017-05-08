package am.apo.filharmonik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by user on 11/27/14.
 */
public class ApoWebView implements FusionWebView.OnClickListener{

    Activity mParentActivity;
    FusionWebView mWebView;
    ProgressBar mProgressBar;
    OnClickListener mClickListener;
    OnProgressListener mProgressListener;
    Date mLoadDate;
    boolean mCheckConnection;

    public ApoWebView(Activity parentActivity, boolean useViewPort, boolean needClickListener)
    {
        mParentActivity = parentActivity;
        mProgressBar = (ProgressBar)mParentActivity.findViewById(R.id.apo_web_load_progress);
        mProgressBar.setVisibility(View.GONE);
        mWebView = (FusionWebView)mParentActivity.findViewById(R.id.apo_web_view_wrapper);

        mWebView.setBackgroundColor(mParentActivity.getResources().getColor(android.R.color.transparent));
        mWebView.setWebChromeClient(new WebChromeClient());

/*        if(mParentActivity.getResources().getBoolean(R.bool.device_is_phone))
        {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
*/
        mCheckConnection = useViewPort;
        mWebView.getSettings().setJavaScriptEnabled(useViewPort);
        mWebView.getSettings().setUseWideViewPort(useViewPort);
        mWebView.getSettings().setLoadWithOverviewMode(useViewPort);
        //mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");

        float density = mParentActivity.getResources().getDisplayMetrics().densityDpi;

        Log.e("WWW", "Creaing web view, needListener: " + needClickListener + ", useViewPort: " + useViewPort + ", density: " + density);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 /*||
                DisplayMetrics.DENSITY_LOW==density || DisplayMetrics.DENSITY_MEDIUM==density*/ ) {
                mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }

        if(needClickListener) {
                mWebView.setClickListener(this);
        }

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoadDate = new Date();
                Log.e("GG", "load url started: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                try {
                    Date endDate = new Date();
                    Log.e("GG", "load url finished: " + url + ", time ms: " + (endDate.getTime() - mLoadDate.getTime()));

                    mProgressBar.setVisibility(View.GONE);
                    if(null != mProgressListener)
                    {
                        mProgressListener.onFinished();
                    }
                }
                catch(Exception e){}
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                try {
                    errorHandler();
                }
                catch (Exception e){}
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                try {
                    if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                        Log.e("TAF", "shouldOverrideUrlLoading: " + url);

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mParentActivity.startActivity(intent);
                        return true;
                    }
                }
                catch (Exception e){}

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onClick() {

        if(null != mClickListener) {
              mClickListener.onWebClick();
        }
    }

    public void loadUrl(String url) {
        try {
            if (mCheckConnection) {
                mProgressBar.setVisibility(View.VISIBLE);
                if (isOnline()) {
                    mWebView.loadUrl(url);
                } else {
                    errorHandler();
                }
            } else {
                mWebView.loadUrl(url);
            }
        }catch (Exception e){}
    }

    public void loadFromText(String text)
    {
        String core = "<html><body style='text-align:justify;color:rgba(255,255,255,255);font-size:15;margin: 10px 10px 10px 10px;'>";
        text = text.replace("\r\n", "<br>");
        text = text.replace("\n", "<br>");
        core += text;
        core += "</body></html>";
        mWebView.loadData(core, "text/html; charset=UTF-8", null);

/*        String playVideo= "<html><body>Youtube video .. <br> <iframe class=\"youtube-player\" type=\"text/html\" width=\"240\" height=\"385\" src=\"http://www.youtube.com/embed/OudxCGUemb8\" frameborder=\"0\"></body></html>";

        mWebView.loadData(playVideo, "text/html", "utf-8");
*/
    }

    public void finish()
    {
        try {
            Log.e("WEB", "Finish called!!!!");
            mWebView.stopLoading();
        }
        catch (Exception e){}
    }

    public void setWebVisibility(int visibility)
    {
        mWebView.setVisibility(visibility);
    }

    private boolean isOnline() {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) mParentActivity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
        catch (Exception e){}
        return false;
    }

    private void errorHandler()
    {
        mProgressBar.setVisibility(View.GONE);
        mWebView.loadUrl("about:blank");
        Toast.makeText(mParentActivity, mParentActivity.getString(R.string.APO_NETWORK_ERROR), Toast.LENGTH_LONG).show();
    }

    public interface OnClickListener {
         void onWebClick();
    }

    void setOnClickListener(OnClickListener listener)
    {
        mClickListener = listener;
    }

    public interface OnProgressListener {
        void onFinished();
    }

    void setOnProgressListener(OnProgressListener listener)
    {
        mProgressListener = listener;
    }
}
