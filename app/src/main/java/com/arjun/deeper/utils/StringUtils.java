package com.arjun.deeper.utils;

import androidx.annotation.StringRes;

import com.arjun.deeper.DeeperApplication;

public class StringUtils {

    public static String getString(@StringRes int stringId) {
        return DeeperApplication.getContext().getString(stringId);
    }
}
