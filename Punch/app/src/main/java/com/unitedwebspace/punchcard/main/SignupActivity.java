package com.unitedwebspace.punchcard.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.unitedwebspace.punchcard.preference.PrefConst;
import com.unitedwebspace.punchcard.preference.Preference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password, repwd;
    int idx = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        repwd = (EditText) findViewById(R.id.repassword);

        ((LinearLayout)findViewById(R.id.linearlayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void gotoSignin(View view){
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void signUp(View view){

        if(name.getText().length() ==0) {
            name.setError("Enter your name");
            return;
        }
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
        if(repwd.getText().length()==0) {
            repwd.setError("Reenter password");
            return;
        }
        else if(!repwd.getText().toString().equals(password.getText().toString())){
            repwd.setError("Enter the same password");
            return;
        }

        if(Commons.user.equals("business")) {
            register("1");
        }else {
            register("0");
        }
    }

    public void register(final String type) {

        String url = ReqConst.SERVER_URL + "signup";

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

                params.put("name", name.getText().toString().trim());
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
                idx = response.getInt("id");
                Preference.getInstance().put(getApplicationContext(), PrefConst.EMAIL, email.getText().toString().trim());
                Preference.getInstance().put(getApplicationContext(), PrefConst.PASSWORD, password.getText().toString());
                Preference.getInstance().put(getApplicationContext(), PrefConst.USER, Commons.user);
                if(Commons.user.equals("business")) {
                    intent = new Intent(getApplicationContext(), RegisterBusinessActivity.class);
                }else {
                    intent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                }
                intent.putExtra("id", String.valueOf(idx));
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
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



























