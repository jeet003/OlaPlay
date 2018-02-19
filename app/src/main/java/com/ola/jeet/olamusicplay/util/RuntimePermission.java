package com.ola.jeet.olamusicplay.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

public class RuntimePermission {
    private String permissions[];
    private Context context;
    private int request_code;
    private ArrayList<String> requiredPermisson;
    private String message;
    private boolean showMessage=false;
    private boolean cancelable;
    public final static int MY_PERMISSIONS_REQUEST_STORAGE=122;
    public RuntimePermission(String permissions[],Context context,int request_code,String message,boolean cancelable){
        this.permissions=permissions;
        this.context=context;
        this.request_code=request_code;
        this.message=message;
        this.cancelable=cancelable;
        requiredPermisson=new ArrayList<String>();
    }

    /**
     * check all permission. If permission granted then add to array list.<br>
     *  if array list size will be greater than one the call {@link #requestPermission()}
     *  else permissions are granted by user.
     */

    public void checkPermission()
    {
        for(String permission:permissions)
        {

            if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED)
            {
                requiredPermisson.add(permission);
            }
        }
        if(requiredPermisson.size()>=1)
        {

            permissions=requiredPermisson.toArray(new String[requiredPermisson.size()]);
            requestPermission();
        }
    }

    /**
     * called to request permission through alert dialog<br>
     * If permission is denied by user then next time show message why it is needed.
     * and if user user click never ask me again then permission will not be asked.
     */
    public void requestPermission() {
        for(final String permission:requiredPermisson)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                showMessage=true;
                break;
            }
        }
        if(showMessage)
        {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(cancelable);
            alertBuilder.setTitle("");
            alertBuilder.setMessage(message);
            alertBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) context, permissions, request_code);
                }
            });
            alertBuilder.show();
        }
        else if( !showMessage && (request_code==MY_PERMISSIONS_REQUEST_STORAGE) && ( !new PrefManager(context).getNeverAskBeforeStorageState())) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(false);
            alertBuilder.setTitle("");
            alertBuilder.setMessage(message);
            alertBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) context, permissions, request_code);
                }
            });
            alertBuilder.show();
        }
        else
        {
            ActivityCompat.requestPermissions((Activity) context,permissions,request_code);
        }
    }

}
