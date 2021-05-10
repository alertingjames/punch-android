package com.unitedwebspace.punchcard.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import static com.unitedwebspace.punchcard.main.SignupActivity.isValidEmail;

public class SigninActivity extends AppCompatActivity {

    EditText email, password;
    TextView forgotPasswordButton, gotoSignupButton, signinButton;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        forgotPasswordButton = (TextView) findViewById(R.id.forgotPasswordButton);
        gotoSignupButton = (TextView) findViewById(R.id.gotosignup);
        signinButton = (TextView) findViewById(R.id.signinButton);

        ((LinearLayout)findViewById(R.id.linearlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    public void gotoSignup(View view){
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void signIn(View view){
        if(email.getText().length() == 0) {
            email.setError("Enter your email");
            return;
        }
        else if(!isValidEmail(email.getText().toString())) {
            email.setError("Enter your vaild email");
            return;
        }
        if(password.getText().length() == 0) {
            password.setError("Enter password");
            return;
        }
        if(Commons.user.equals("business")){
            login("1");
        }else {
            login("0");
        }
    }

    public void forgotPassword(View view){
        Intent intent = new Intent(getApplicationContext(), ForgotPwdActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void login(final String type) {

        String url = ReqConst.SERVER_URL + "login";

        pd.show();

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
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email.getText().toString().trim());
                params.put("password", password.getText().toString());
                params.put("business", type);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        pd.dismiss();
        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                JSONObject loginData = response.getJSONObject("login_data");
                JSONObject userInfo = loginData.getJSONObject("user_info");

                Preference.getInstance().put(getApplicationContext(), PrefConst.EMAIL, email.getText().toString().trim());
                Preference.getInstance().put(getApplicationContext(), PrefConst.PASSWORD, password.getText().toString());
                Preference.getInstance().put(getApplicationContext(), PrefConst.USER, Commons.user);

                if(Commons.user.equals("business")){
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
                        Commons.thisMerchant.setPunches(businessInfo.getInt("cards_for_free"));
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
            else if(result_code == 102){
                Toast.makeText(getApplicationContext(), "Invalid email. Try again", Toast.LENGTH_SHORT).show();
            }
            else if(result_code == 103){
                Toast.makeText(getApplicationContext(), "Invalid password. Try again", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

}



















