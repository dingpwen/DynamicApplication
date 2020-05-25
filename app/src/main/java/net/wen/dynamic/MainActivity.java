package net.wen.dynamic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.feature.install.FeatureInstallManager;
import com.huawei.hms.feature.install.FeatureInstallManagerFactory;
import com.huawei.hms.feature.listener.InstallStateListener;
import com.huawei.hms.feature.model.FeatureInstallException;
import com.huawei.hms.feature.model.FeatureInstallRequest;
import com.huawei.hms.feature.model.FeatureInstallSessionStatus;
import com.huawei.hms.feature.model.InstallState;
import com.huawei.hms.feature.tasks.listener.OnFeatureFailureListener;
import com.huawei.hms.feature.tasks.listener.OnFeatureSuccessListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "base/MainActivity";
    private static final String DYNAMIC_MODULE = "dynamictest";
    private FeatureInstallManager mFeatureInstallManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFeatureInstallManager = FeatureInstallManagerFactory.create(this);
        findViewById(R.id.click_test).setOnClickListener(v -> launchDynamic());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFeatureInstallManager != null) {
            mFeatureInstallManager.registerInstallListener(mStateUpdateListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFeatureInstallManager != null) {
            mFeatureInstallManager.unregisterInstallListener(mStateUpdateListener);
        }
    }

    private InstallStateListener mStateUpdateListener = new InstallStateListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            Log.d(TAG, "install session state " + state);
            if (state.status() == FeatureInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
                try {
                    mFeatureInstallManager.triggerUserConfirm(state, MainActivity.this, 1);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (state.status() == FeatureInstallSessionStatus.REQUIRES_PERSON_AGREEMENT) {
                try {
                    mFeatureInstallManager.triggerUserConfirm(state, MainActivity.this, 1);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (state.status() == FeatureInstallSessionStatus.INSTALLED) {
                Log.i(TAG, "installed success ,can use new feature");
                makeToast("installed success , can test new feature ");
                startDynamic();//use the dynamic feature
                return;
            }

            if (state.status() == FeatureInstallSessionStatus.UNKNOWN) {
                Log.e(TAG, "installed in unknown status");
                makeToast("installed in unknown status ");
                return;
            }

            if (state.status() == FeatureInstallSessionStatus.DOWNLOADING) {
                long process = state.bytesDownloaded() * 100 / state.totalBytesToDownload();
                Log.d(TAG, "downloading  percentage: " + process);
                makeToast("downloading  percentage: " + process);
                return;
            }

            if (state.status() == FeatureInstallSessionStatus.FAILED) {
                Log.e(TAG, "installed failed, errorcode : " + state.errorCode());
                makeToast("installed failed, errorcode : " + state.errorCode());
            }
        }
    };

    private void requestDynamicInstall() {
        makeToast("Starting to download " + DYNAMIC_MODULE);
        FeatureInstallRequest request = FeatureInstallRequest.newBuilder()
                // 添加dynamic feature 的名称
                .addModule(DYNAMIC_MODULE)
                .build();
        mFeatureInstallManager.installFeature(request)
                .addOnListener(new OnFeatureSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d(TAG, "load feature onSuccess.session id:" + integer);
                    }
                })
                .addOnListener(new OnFeatureFailureListener<Integer>() {
                    @Override
                    public void onFailure(Exception exception) {
                        if (exception instanceof FeatureInstallException) {
                            int errorCode = ((FeatureInstallException) exception).getErrorCode();
                            Log.d(TAG, "load feature onFailure.errorCode:" + errorCode);
                        } else {
                            exception.printStackTrace();
                        }
                    }
                });
    }

    private boolean isDynamicInstalled() {
        return (mFeatureInstallManager != null) &&
                mFeatureInstallManager.getAllInstalledModules().contains(DYNAMIC_MODULE);
    }

    private void launchDynamic() {
        if(isDynamicInstalled()) {
            startDynamic();
        } else {
            requestDynamicInstall();
        }
    }

    private void startDynamic() {
        try {
            startActivity(new Intent(this,Class.forName("net.wen.dynamic.test.MainActivity")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void makeToast(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
