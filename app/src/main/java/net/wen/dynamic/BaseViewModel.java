package net.wen.dynamic;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.FAILED;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.INSTALLED;

abstract class BaseViewModel extends AndroidViewModel {
    private final SplitInstallManager splitInstallManager;
    private static final String TAG = "BaseViewModel";
    private static final String DYNAMIC_MODULE = "dynamictest";
    private int sessionId;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        splitInstallManager  = SplitInstallManagerFactory.create(application);
        splitInstallManager.registerListener(listener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        splitInstallManager.unregisterListener(listener);
    }

    private SplitInstallStateUpdatedListener listener = state -> {
        if(state.sessionId() == sessionId) {
            if(state.status() == FAILED){
                Log.d(TAG, "Module install failed with " + state.errorCode());
                Toast.makeText(getApplication(), "Module install failed with " + state.errorCode(), Toast.LENGTH_SHORT).show();
            } else if(state.status() == INSTALLED) {
                Toast.makeText(getApplication(), "Storage module installed successfully", Toast.LENGTH_SHORT).show();
                //saveCounter()
            } else {
                Log.d(TAG, "Status: " + state.status());
            }
        }
    };

    private void requestDynamicInstall() {
        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule(DYNAMIC_MODULE).build();
        splitInstallManager.startInstall(request)
                .addOnSuccessListener(id -> sessionId = id)
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Error installing module: ", exception);
                    Toast.makeText(getApplication(), "Error requesting module install", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isDynamicInstalled() {
        return (splitInstallManager != null) &&
                splitInstallManager.getInstalledModules().contains(DYNAMIC_MODULE);
    }

    public void callDynamicModule() {
        if(isDynamicInstalled()){
            initializeDynamicFeature();
        } else {
            requestDynamicInstall();
        }
    }

    protected abstract void initializeDynamicFeature();
}
