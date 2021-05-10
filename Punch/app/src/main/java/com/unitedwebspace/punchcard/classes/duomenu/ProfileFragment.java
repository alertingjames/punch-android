package com.unitedwebspace.punchcard.classes.duomenu;

/**
 * Created by sonback123456 on 4/15/2018.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
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
import com.unitedwebspace.punchcard.main.BuyCardActivity;
import com.unitedwebspace.punchcard.main.CreateCardActivity;
import com.unitedwebspace.punchcard.main.CustomerHomeActivity;
import com.unitedwebspace.punchcard.main.EditCardActivity;
import com.unitedwebspace.punchcard.main.HomeActivity;
import com.unitedwebspace.punchcard.main.RegisterBusinessActivity;
import com.unitedwebspace.punchcard.models.Merchant;
import com.unitedwebspace.punchcard.preference.PrefConst;
import com.unitedwebspace.punchcard.preference.Preference;
import com.unitedwebspace.punchcard.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment implements View.OnClickListener {

    TextView name, email, businessName, rewardName, phoneNumber, websiteUrl, socialMediaLink, notes, editFrameTitle, editCancelButton, editSubmitButton, cards;
    LinearLayout holeFrame1, holeFrame2, cardHoleFrame, editAlert;
    FrameLayout cardFrame, layout;
    NetworkImageView cardLogoNet, backgroundImageNet;
    ImageView cardLogo, backgroundImage;
    TextView imageFrame, bsName, rwName;
    EditText editBox;
    TextView takePhotoButton, pickPhotoButton, dismisFrameButton, editBackgroundButton, editCardButton, buyCardButton, editNameButton, editEmailButton, editBusinessNameButton,
            editRewardNameButton, editPhoneButton, editWebsiteUrlButton, editSocialMediaButton, editNotesButton, deleteAccountButton;
    int editItem = 0;
    LinearLayout pictureItemFrame, alertDialogDelete;
    TextView noButton, deleteButton;
    File imageFile;
    ProgressDialog pd;
    Toolbar toolbar;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Merchant merchant = Commons.thisMerchant;

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getChildAt(1).setVisibility(View.GONE);
        toolbar.getChildAt(0).setVisibility(View.GONE);

        alertDialogDelete = (LinearLayout)view.findViewById(R.id.alertDialog);
        noButton = (TextView)view.findViewById(R.id.noButton);
        deleteButton = (TextView)view.findViewById(R.id.deleteButton);

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
                deleteAccount();
            }
        });

        name = (TextView) view.findViewById(R.id.name);
        name.setText(merchant.getName());
        email = (TextView) view.findViewById(R.id.email);
        email.setText(merchant.getEmail());
        businessName = (TextView) view.findViewById(R.id.businessName);
        businessName.setText(merchant.getBusinessName());
        rewardName = (TextView)view.findViewById(R.id.rewardName);
        rewardName.setText(merchant.getRewardName());
        phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        phoneNumber.setText(merchant.getPhoneNumber());
        websiteUrl = (TextView)view.findViewById(R.id.websiteUrl);
        websiteUrl.setText(merchant.getWebsiteUrl());
        socialMediaLink = (TextView)view.findViewById(R.id.socialMediaLink);
        socialMediaLink.setText(merchant.getSocialMediaLink());
        notes = (TextView)view.findViewById(R.id.notes);
        notes.setText(merchant.getNotes());

        cards = (TextView) view.findViewById(R.id.cards);
        cards.setText(merchant.getCardNumbers() + " Cards");

        holeFrame1 = (LinearLayout) view.findViewById(R.id.holeFrame1);
        holeFrame2 = (LinearLayout) view.findViewById(R.id.holeFrame2);
        cardHoleFrame = (LinearLayout) view.findViewById(R.id.cardHoleFrame);
        cardFrame = (FrameLayout) view.findViewById(R.id.cardFrame);
        cardLogoNet = (NetworkImageView) view.findViewById(R.id.cardLogoNet);
        cardLogo = (ImageView) view.findViewById(R.id.cardLogo);
        backgroundImage = (ImageView) view.findViewById(R.id.backgroundImage);
        backgroundImageNet = (NetworkImageView) view.findViewById(R.id.backgroundImageNet);
        imageFrame = (TextView) view.findViewById(R.id.imageFrame);
        bsName = (TextView) view.findViewById(R.id.bsName);
        rwName = (TextView) view.findViewById(R.id.rwName);
        layout = (FrameLayout) view.findViewById(R.id.layout);
        editAlert = (LinearLayout)view.findViewById(R.id.editAlert);
        editFrameTitle = (TextView) view.findViewById(R.id.editFrameTitle);
        editBox = (EditText) view.findViewById(R.id.editBox);
        editCancelButton = (TextView) view.findViewById(R.id.editCancelButton);
        editCancelButton.setOnClickListener(this);
        editSubmitButton = (TextView) view.findViewById(R.id.editSubmitButton);
        editSubmitButton.setOnClickListener(this);
        deleteAccountButton = (TextView) view.findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(this);
        buyCardButton = (TextView) view.findViewById(R.id.buyCardButton);
        buyCardButton.setOnClickListener(this);
        editBackgroundButton = (TextView)view.findViewById(R.id.editBackgroundButton);
        editBackgroundButton.setOnClickListener(this);

        takePhotoButton = (TextView)view.findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(this);
        pickPhotoButton = (TextView)view.findViewById(R.id.pickPhotoButton);
        pickPhotoButton.setOnClickListener(this);
        dismisFrameButton = (TextView)view.findViewById(R.id.dismisFrameButton);
        dismisFrameButton.setOnClickListener(this);

        pictureItemFrame = (LinearLayout) view.findViewById(R.id.pictureItemFrame);

        editNameButton = (TextView)view.findViewById(R.id.editNameButton);
        editNameButton.setOnClickListener(this);
        editEmailButton = (TextView)view.findViewById(R.id.editEmailButton);
        editEmailButton.setOnClickListener(this);
        editBusinessNameButton = (TextView)view.findViewById(R.id.editBusinessNameButton);
        editBusinessNameButton.setOnClickListener(this);
        editRewardNameButton = (TextView)view.findViewById(R.id.editRewardNameButton);
        editRewardNameButton.setOnClickListener(this);
        editPhoneButton = (TextView)view.findViewById(R.id.editPhoneButton);
        editPhoneButton.setOnClickListener(this);
        editWebsiteUrlButton = (TextView)view.findViewById(R.id.editWebsiteUrlButton);
        editWebsiteUrlButton.setOnClickListener(this);
        editSocialMediaButton = (TextView)view.findViewById(R.id.editSocialMediaButton);
        editSocialMediaButton.setOnClickListener(this);
        editNotesButton = (TextView)view.findViewById(R.id.editNotesButton);
        editNotesButton.setOnClickListener(this);
        editCardButton = (TextView)view.findViewById(R.id.editCardButton);
        editCardButton.setOnClickListener(this);

        cardLogoNet.setImageUrl(merchant.getLogo(), PunchApplication.getInstance().getImageLoader());
        backgroundImageNet.setImageUrl(merchant.getBackgroundImage(), PunchApplication.getInstance().getImageLoader());

        bsName.setText(Commons.thisMerchant.getBusinessName());
        rwName.setText(Commons.thisMerchant.getRewardName());

        try{
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/"+(new Font().getFontPathFromMap(merchant.getBusinessFont())));
            bsName.setTypeface(font);
            font = Typeface.createFromAsset(getActivity().getAssets(), "font/"+(new Font().getFontPathFromMap(merchant.getRewardFont())));
            rwName.setTypeface(font);
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        addHoles(merchant.getPunches());

        int color = Color.parseColor(merchant.getCardColor());

        setupCardFrameBackground(cardFrame, 0xffffffff, color);
        setupCardHoleFrameBackground(cardHoleFrame, color, color);
        setupCardBackground(imageFrame, 0xffffffff, color);

        return view;
    }

    private void addHoles(int holes){

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
        }

    }

    private void initHoles(int holes){

        holeFrame1.removeAllViews();
        holeFrame2.removeAllViews();

        for(int i=1; i<holes+1; i++) {
            View hole = getLayoutInflater().inflate(R.layout.edit_holebox, holeFrame1, false);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editNameButton:
                showEditAlert(editItem = 1);
                break;
            case R.id.editEmailButton:
                showEditAlert(editItem = 2);
                break;
            case R.id.editBusinessNameButton:
                showEditAlert(editItem = 3);
                break;
            case R.id.editRewardNameButton:
                showEditAlert(editItem = 4);
                break;
            case R.id.editPhoneButton:
                showEditAlert(editItem = 5);
                break;
            case R.id.editWebsiteUrlButton:
                showEditAlert(editItem = 6);
                break;
            case R.id.editSocialMediaButton:
                showEditAlert(editItem = 7);
                break;
            case R.id.editNotesButton:
                showEditAlert(editItem = 8);
                break;
            case R.id.editCancelButton:
                dismisEditAlert();
                editItem = 0;
                break;
            case R.id.editSubmitButton:
                submitEditBox(editItem);
                break;
            case R.id.deleteAccountButton:
                showAlertDialog();
                break;
            case R.id.editCardButton:
                editCardPage();
                break;
            case R.id.buyCardButton:
                buyCardsPage();
                break;
            case R.id.editBackgroundButton:
                editBackground();
                break;
            case R.id.takePhotoButton:
                takePhoto();
                break;
            case R.id.pickPhotoButton:
                pickPhoto();
                break;
            case R.id.dismisFrameButton:
                dismisFrame();
                break;
        }
    }

    public void showEditAlert(final int item){
        if(item == 1) editFrameTitle.setText("Change Name");
        else if(item == 2) editFrameTitle.setText("Change Email");
        else if(item == 3) editFrameTitle.setText("Change Business Name");
        else if(item == 4) editFrameTitle.setText("Change Reward Name");
        else if(item == 5) editFrameTitle.setText("Change Phone Number");
        else if(item == 6) editFrameTitle.setText("Change Website URL");
        else if(item == 7) editFrameTitle.setText("Change Social Media Link");
        else if(item == 8) editFrameTitle.setText("Change Notes");
        editAlert.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public void dismisEditAlert(){
        editAlert.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        editBox.setText("");
    }

    public void submitEditBox(final int item){
        if(editBox.getText().length() ==0){
            editBox.setError("Enter edit content");
            return;
        }
        if(item == 1) updateBusinessDetail("name", editBox.getText().toString().trim());
        else if(item == 2) {
            if(!isValidEmail(editBox.getText().toString().trim())){
                editBox.setError("Enter valid email");
                return;
            }
            updateBusinessDetail("email", editBox.getText().toString().trim());
        }
        else if(item == 3) updateBusinessDetail("business_name", editBox.getText().toString().trim());
        else if(item == 4) updateBusinessDetail("reward", editBox.getText().toString().trim());
        else if(item == 5) {
            if(!isValidMobile(editBox.getText().toString().trim())){
                editBox.setError("Enter valid phone number");
                return;
            }
            updateBusinessDetail("phonenumber", editBox.getText().toString().trim());
        }
        else if(item == 6) updateBusinessDetail("website_url", editBox.getText().toString().trim());
        else if(item == 7) updateBusinessDetail("social_media_link", editBox.getText().toString().trim());
        else if(item == 8) updateBusinessDetail("notes", editBox.getText().toString().trim());

        editItem = 0;
    }

    public void editCardPage(){
        Commons.profileFragment = this;
        Intent intent = new Intent(getActivity(), EditCardActivity.class);
        startActivity(intent);
    }

    public void buyCardsPage(){
        Commons.buyCardFromEditProfile = true;
        Intent intent = new Intent(getActivity(), BuyCardActivity.class);
        startActivity(intent);
    }

    public void editBackground(){
        pictureItemFrame.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
        pictureItemFrame.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    public void dismisFrame(){
        if(pictureItemFrame.getVisibility() == View.VISIBLE){
            pictureItemFrame.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.top_down);
            pictureItemFrame.startAnimation(animation);
            layout.setVisibility(View.GONE);
        }
    }

    public void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.TAKE_FROM_CAMERA);
        pictureItemFrame.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
    }

    public void pickPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),Constants.PICK_FROM_GALLERY);
        pictureItemFrame.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.PICK_FROM_GALLERY:

                if (resultCode == RESULT_OK) {
                    onSelectFromGalleryResult(data);
                }
                break;

            case Constants.TAKE_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    onCaptureImageResult(data);
                }

                break;
            }
        }
    }

    private void onCaptureImageResult(Intent data) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Pictures");
        if (!dir.exists())
            dir.mkdirs();

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageFile = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            imageFile.createNewFile();
            fo = new FileOutputStream(imageFile);
            fo.write(byteArrayOutputStream.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        backgroundImageNet.setVisibility(View.GONE);
        backgroundImage.setVisibility(View.VISIBLE);
        backgroundImage.setImageBitmap(thumbnail);
        submitBackgroundImage();
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), data.getData());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageFile = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    imageFile.createNewFile();
                    fo = new FileOutputStream(imageFile);
                    fo.write(byteArrayOutputStream.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                backgroundImageNet.setVisibility(View.GONE);
                backgroundImage.setVisibility(View.VISIBLE);
                backgroundImage.setImageBitmap(bitmap);
                submitBackgroundImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitBackgroundImage() {
        uploadBackgroundImage(Commons.thisMerchant.get_idx(), imageFile);
    }

    public void updateBusinessDetail(final String option, final String value) {

        String url = ReqConst.SERVER_URL + "updateBusinessDetails";

        pd.show();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                pd.dismiss();
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(Commons.thisMerchant.get_idx()));
                params.put("option", option);
                params.put("value", value);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseUpdateResponse(String json) {

        pd.dismiss();
        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                dismisEditAlert();
                refreshFragment();
            }
            else {
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void uploadBackgroundImage(int user_id, File file) {

        try {
            pd.show();
            final Map<String, String> params = new HashMap<>();
            params.put("user_id", String.valueOf(user_id));

            String url = ReqConst.SERVER_URL + "uploadBackground";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(getActivity(),"Processing failed", Toast.LENGTH_SHORT).show();
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    parseRegisterPictureResponse(json);
                }
            }, file, "file", params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            PunchApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {

            e.printStackTrace();
            pd.dismiss();
            Toast.makeText(getActivity(),"Processing failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void parseRegisterPictureResponse(String json) {

        pd.dismiss();
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if (result_code == 200) {
                imageFile=null;
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    public void refreshFragment() {

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

                params.put("email", Preference.getInstance().getValue(getContext(), PrefConst.EMAIL, ""));
                params.put("password", Preference.getInstance().getValue(getContext(), PrefConst.PASSWORD, ""));
                String user = Preference.getInstance().getValue(getContext(), PrefConst.USER, "");
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

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteAccount() {

        String url = ReqConst.SERVER_URL + "deleteAccount";

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
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(Commons.thisMerchant.get_idx()));

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
                Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.logout();
                getActivity().finish();
            }
            else {
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void showAlertDialog(){
        alertDialogDelete.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
    }
}








































