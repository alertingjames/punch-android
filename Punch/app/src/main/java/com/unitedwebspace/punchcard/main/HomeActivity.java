package com.unitedwebspace.punchcard.main;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.Firebase;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.classes.duomenu.CardsFragment;
import com.unitedwebspace.punchcard.classes.duomenu.CustomersFragment;
import com.unitedwebspace.punchcard.classes.duomenu.MenuAdapter;
import com.unitedwebspace.punchcard.classes.duomenu.NotificationsFragment;
import com.unitedwebspace.punchcard.classes.duomenu.ProfileFragment;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.models.Merchant;
import com.unitedwebspace.punchcard.preference.PrefConst;
import com.unitedwebspace.punchcard.preference.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class HomeActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {
    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    final Handler mHandler = new Handler();
    Timer mTimer = new Timer();
    LinearLayout alertDialog, alertDialogBuy;
    FrameLayout layout;
    TextView email, acceptButton, denyButton, cancelButton, buyButton;
    ProgressDialog pd;

    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        alertDialog = (LinearLayout)findViewById(R.id.alertDialog);
        layout = (FrameLayout) findViewById(R.id.layout);

        email = (TextView) findViewById(R.id.email);
        acceptButton = (TextView) findViewById(R.id.acceptButton);
        denyButton = (TextView) findViewById(R.id.denyButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                punchNewCard();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineRequest();
            }
        });

        alertDialogBuy = (LinearLayout)findViewById(R.id.alertDialogBuy);
        cancelButton = (TextView) findViewById(R.id.cancelButton);
        buyButton = (TextView) findViewById(R.id.buyButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuy.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuy.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                Intent intent=new Intent(getApplicationContext(), BuyCardActivity.class);
                intent.putExtra("newBuyFlag", true);
                startActivity(intent);
            }
        });

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        if(!Commons.refreshF) {
            goToFragment(new CustomersFragment(), false);

            mMenuAdapter.setViewSelected(0, true);
            setTitle(mTitles.get(0));
        }else {
            setTitle(mTitles.get(2));
            goToFragment(new CardsFragment(), false);
        }

        mTimer.schedule(doAsynchronousTask, 0, 6000);

        login();
        Log.d("Token===>", Preference.getInstance().getValue(this, PrefConst.TOKEN, ""));
        updateToken(Preference.getInstance().getValue(this, PrefConst.TOKEN, ""));

        getNotifications();
    }

    TimerTask doAsynchronousTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
     //               getNotifications();
                }
            });
        }
    };

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onFooterClicked() {
        Toast.makeText(this, "onFooterClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClicked() {
        Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show();
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title
        setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {
            case 0:
                goToFragment(new CustomersFragment(), false);
                break;
            case 1:
                goToFragment(new ProfileFragment(), false);
                break;
            case 2:
                refresh();
//                goToFragment(new CardsFragment(), false);

//                Commons.buyCardFromEditProfile = true;
//                Intent intent = new Intent(getApplicationContext(), BuyCardActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
                break;
            case 3:
                goToFragment(new NotificationsFragment(), false);
                break;
            case 4:
                logout();
                break;
            default:
                goToFragment(new CustomersFragment(), false);
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private void refresh(){
        Commons.refreshF = true;
        finish();
        overridePendingTransition(0,0);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    public void logout(){

        Preference.getInstance().put(getApplicationContext(), PrefConst.EMAIL, "");
        Preference.getInstance().put(getApplicationContext(), PrefConst.PASSWORD, "");
        Preference.getInstance().put(getApplicationContext(), PrefConst.USER, "");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void login() {

        String url = ReqConst.SERVER_URL + "login";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseRegisterResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", Preference.getInstance().getValue(getApplicationContext(), PrefConst.EMAIL, ""));
                params.put("password", Preference.getInstance().getValue(getApplicationContext(), PrefConst.PASSWORD, ""));
                String user = Preference.getInstance().getValue(getApplicationContext(), PrefConst.USER, "");
                if (user.equals("business"))
                    params.put("business", "1");
                else params.put("business", "0");

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if (result_code == 200) {
                JSONObject loginData = response.getJSONObject("login_data");
                JSONObject userInfo = loginData.getJSONObject("user_info");
                JSONObject businessInfo = loginData.getJSONObject("business_info");
                Merchant merchant = new Merchant();
                merchant.set_idx(userInfo.getInt("id"));
                merchant.setName(userInfo.getString("name"));
                merchant.setEmail(userInfo.getString("email"));
                merchant.set_bidx(businessInfo.getInt("business_id"));
                Commons.thisMerchant = merchant;
                Commons.thisMerchant.setRewardName(businessInfo.getString("reward"));
                Commons.thisMerchant.setBusinessName(businessInfo.getString("business_name"));
                Commons.thisMerchant.setPhoneNumber(businessInfo.getString("business_phonenumber"));
                Commons.thisMerchant.setWebsiteUrl(businessInfo.getString("website_url"));
                Commons.thisMerchant.setSocialMediaLink(businessInfo.getString("social_media_link"));
                Commons.thisMerchant.setNotes(businessInfo.getString("notes"));
                Commons.thisMerchant.setBusinessFont(businessInfo.getString("font1"));
                Commons.thisMerchant.setRewardFont(businessInfo.getString("font2"));
                Commons.thisMerchant.setPunches(businessInfo.getInt("cards_for_free"));
                Commons.thisMerchant.setCardColor(businessInfo.getString("color"));
                Commons.thisMerchant.setLogo(businessInfo.getString("logo"));
                Commons.thisMerchant.setBackgroundImage(businessInfo.getString("background"));
                Commons.thisMerchant.setCardNumbers(businessInfo.getInt("card_numbers"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateToken(final String token) {

        String url = ReqConst.SERVER_URL + "updateToken";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateTokenResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                if(Commons.user.equals("business"))
                    params.put("user_id", String.valueOf(Commons.thisMerchant.get_idx()));
                else
                    params.put("user_id", String.valueOf(Commons.thisCustomer.get_idx()));
                params.put("user_token", token);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseUpdateTokenResponse(String json) {

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
            }
            else {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Commons.refreshF = false;
    }

    public void getNotifications() {

        String url = ReqConst.SERVER_URL + "getNotifications";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseMyRewardsResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("business_id", String.valueOf(Commons.thisMerchant.get_bidx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseMyRewardsResponse(String json) {

        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                try{
                    JSONArray jsonArray = response.getJSONArray("notifications");
                    JSONObject jsonData = jsonArray.getJSONObject(0);
                    String email = jsonData.getString("customer_email");
//                    if(!Commons.notiF)
//                        showNotification(email);
                    if(email.length() > 0)
                        showNotiAlert(email);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showNotification(String email){

        Commons.notiF = true;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        long[] v = {500, 1000};

        Intent intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.app.Notification n = new android.app.Notification.Builder(this)
                .setContentTitle("You have a message")
                .setFullScreenIntent(pIntent,true)
                .setContentText(email)
                .setSmallIcon(R.drawable.noti).setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.noti))
                .setContentIntent(pIntent)
                .setSound(uri)
                .setVibrate(v)
                .setAutoCancel(true).build();

        notificationManager.notify(0, n);

        Commons.notificationManager = notificationManager;
    }

    public void showNotiAlert(String eml) {
        alertDialog.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        email.setText(eml);
    }

    public void punchNewCard() {

        String url = ReqConst.SERVER_URL + "punchNewCard";

        pd.show();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("business_id", String.valueOf(Commons.thisMerchant.get_bidx()));
                params.put("email", email.getText().toString().trim());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseResponse(String json) {

        pd.dismiss();
        Intent intent;
        alertDialog.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        email.setText("");
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getApplicationContext(), "Accepted!", Toast.LENGTH_SHORT).show();
                sendNotification("accepted");
                finish();
                overridePendingTransition(0,0);
                intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            else if (result_code == 106) {
            //    showAlertDialogPunch();
                alertDialogBuy.setVisibility(View.VISIBLE);
                layout.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void showAlertDialogPunch(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Hint!");
        alertDialog.setMessage("You don't have any enough card");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Buy Card",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent=new Intent(getApplicationContext(), BuyCardActivity.class);
                        intent.putExtra("newBuyFlag", true);
                        startActivity(intent);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void declineRequest() {

        String url = ReqConst.SERVER_URL + "declineRequest";

        pd.show();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseDeclineResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("business_id", String.valueOf(Commons.thisMerchant.get_bidx()));
                params.put("customer_email", email.getText().toString().trim());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseDeclineResponse(String json) {

        pd.dismiss();
        Intent intent;
        alertDialog.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        email.setText("");
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getApplicationContext(), "Denied!", Toast.LENGTH_SHORT).show();
                sendNotification("denied");
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    Firebase reference;

    public void sendNotification(String content){
        Firebase.setAndroidContext(this);
        reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/" +
                email.getText().toString().replace(".com", "").replace(".", "ddoott")+
                "/" + Commons.thisMerchant.getEmail().replace(".com", "").replace(".", "ddoott"));

        Map<String, String> map = new HashMap<String, String>();
        map.put("sender", Commons.thisMerchant.getBusinessName());
        map.put("senderId", String.valueOf(Commons.thisMerchant.get_bidx()));
        map.put("msg", content);
        reference.removeValue();
        reference.push().setValue(map);
    }
}





















