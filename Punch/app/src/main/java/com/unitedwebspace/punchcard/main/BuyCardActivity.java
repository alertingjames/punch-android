package com.unitedwebspace.punchcard.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.classes.carousel.CarouselPagerAdapter;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.models.Plan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuyCardActivity extends AppCompatActivity {

    public final static int LOOPS = 1000;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    public static int count = 2; //ViewPager items size

    public static int FIRST_PAGE = 1;
    ArrayList<Plan> plans = new ArrayList<>();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_card);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing");
        pd.setMessage("Wait");

        try{
            boolean newBuyFlag = getIntent().getBooleanExtra("newBuyFlag", false);
            if(newBuyFlag)
                ((TextView) findViewById(R.id.skip)).setVisibility(View.GONE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(Commons.buyCardFromEditProfile)
            ((TextView)findViewById(R.id.skip)).setVisibility(View.GONE);

        pager = (ViewPager) findViewById(R.id.myviewpager);

        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = ((metrics.widthPixels * 3/ 20) * 2);
        pager.setPageMargin(-pageMargin);

        adapter = new CarouselPagerAdapter(this, plans, getSupportFragmentManager());
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pager.addOnPageChangeListener(adapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(3);

        getPlans();

    }

    public void skip(View view){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Commons.buyCardFromEditProfile = false;
    }

    public void getPlans(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ReqConst.SERVER_URL + "getPlans";
        pd.show();
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Server failed", Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseResponse(String json){
        pd.dismiss();
        try{
            JSONObject response = new JSONObject(json);
            JSONArray data = response.getJSONArray("plans");
            for(int i=0; i<data.length(); i++){
                Plan plan = new Plan();
                plan.set_idx(Integer.parseInt(data.getJSONObject(i).getString("id")));
                plan.setPlan(Integer.parseInt(data.getJSONObject(i).getString("plan")));
                plan.setCards(Integer.parseInt(data.getJSONObject(i).getString("cards")));
                plan.setAmount(Float.parseFloat(data.getJSONObject(i).getString("amount")));
                plan.setProductID(data.getJSONObject(i).getString("product_id"));
                if(plan.getAmount()!= 0.0f)
                    plans.add(plan);
            }
            adapter = new CarouselPagerAdapter(this, plans, getSupportFragmentManager());
            pager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}





















