package com.example.robert.easytransport.Utils;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Robert on 11/1/2017.
 */

public class Utils {
    public static void closeKeyboard(Context context, View v){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public static void closeDrawer(DrawerLayout drawer){
        drawer.closeDrawer(GravityCompat.START);
    }


}
