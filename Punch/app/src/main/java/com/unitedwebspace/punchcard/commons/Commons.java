package com.unitedwebspace.punchcard.commons;

import android.app.Fragment;
import android.app.NotificationManager;

import com.unitedwebspace.punchcard.classes.duomenu.ProfileFragment;
import com.unitedwebspace.punchcard.models.Card;
import com.unitedwebspace.punchcard.models.Customer;
import com.unitedwebspace.punchcard.models.Merchant;

/**
 * Created by sonback123456 on 4/13/2018.
 */

public class Commons {
    public static boolean buyCardFromEditProfile = false;
    public static Card card = new Card();
    public static Customer customer = new Customer();
    public static String user = "";
    public static Merchant merchant = new Merchant();
    public static Merchant thisMerchant = new Merchant();
    public static Customer thisCustomer = new Customer();
    public static ProfileFragment profileFragment = null;
    public static boolean refreshF = false;
    public static boolean notiF = false;
    public static NotificationManager notificationManager = null;
}
