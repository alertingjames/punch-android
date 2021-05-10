package com.unitedwebspace.punchcard.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.android.volley.toolbox.StringRequest;
import com.unitedwebspace.punchcard.*;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
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

public class RegisterBusinessActivity extends AppCompatActivity {

    EditText businessName, reward, phoneNumber, websiteUrl, socialMediaLink, notes;
    TextView selectBackgroundButton, nextButton, skipButton;
    ImageView checkMark;
    LinearLayout pictureItemFrame;
    FrameLayout layout;
    File imageFile = null;
    int userID = 0;
    ProgressDialog pd;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.unitedwebspace.punchcard.R.layout.activity_register_business);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        userID = Integer.parseInt(getIntent().getStringExtra("id"));
        Log.d("USERID===>", String.valueOf(userID));

        selectBackgroundButton = (TextView) findViewById(R.id.selectBackground);
        checkMark = (ImageView) findViewById(R.id.checkMark);
        pictureItemFrame = (LinearLayout) findViewById(R.id.pictureItemFrame);
        layout = (FrameLayout) findViewById(R.id.layout);

        businessName = (EditText) findViewById(R.id.businessName);
        reward = (EditText) findViewById(R.id.reward);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        websiteUrl = (EditText) findViewById(R.id.websiteUrl);
        socialMediaLink = (EditText) findViewById(R.id.socialMediaLink);
        notes = (EditText) findViewById(R.id.notes);
    }

    public void skip(View view){
        Intent intent = new Intent(getApplicationContext(), CreateCardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void selectBackground(View view){
        pictureItemFrame.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_up);
        pictureItemFrame.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
            }
        }, 200);

    }

    public void next(View view){
        if(businessName.getText().length() == 0){
            businessName.setError("Enter business name");
            return;
        }
        if(reward.getText().length() == 0){
            reward.setError("Enter reward name");
            return;
        }
        if(imageFile == null){
            Toast.makeText(getApplicationContext(), "Take background image", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phoneNumber.getText().length() > 0){
            if(!isValidMobile(phoneNumber.getText().toString().trim())) {
                phoneNumber.setError("Enter valid phone number");
                return;
            }
        }
        registerBusinessInfo();
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public void dismisFrame(View view){
        if(pictureItemFrame.getVisibility() == View.VISIBLE){
            pictureItemFrame.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
            pictureItemFrame.startAnimation(animation);
            layout.setVisibility(View.GONE);
        }
    }

    public void takePhoto(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.TAKE_FROM_CAMERA);
        pictureItemFrame.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
    }

    public void pickPhoto(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),Constants.PICK_FROM_GALLERY);
        pictureItemFrame.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        checkMark.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
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

                checkMark.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerBusinessInfo() {

        String url = ReqConst.SERVER_URL + "createBusinessDetails";

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

                params.put("user_id", String.valueOf(userID));
                params.put("business_name", businessName.getText().toString().trim());
                params.put("reward", reward.getText().toString().trim());
                try{
                    params.put("phonenumber", phoneNumber.getText().toString().trim());
                }catch (NullPointerException e){
                    params.put("phonenumber", "");
                }
                try{
                    params.put("website_url", websiteUrl.getText().toString().trim());
                }catch (NullPointerException e){
                    params.put("website_url", "");
                }
                try{
                    params.put("social_media_link", socialMediaLink.getText().toString().trim());
                }catch (NullPointerException e){
                    params.put("social_media_link", "");
                }
                try{
                    params.put("notes", notes.getText().toString().trim());
                }catch (NullPointerException e){
                    params.put("notes", "");
                }

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                uploadBackgroundImage(userID, imageFile);
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void uploadBackgroundImage(int user_id, File file) {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("user_id", String.valueOf(user_id));

            String url = ReqConst.SERVER_URL + "uploadBackground";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Processing failed", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(),"Processing failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void parseRegisterPictureResponse(String json) {

        pd.dismiss();
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if (result_code == 200) {
                imageFile=null;
      //          Preference.getInstance().put(getApplicationContext(), PrefConst.REGISTERED_BUSINESS, "true");
                Intent intent = new Intent(getApplicationContext(), CreateCardActivity.class);
                intent.putExtra("id", String.valueOf(userID));
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


































