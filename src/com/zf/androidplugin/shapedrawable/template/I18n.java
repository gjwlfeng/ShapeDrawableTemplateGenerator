package com.zf.androidplugin.shapedrawable.template;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Lenovo on 2016/1/20.
 */
public class I18n
{
    static String I18N_NAME="/i18n/resource";
    static ResourceBundle bundle=null;
    static
    {
        Locale locale=Locale.getDefault();
       // Locale locale=Locale.US;
        bundle = ResourceBundle.getBundle(I18N_NAME, locale);
    }

    public static String getString(String key)
    {
       return bundle.getString(key);
    }
}
