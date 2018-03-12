package am.apo.filharmonik2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by henrikgardishyan on 12/13/14.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Foreground implements Application.ActivityLifecycleCallbacks {

private static Foreground instance;

    public static void init(Application app){
        if (instance == null){
                instance = new Foreground();
                app.registerActivityLifecycleCallbacks(instance);
        }
    }

    public static Foreground getInstance(){
        return instance;
    }

    private Foreground(){}

    private boolean mForeground;

    public boolean isForeground(){
        return mForeground;
    }

    public boolean isBackground(){
        return !mForeground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mForeground = false;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }
}
