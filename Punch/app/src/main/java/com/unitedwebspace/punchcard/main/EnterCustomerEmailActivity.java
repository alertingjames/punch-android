package com.unitedwebspace.punchcard.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.unitedwebspace.punchcard.Entities.Font;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.models.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnterCustomerEmailActivity extends AppCompatActivity {

    LinearLayout holeFrame1, holeFrame2, cardHoleFrame;
    FrameLayout cardFrame;
    NetworkImageView cardBackground;
    TextView imageFrame, businessName, rewardName;
    EditText email;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_customer_email);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        holeFrame1 = (LinearLayout) findViewById(R.id.holeFrame1);
        holeFrame2 = (LinearLayout) findViewById(R.id.holeFrame2);
        cardHoleFrame = (LinearLayout) findViewById(R.id.cardHoleFrame);
        cardFrame = (FrameLayout) findViewById(R.id.cardFrame);
        cardBackground = (NetworkImageView) findViewById(R.id.cardBackground);
        imageFrame = (TextView) findViewById(R.id.imageFrame);
        businessName = (TextView) findViewById(R.id.businessName);
        rewardName = (TextView) findViewById(R.id.rewardName);

        email = (EditText) findViewById(R.id.customerEmail);

        businessName.setText(Commons.thisMerchant.getBusinessName());
        rewardName.setText(Commons.thisMerchant.getRewardName());

        cardBackground.setImageUrl(Commons.thisMerchant.getLogo(), PunchApplication.getInstance().getImageLoader());

        try{
            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/"+(new Font().getFontPathFromMap(Commons.thisMerchant.getBusinessFont())));
            businessName.setTypeface(font);
            font = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/"+(new Font().getFontPathFromMap(Commons.thisMerchant.getRewardFont())));
            rewardName.setTypeface(font);
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        initHoles(3);

        addHoles(Commons.thisMerchant.getPunches());

        int color = Color.parseColor(Commons.thisMerchant.getCardColor());

        setupCardFrameBackground(cardFrame, 0xffffffff, color);
        setupCardHoleFrameBackground(cardHoleFrame, color, color);
        setupCardBackground(imageFrame, 0xffffffff, color);
    }

    private void addHoles(int holes){

        if(holes > 10){
            for(int i=4; i<11; i++) {
                View hole = getLayoutInflater().inflate(R.layout.holebox, holeFrame1, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holeFrame1.addView(hole);
            }
            for(int i=11; i<holes+1; i++) {
                View hole = getLayoutInflater().inflate(R.layout.holebox, holeFrame2, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holeFrame2.addView(hole);
            }
        }else if (holes > 3 && holes <= 10){
            for(int i=4; i<holes+1; i++) {
                View hole = getLayoutInflater().inflate(R.layout.holebox, holeFrame1, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holeFrame1.addView(hole);
            }
        }else initHoles(3);

    }

    private void initHoles(int holes){

        holeFrame1.removeAllViews();
        holeFrame2.removeAllViews();

        for(int i=1; i<holes+1; i++) {
            View hole = getLayoutInflater().inflate(R.layout.holebox, holeFrame1, false);
            final TextView holeText = (TextView) hole.findViewById(R.id.hole);
            holeText.setText(String.valueOf(i));
            holeFrame1.addView(hole);
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

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void enter(View view){
        if(email.getText().length() == 0){
            email.setError("Enter customer's email");
            return;
        }else if(!isValidEmail(email.getText().toString().trim())){
            email.setError("Enter customer's valid email");
            return;
        }
        punchNewCard();
    }

    public void punchNewCard() {

        String url = ReqConst.SERVER_URL + "punchNewCard";

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

                params.put("business_id", String.valueOf(Commons.thisMerchant.get_bidx()));
                params.put("email", email.getText().toString().trim());

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
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
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































