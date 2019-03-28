package com.example.myapplication.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyu.
 * Created on 下午4:53 2019/3/28.
 * Author'github https://github.com/PrettyAnt
 */
public class RuntimePermissionUtil {
    /**
     * Id to identify a camera permission request.
     */
    public static final int REQUEST_CAMERA = 0;
    public static final int REQUEST_CONTACTS = 1;
    private static boolean isShouldShowRequestPermission = true;
    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    public static void requestCameraPermission(final Activity activity) {
        //动态申请权限
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            isShouldShowRequestPermission = false;
            //首次申请,弹出系统dialog权限提示框
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            if (!isShouldShowRequestPermission) {
                //拒绝首次申请后（不再提示）再次申请。--->>>弹出自定义对话框，让用户自己去设置页面申请
                MyDialogUtils.CommDialog(activity, "温馨提示", "请从设置界面开启相机权限!", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyDialogUtils.commDialog.dismiss();
                    }
                }, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + activity.getPackageName()));
                            activity.startActivityForResult(intent,999);
                        } catch (Exception e) {
                        } finally {
                            MyDialogUtils.commDialog.dismiss();
                        }
                    }
                });
            } else {
                isShouldShowRequestPermission = false;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }

        }
    }

    /**
     * 检查单个权限
     * @param activity
     * @param permission
     * @return
     */
    public static boolean checkSinglePermission(Activity activity, @NonNull String permission){
        return ActivityCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查多个权限
     */
    public void checkPermission(Activity activity) {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {

                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            }
            if ((activity.checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED)) {

                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            }
            if ((activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            }
            if (permissionsList.size() != 0) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        666);
            }
        }
    }
}
