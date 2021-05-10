package com.unitedwebspace.punchcard.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.models.Customer;
import com.unitedwebspace.punchcard.models.Merchant;
import com.unitedwebspace.punchcard.preference.PrefConst;
import com.unitedwebspace.punchcard.preference.Preference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CALL_PHONE,
//            android.Manifest.permission.VIBRATE,
//            android.Manifest.permission.READ_CALENDAR,
//            android.Manifest.permission.WRITE_CALENDAR,
//            android.Manifest.permission.SET_TIME,
//            android.Manifest.permission.RECORD_AUDIO,
//            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
//            android.Manifest.permission.RECEIVE_SMS,
//            android.Manifest.permission.SEND_SMS,
//            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.WAKE_LOCK,
//            android.Manifest.permission.LOCATION_HARDWARE
    };

    TextView businessButton, customerButton;
    String user, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAllPermission();

        businessButton = (TextView) findViewById(R.id.businessButton);
        customerButton = (TextView) findViewById(R.id.customerButton);

        user = Preference.getInstance().getValue(getApplicationContext(), PrefConst.USER, "");
        email = Preference.getInstance().getValue(getApplicationContext(), PrefConst.EMAIL, "");
        password = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PASSWORD, "");

        if(user.length() > 0) {
            if (user.equals("business"))
                login("1");
            else
                login("0");
        }

    }

    public void signinPageForBusiness(View view){
        Commons.user = "business";
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    public void signinPageForCustomer(View view){
        Commons.user = "customer";
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    ProgressDialog _progressDlg;

    public void showProgress() {
        closeProgress();
        _progressDlg = ProgressDialog.show(this, "Processing...", "Wait",true);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        if(_progressDlg!=null && _progressDlg.isShowing()){
            _progressDlg.dismiss();
            _progressDlg = null;
        }
    }

    public void login(final String type) {

        String url = ReqConst.SERVER_URL + "login";

        showProgress();

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
                closeProgress();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("password", password);
                params.put("business", type);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        closeProgress();
        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                JSONObject loginData = response.getJSONObject("login_data");
                JSONObject userInfo = loginData.getJSONObject("user_info");

                if(user.equals("business")){
                    JSONObject businessInfo = loginData.getJSONObject("business_info");
                    Merchant merchant = new Merchant();
                    merchant.set_idx(userInfo.getInt("id"));
                    merchant.setName(userInfo.getString("name"));
                    merchant.setEmail(userInfo.getString("email"));
                    merchant.set_bidx(businessInfo.getInt("business_id"));
                    Commons.thisMerchant = merchant;
                    if(Commons.thisMerchant.get_bidx() == 0) {
                        intent = new Intent(getApplicationContext(), RegisterBusinessActivity.class);
                        intent.putExtra("id", String.valueOf(Commons.thisMerchant.get_idx()));
                    }
                    else {
                        Commons.thisMerchant.setRewardName(businessInfo.getString("reward"));
                        Commons.thisMerchant.setBusinessName(businessInfo.getString("business_name"));
                        Commons.thisMerchant.setPhoneNumber(businessInfo.getString("business_phonenumber"));
                        Commons.thisMerchant.setWebsiteUrl(businessInfo.getString("website_url"));
                        Commons.thisMerchant.setSocialMediaLink(businessInfo.getString("social_media_link"));
                        Commons.thisMerchant.setNotes(businessInfo.getString("notes"));
                        Commons.thisMerchant.setBusinessFont(businessInfo.getString("font1"));
                        Commons.thisMerchant.setRewardFont(businessInfo.getString("font2"));
                        Commons.thisMerchant.setRewards(businessInfo.getInt("cards_for_free"));
                        Commons.thisMerchant.setCardColor(businessInfo.getString("color"));
                        Commons.thisMerchant.setLogo(businessInfo.getString("logo"));
                        Commons.thisMerchant.setBackgroundImage(businessInfo.getString("background"));
                        Commons.thisMerchant.setCardNumbers(businessInfo.getInt("card_numbers"));

                        if(Commons.thisMerchant.getLogo().length()==0) {
                            intent = new Intent(getApplicationContext(), CreateCardActivity.class);
                            intent.putExtra("id", String.valueOf(Commons.thisMerchant.get_idx()));
                        }
                        else
                            intent = new Intent(getApplicationContext(), HomeActivity.class);
                    }
                }else {
                    Customer customer = new Customer();
                    customer.set_idx(userInfo.getInt("id"));
                    customer.setName(userInfo.getString("name"));
                    customer.setEmail(userInfo.getString("email"));
                    Commons.thisCustomer = customer;
                    intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}























