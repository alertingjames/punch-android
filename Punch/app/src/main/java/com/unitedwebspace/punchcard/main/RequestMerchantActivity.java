package com.unitedwebspace.punchcard.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.unitedwebspace.punchcard.models.Merchant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestMerchantActivity extends AppCompatActivity {

    LinearLayout holeFrame1, holeFrame2, cardHoleFrame;
    FrameLayout cardFrame;
    NetworkImageView cardLogoNet, backgroundImage;
    ImageView cardLogo;
    TextView imageFrame, bsName, rwName, rewards, businessName, rewardName, phoneNumber, websiteUrl, socialMediaLink, notes;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_merchant);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        cardFrame = (FrameLayout) findViewById(R.id.cardFrame);
        cardHoleFrame = (LinearLayout) findViewById(R.id.cardHoleFrame);
        holeFrame1 = (LinearLayout) findViewById(R.id.holeFrame1);
        holeFrame2 = (LinearLayout) findViewById(R.id.holeFrame2);

        bsName = (TextView) findViewById(R.id.bsName);
        rwName = (TextView) findViewById(R.id.rwName);
        businessName = (TextView) findViewById(R.id.businessName);
        rewardName = (TextView) findViewById(R.id.rewardName);
        imageFrame = (TextView) findViewById(R.id.imageFrame);
        cardLogo = (ImageView) findViewById(R.id.cardLogo);
        cardLogoNet = (NetworkImageView) findViewById(R.id.cardLogoNet);
        rewards = (TextView) findViewById(R.id.rewards);

        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        websiteUrl = (TextView) findViewById(R.id.websiteUrl);
        socialMediaLink = (TextView) findViewById(R.id.socialMediaLink);
        notes = (TextView) findViewById(R.id.notes);

        backgroundImage = (NetworkImageView) findViewById(R.id.backgroundImage);

        Merchant merchant = Commons.merchant;

        phoneNumber.setText(merchant.getPhoneNumber());
        websiteUrl.setText(merchant.getWebsiteUrl());
        socialMediaLink.setText(merchant.getSocialMediaLink());
        notes.setText(merchant.getNotes());

        backgroundImage.setImageUrl(merchant.getBackgroundImage(), PunchApplication.getInstance().getImageLoader());

        bsName.setText(merchant.getBusinessName());
        rwName.setText(merchant.getRewardName());
        businessName.setText(merchant.getBusinessName());
        rewardName.setText(merchant.getRewardName());
        if(merchant.getLogo().length() > 0)
            cardLogoNet.setImageUrl(merchant.getLogo(), PunchApplication.getInstance().getImageLoader());

        try{
            Typeface font = Typeface.createFromAsset(getAssets(), "font/"+(new Font().getFontPathFromMap(merchant.getBusinessFont())));
            bsName.setTypeface(font);
            font = Typeface.createFromAsset(getAssets(), "font/"+(new Font().getFontPathFromMap(merchant.getRewardFont())));
            rwName.setTypeface(font);
        }catch (RuntimeException e){
            e.printStackTrace();
        }

  //      rewards.setText(String.valueOf(merchant.getRewards()));

        int color;
        try{
            color = Color.parseColor(merchant.getCardColor());
        }catch (NumberFormatException e){
            e.printStackTrace();
            if(merchant.getCardColor().length()>7)
                color = Color.parseColor(merchant.getCardColor().substring(0, 7));
            else color = Color.GREEN;
        }

        setupCardFrameBackground(cardFrame, 0xffffffff, color);
        setupCardHoleFrameBackground(cardHoleFrame, color, color);
        setupCardBackground(imageFrame, 0xffffffff, color);

        holeFrame1.removeAllViews();
        holeFrame2.removeAllViews();

        int holes = merchant.getPunches();

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
        }else if (holes > 0 && holes <= 10){
            for(int i=1; i<holes+1; i++) {
                View hole = getLayoutInflater().inflate(R.layout.edit_holebox, holeFrame1, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holeFrame1.addView(hole);
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

    public void requestCard(View view){
        requestCard();
    }

    public void phoneCall(View view){
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + Commons.merchant.getPhoneNumber().replace("+","").replace("-", ""))));

    }

    public void shareEmail(View view){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",Commons.merchant.getEmail(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject...");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body...");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void requestCard() {

        String url = ReqConst.SERVER_URL + "requestCard";

        pd.show();

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
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("business_id", String.valueOf(Commons.merchant.get_bidx()));
                params.put("customer_email", Commons.thisCustomer.getEmail());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseMyRewardsResponse(String json) {

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































