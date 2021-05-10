package com.unitedwebspace.punchcard.classes.duomenu;

/**
 * Created by sonback123456 on 4/15/2018.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.adapters.CarouselPagerFromHomeAdapter;
import com.unitedwebspace.punchcard.classes.carousel.CarouselPagerAdapter;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.models.Plan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class CardsFragment extends Fragment {

    public final static int LOOPS = 1000;
    public CarouselPagerFromHomeAdapter adapter;
    public ViewPager pager;
    public static int count = 2; //ViewPager items size

    public static int FIRST_PAGE = 1;
    ProgressDialog pd;
    ArrayList<Plan> plans = new ArrayList<>();
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        pager = (ViewPager) view.findViewById(R.id.myviewpager);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getChildAt(1).setVisibility(View.GONE);
        toolbar.getChildAt(0).setVisibility(View.GONE);

        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = ((metrics.widthPixels * 3/ 20) * 2);
        pager.setPageMargin(-pageMargin);

        adapter = new CarouselPagerFromHomeAdapter(this, plans, getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pager.addOnPageChangeListener(adapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(3);

        getPlans();

        return view;
    }

    public void getPlans(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                Toast.makeText(getActivity(), "Server failed", Toast.LENGTH_SHORT).show();
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
                if(plan.getAmount() != 0.0f)
                    plans.add(plan);
            }
            Log.d("PlanSize===>", String.valueOf(plans.size()));
            adapter = new CarouselPagerFromHomeAdapter(this, plans, getActivity().getSupportFragmentManager());
            pager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}





















