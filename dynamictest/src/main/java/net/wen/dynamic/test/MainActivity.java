package net.wen.dynamic.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.feature.install.FeatureInstallManager;
import com.huawei.hms.feature.install.FeatureInstallManagerFactory;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dynamic);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        FeatureCompat.install(newBase);
    }
}
