package net.wen.dynamic;

import android.app.Application;
import android.content.Context;

import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class DynamicApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        FeatureCompat.install(base);
    }
}
