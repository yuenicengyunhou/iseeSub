package com.dqhc.iseesub.com.dqhc.iseesub.baseActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePermissionActivity extends BaseWebActivity {

    protected final String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
    };
    protected int PERMISSON_REQUESTCODE = 57;


    protected void permissionsRequest() {
        if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
//            if (isNeedCheck) {
            checkPermissions(needPermissions);
//            }
        }
    }

    protected void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
                    int size = needRequestPermissonList.size();
                    String[] array = needRequestPermissonList.toArray(new String[size]);
                    Method method = getClass().getMethod("requestPermissions", String[].class, int.class);
                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
                    Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                    Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                            String.class);
                    if ((Integer) checkSelfMethod.invoke(this, perm) != PackageManager.PERMISSION_GRANTED
                            || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return needRequestPermissonList;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                permissionDenied();
            } else {
                permissionAllowed();
            }
        }
    }

    protected abstract void permissionAllowed();

    protected abstract void permissionDenied();

    protected boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
