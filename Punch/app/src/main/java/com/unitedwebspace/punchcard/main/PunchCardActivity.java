package com.unitedwebspace.punchcard.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.models.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PunchCardActivity extends AppCompatActivity {

    LinearLayout holeFrame1, holeFrame2, cardHoleFrame;
    FrameLayout cardFrame, layout;
    TextView name, email, rewards;
    ProgressDialog pd;
    LinearLayout alertDialogDelete, alertDialogBuy;
    TextView cancelButton, noButton, buyButton, deleteButton;
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_card);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        alertDialogBuy = (LinearLayout) findViewById(R.id.alertDialogBuy);
        alertDialogDelete = (LinearLayout)findViewById(R.id.alertDialog);
        cancelButton = (TextView)findViewById(R.id.cancelButton);
        noButton = (TextView)findViewById(R.id.noButton);
        buyButton = (TextView)findViewById(R.id.buyButton);
        deleteButton = (TextView)findViewById(R.id.deleteButton);
        layout = (FrameLayout)findViewById(R.id.layout);

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

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogDelete.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogDelete.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                deleteCustomer();
            }
        });

        cardFrame = (FrameLayout) findViewById(R.id.cardFrame);
        cardHoleFrame = (LinearLayout) findViewById(R.id.cardHoleFrame);
        holeFrame1 = (LinearLayout) findViewById(R.id.holeFrame1);
        holeFrame2 = (LinearLayout) findViewById(R.id.holeFrame2);

        rewards = (TextView) findViewById(R.id.rewards);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);

        Customer customer = Commons.customer;

        if(customer.getPunches() == 0)
            ((TextView)findViewById(R.id.deleteCustomerButton)).setTextColor(Color.parseColor("#e6005c"));

        name.setText(customer.getName());
        email.setText(customer.getEmail());

        rewards.setText(String.valueOf(customer.getRewards()));

        int color = Color.parseColor(customer.getCardColor());

        setupCardFrameBackground(cardFrame, 0xffffffff, color);
        setupCardHoleFrameBackground(cardHoleFrame, color, color);

        holeFrame1.removeAllViews();
        holeFrame2.removeAllViews();

        int holes = customer.getPunches();

        if(holes > 10){
            for(int i=1; i<11; i++) {
                View hole = getLayoutInflater().inflate(R.layout.edit_holebox, holeFrame1, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holeFrame1.addView(hole);
            }
            for(int i=11; i<holes+1; i++) {
                View hole = getLayoutInflater().inflate(R.layout.edit_holebox, holeFrame2, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holeFrame2.addView(hole);
            }
            for(int i=holes+1; i<Commons.thisMerchant.getPunches()+1; i++) {
                View hole = getLayoutInflater().inflate(R.layout.empty_holebox, holeFrame2, false);
                holeFrame2.addView(hole);
            }
        }else if (holes > 0 && holes <= 10){
            for(int i=1; i<holes+1; i++) {
                View hole = getLayoutInflater().inflate(R.layout.edit_holebox, holeFrame1, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holeFrame1.addView(hole);
            }
            if(Commons.thisMerchant.getPunches()<=10){
                for(int i=holes+1; i<Commons.thisMerchant.getPunches()+1; i++) {
                    View hole = getLayoutInflater().inflate(R.layout.empty_holebox, holeFrame2, false);
                    holeFrame2.addView(hole);
                }
            }else {
                for(int i=holes+1; i<11; i++) {
                    View hole = getLayoutInflater().inflate(R.layout.empty_holebox, holeFrame2, false);
                    holeFrame1.addView(hole);
                }
                for(int i=11; i<Commons.thisMerchant.getPunches()+1; i++) {
                    View hole = getLayoutInflater().inflate(R.layout.empty_holebox, holeFrame2, false);
                    holeFrame2.addView(hole);
                }
            }
        }else {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setupCardHoleFrameBackground(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(20);
        shape.setColor(backgroundColor);
        shape.setStroke(2, borderColor);
        v.setBackground(shape);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setupCardFrameBackground(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(20);
        shape.setColor(backgroundColor);
        shape.setStroke(4, borderColor);
        v.setBackground(shape);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setupCardBackground(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(backgroundColor);
        shape.setStroke(2, borderColor);
        v.setBackground(shape);
    }

    public void punch(View view){

        if(Commons.customer.getPunches() > 0)
            punchNewCard();
        else {
            showAlertDialogPunch();
        }
    }

    public void reward(View view){
        if(Commons.customer.getRewards() > 0)
            rewardSent();
        else
            Toast.makeText(getApplicationContext(), "Customer's rewards is 0", Toast.LENGTH_SHORT).show();
    }

    public void deleteCustomer(View view){
        showAlertDialog();
    }

    public void showAlertDialog(){
        alertDialogDelete.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
    }

    public void showAlertDialogPunch(){
        alertDialogBuy.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
    }

    public void punchNewCard() {

        String url = ReqConst.SERVER_URL + "punchNewCard";

        pd.show();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parsePunchResponse(response);

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

    public void parsePunchResponse(String json) {

        pd.dismiss();
        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                getCustomer();
            }
            else if (result_code == 106) {
       //         getCustomer();
                showAlertDialogPunch();
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void rewardSent() {

        String url = ReqConst.SERVER_URL + "rewardSent";

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
                params.put("customer_email", email.getText().toString().trim());

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
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                rewards.setText(String.valueOf(Integer.parseInt(rewards.getText().toString().trim())-1));
                Commons.customer.setRewards(Integer.parseInt(rewards.getText().toString().trim()));
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void deleteCustomer() {

        String url = ReqConst.SERVER_URL + "deleteCustomer";

        pd.show();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseDeleteResponse(response);

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

    public void parseDeleteResponse(String json) {

        pd.dismiss();
        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void getCustomer() {

        String url = ReqConst.SERVER_URL + "getCustomer";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseCustomerResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                Toast.makeText(getApplicationContext(), "Refreshing failed", Toast.LENGTH_SHORT).show();

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

    public void parseCustomerResponse(String json) {

        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                JSONObject jsonObject = response.getJSONObject("my_customer");
                Commons.customer.setPunches(Commons.thisMerchant.getPunches() - jsonObject.getInt("punchs"));
                Commons.customer.setRewards(jsonObject.getInt("punchs")/Commons.thisMerchant.getPunches());
                finish();
                intent = new Intent(getApplicationContext(), PunchCardActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            else {
                Toast.makeText(getApplicationContext(), "Refreshing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Refreshing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}


































