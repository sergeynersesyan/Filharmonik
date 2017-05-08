package am.apo.filharmonik;

import android.app.Application;
import android.os.Build;
import android.util.Log;

/**
 * Created by henrikgardishyan on 12/13/14.
 */
public class ApoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Foreground.init(this);
        }
    }

    public boolean isVisible()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return Foreground.getInstance().isForeground();
        }
        return false;
    }
}
