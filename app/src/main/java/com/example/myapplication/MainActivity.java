package com.example.myapplication;/*
 * Copyright 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.android.system.runtimepermissions.R;
import com.example.myapplication.camera.CameraPreviewFragment;
import com.example.myapplication.util.MyDialogUtils;
import com.example.myapplication.util.RuntimePermissionUtil;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "wangbei";


    private View mLayout;


    public void showCamera(View view) {
        boolean b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        Log.i(TAG, "Show camera button pressed. Checking permission.=-------"+b);
        // BEGIN_INCLUDE(camera_permission)
        // 权限检查，如果没有权限则申请
        if (RuntimePermissionUtil.checkSinglePermission(this, Manifest.permission.CAMERA)) {
            // 权限检查
            RuntimePermissionUtil.requestCameraPermission(this);

        } else {
            // 已经拥有权限则直接开始业务逻辑
            Log.i(TAG, "CAMERA permission has already been granted. Displaying camera preview.");
            showCameraPreview();
        }
    }




    /**
     * Display the {@link CameraPreviewFragment} in the content area if the required Camera
     * permission has been granted.
     */
    private void showCameraPreview() {
        getSupportFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, CameraPreviewFragment.newInstance()).addToBackStack("contacts").commit();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == RuntimePermissionUtil.REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCameraPreview();
            } else {
                //拒绝首次申请后（不再提示）再次申请。--->>>弹出自定义对话框，让用户自己去设置页面申请
                MyDialogUtils.CommDialog(this, "温馨提示", "请从设置界面开启相机权限!", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyDialogUtils.commDialog.dismiss();
                    }
                }, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            // FIXME: 2019/3/28 进入权限设置页面，不同手机设置页面不同，需要在此适配
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 666);
                        } catch (Exception e) {
                        } finally {
                            MyDialogUtils.commDialog.dismiss();
                        }
                    }
                });
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode:" + requestCode + "resultCode:" + resultCode + "data:" + data);
    }

    public void onBackClick(View view) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.sample_main_layout);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RuntimePermissionsFragment fragment = new RuntimePermissionsFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }
}
