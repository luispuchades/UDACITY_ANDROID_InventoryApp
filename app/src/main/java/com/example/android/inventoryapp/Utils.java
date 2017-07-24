package com.example.android.inventoryapp;

import android.text.TextUtils;

/**
 * Created by luisp on 24/07/2017.
 */

public class Utils {

    /**
     * Private default constructor to avoid instantiating this class
     */
    protected Utils() {
    }

    /**
     * Check if a given email address is in a valid format
     *
     * @param target The email address to check
     * @return if it looks valid or not
     */
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}