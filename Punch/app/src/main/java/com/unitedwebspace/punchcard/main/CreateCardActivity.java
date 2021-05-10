package com.unitedwebspace.punchcard.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.unitedwebspace.punchcard.Entities.Font;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.adapters.FontListAdapter;
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

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateCardActivity extends Activity {

    LinearLayout holeFrame1, holeFrame2, cardHoleFrame;
    FrameLayout cardFrame;
    EditText holePunches;
    LinearLayout pictureItemFrame, fontListFrame;
    FrameLayout layout;
    File imageFile = null;
    ImageView checkMark, cardBackground;
    ListView list;
    FontListAdapter adapter;
    TextView imageFrame;
    int userID = 0;
    String font1 = "", font2 = "";
    String cardColor = "";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        userID = Integer.parseInt(getIntent().getStringExtra("id"));
        Log.d("CardUserId===>", String.valueOf(userID));

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        holeFrame1 = (LinearLayout) findViewById(R.id.holeFrame1);
        holeFrame2 = (LinearLayout) findViewById(R.id.holeFrame2);
        cardHoleFrame = (LinearLayout) findViewById(R.id.cardHoleFrame);
        pictureItemFrame = (LinearLayout) findViewById(R.id.pictureItemFrame);

        cardFrame = (FrameLayout) findViewById(R.id.cardFrame);
        holePunches = (EditText) findViewById(R.id.holePunches);
        layout = (FrameLayout) findViewById(R.id.layout);
        checkMark = (ImageView) findViewById(R.id.checkMark);
        cardBackground = (ImageView) findViewById(R.id.cardBackground);
        fontListFrame = (LinearLayout) findViewById(R.id.fontList);
        list = (ListView) findViewById(R.id.list);
        imageFrame = (TextView) findViewById(R.id.imageFrame);

        Font font = new Font();

        adapter = new FontListAdapter(this, font);

        list.setAdapter(adapter);

        initHoles(3);

        holePunches.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    if (Integer.parseInt(holePunches.getText().toString()) > 2 && Integer.parseInt(holePunches.getText().toString()) < 21)
                        addHoles(Integer.parseInt(holePunches.getText().toString()));
                    else {
                        showAlertDialog("Please input the value between 3 and 20");
                        initHoles(3);
                    }
                }catch (NullPointerException e){

                }catch (NumberFormatException e){
                    initHoles(3);
                }
            }
        });

    }

    public void skip(View view){
        Intent intent = new Intent(getApplicationContext(), BuyCardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
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

    public void getColor(View view){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, 0xff0000ff, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                setupCardFrameBackground(cardFrame, 0xffffffff, color);
                setupCardHoleFrameBackground(cardHoleFrame, color, color);
                setupCardBackground(imageFrame, 0xffffffff, color);
                cardColor = String.format("#%06X", 0xFFFFFF & color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });

        dialog.show();
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

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Hint!");
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog.show();

    }

    public void setupLogo(View view){
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

    public void dismisFrame(View view){
        if(pictureItemFrame.getVisibility() == View.VISIBLE){
            pictureItemFrame.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
            pictureItemFrame.startAnimation(animation);
            layout.setVisibility(View.GONE);
        }
        if(fontListFrame.getVisibility() == View.VISIBLE){
            fontListFrame.setVisibility(View.GONE);
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
        cardBackground.setImageBitmap(thumbnail);
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
                cardBackground.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int selfontflag = 0;

    public void selectBusinessFont(View view){
        fontListFrame.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        selfontflag = 1;
    }

    public void selectRewardFont(View view){
        fontListFrame.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        selfontflag = 2;
    }

    public void setupFont(String fontPath){
        fontListFrame.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        if(selfontflag == 1){
            font1 = "font/"+fontPath;
            Log.d("Font1===>", font1.substring(0, font1.length()-4).replace("font/", ""));
            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), font1);
            ((TextView)findViewById(R.id.businessName)).setTypeface(font);
            ((TextView)findViewById(R.id.businessName)).getText().toString().replaceAll(System.getProperty("line.separator"), "");
        }else if(selfontflag == 2){
            font2 = "font/"+fontPath;
            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), font2);
            ((TextView)findViewById(R.id.rewardName)).setTypeface(font);
            ((TextView)findViewById(R.id.rewardName)).getText().toString().replaceAll(System.getProperty("line.separator"), "");
        }
    }

    public void next(View view){
        if(holePunches.getText().length() == 0){
            holePunches.setError("Enter number of punchcard holes");
            return;
        }
        if(cardColor.length() == 0){
            Toast.makeText(getApplicationContext(), "Select card color", Toast.LENGTH_SHORT).show();
            return;
        }
        if(font1.length() == 0){
            Toast.makeText(getApplicationContext(), "Select a font for business name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(font2.length() == 0){
            Toast.makeText(getApplicationContext(), "Select a font for reward name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(imageFile == null){
            Toast.makeText(getApplicationContext(), "Take logo image", Toast.LENGTH_SHORT).show();
            return;
        }

        createPunchCard();
    }


    public void createPunchCard() {

        String url = ReqConst.SERVER_URL + "createPunchCard";

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

                params.put("user_id", String.valueOf(userID));
                params.put("cards_for_free", holePunches.getText().toString().trim());
                params.put("color", cardColor);
                params.put("font1", font1.substring(0, font1.length()-4).replace("font/", ""));
                params.put("font2", font2.substring(0, font2.length()-4).replace("font/", ""));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseResponse(String json) {

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                uploadLogo(userID, imageFile);
            }
            else {
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            e.printStackTrace();
        }

    }

    public void uploadLogo(int user_id, File file) {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("user_id", String.valueOf(user_id));

            String url = ReqConst.SERVER_URL + "uploadLogo";

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
                //        Preference.getInstance().put(getApplicationContext(),
//                PrefConst.CREATED_CARD, "true");
                Intent intent = new Intent(getApplicationContext(), BuyCardActivity.class);
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























