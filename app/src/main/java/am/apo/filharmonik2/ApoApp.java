package am.apo.filharmonik2;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.batch.android.Batch;
import com.batch.android.Config;

/**
 * Created by henrikgardishyan on 12/13/14.
 */
public class ApoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        Foreground.init(this);
        Batch.Push.setGCMSenderId("335176342881");
        Batch.setConfig(new Config("5A871FC44426845753950243DDD971"));
//        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public boolean isVisible() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        return Foreground.getInstance().isForeground();
//        }
//        return false;
    }
}
