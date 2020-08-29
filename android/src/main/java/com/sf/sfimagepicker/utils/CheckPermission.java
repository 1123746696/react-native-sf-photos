package com.sf.sfimagepicker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-04-18.
 */

public class CheckPermission {
    public static void CheckPermission(Context context, String[] permission, CheckPermissionCallback callback) {
        {
            List<String> requestpermission = new ArrayList<>();
            for (int i = 0; i < permission.length; i++) {
                if (ContextCompat.checkSelfPermission(context, permission[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    requestpermission.add(permission[i]);
                }
            }
            String[] request = new String[requestpermission.size()];
            for (int i = 0; i < requestpermission.size(); i++) {
                request[i] = requestpermission.get(i);
            }
            if (request.length > 0) {
                ActivityCompat.requestPermissions((Activity) context, request, 0);
            }else{
                callback.onResult();
            }
        }
    }
    public static void CheckPermission(Fragment fragment, String[] permission, CheckPermissionCallback callback) {
        {
            List<String> requestpermission = new ArrayList<>();
            for (int i = 0; i < permission.length; i++) {
                if (ContextCompat.checkSelfPermission(fragment.getContext(), permission[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    requestpermission.add(permission[i]);
                }
            }
            String[] request = new String[requestpermission.size()];
            for (int i = 0; i < requestpermission.size(); i++) {
                request[i] = requestpermission.get(i);
            }
            if (request.length > 0) {
                fragment.requestPermissions(request, 0);
            }else{
                callback.onResult();
            }
        }
    }
    public interface CheckPermissionCallback{
        void onResult();
    }
}
