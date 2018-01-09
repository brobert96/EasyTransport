package com.example.robert.easytransport.Utils;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Robert on 11/29/2017.
 */

public class DialogBox extends AlertDialog {

    AlertDialog.Builder builder;

    protected DialogBox(Context context) {
        super(context);
        builder = new AlertDialog.Builder(context);
    }

    public DialogBox(Context context, OnAlertDialgoClickCallback callbackPositive, OnAlertDialgoClickCallback callbackNegative){
        super(context);
        builder = new AlertDialog.Builder(context);
        if(callbackNegative != null && callbackPositive != null){
            callbackNegative.setOnClickMethod(builder);
            callbackPositive.setOnClickMethod(builder);
        }
    }

    public DialogBox(Context context, OnAlertDialgoClickCallback callbackNeutral){
        super(context);
        builder = new AlertDialog.Builder(context);
        if(callbackNeutral != null ){
            callbackNeutral.setOnClickMethod(builder);
        }
    }

    public void setMessage(String message){
        builder.setMessage(message);
    }

    public void setTitle(String title){
        builder.setTitle(title);
    }

    public void showDialog(){
        builder.create().show();
    }

    public interface OnAlertDialgoClickCallback {
        public void setOnClickMethod(AlertDialog.Builder builder);
    }

}




