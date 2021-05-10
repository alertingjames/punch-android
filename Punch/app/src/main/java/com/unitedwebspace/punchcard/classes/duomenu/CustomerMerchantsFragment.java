package com.unitedwebspace.punchcard.classes.duomenu;

/**
 * Created by sonback123456 on 4/17/2018.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.adapters.MerchantListAdapter;
import com.unitedwebspace.punchcard.classes.carousel.CarouselPagerAdapter;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.main.RequestMerchantActivity;
import com.unitedwebspace.punchcard.models.Card;
import com.unitedwebspace.punchcard.models.Merchant;
import com.unitedwebspace.punchcard.models.Plan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class CustomerMerchantsFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {

    TextView listTitle;
    SwipyRefreshLayout ui_RefreshLayout;
    ListView listView;
    ArrayList<Merchant> merchants = new ArrayList<>();
    MerchantListAdapter adapter = new MerchantListAdapter(this);
    ProgressDialog pd;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_cards_fragment, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((ImageView)toolbar.getChildAt(1)).setBackgroundResource(R.drawable.search);
        toolbar.getChildAt(1).setVisibility(View.VISIBLE);
        toolbar.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((toolbar.getChildAt(0).getVisibility()==View.GONE)){
                    (toolbar.getChildAt(0)).setVisibility(View.VISIBLE);
                    getActivity().setTitle("");
                    (toolbar.getChildAt(1)).setBackgroundResource(R.drawable.cancel);
                }else {
                    (toolbar.getChildAt(0)).setVisibility(View.GONE);
                    getActivity().setTitle("All Merchants");
                    ((EditText)(toolbar.getChildAt(0))).setText("");
                    (toolbar.getChildAt(1)).setBackgroundResource(R.drawable.search);
                }
            }
        });

        ((EditText)toolbar.getChildAt(0)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    adapter.filter(((EditText)toolbar.getChildAt(0)).getText().toString());
                    adapter.notifyDataSetChanged();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });

        ui_RefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        listView=(ListView)view.findViewById(R.id.list);
        adapter.setDatas(merchants);
        listView.setAdapter(adapter);

        getAllCards();

        return view;
    }

    private ArrayList<Merchant> getData(){
        ArrayList<Merchant> merchants = new ArrayList<>();
        int[] holes = {8, 16};
        int[] rewards = {2, 3};
        String[] businessNames = {"First Business Name", "Second Business Name"};
        String[] rewardNames = {"First Reward Name", "Second Business Name"};
        String[] bnFonts = {"font/LibreBodoni-BoldItalic.otf", "font/lmmonoproplt10-boldoblique.otf"};
        String[] rwFonts = {"font/vanchrome front.ttf", "font/Spirax-Regular.ttf"};
        String[] logos = {"https://thumbs.dreamstime.com/t/purple-petunia-flowers-bed-beautiful-blurred-nature-background-banner-website-garden-concept-toned-54798543.jpg",
                "https://cdn.wallpapersafari.com/18/17/KZn0LD.jpg"};
        String[] cardColors = {"#9933ff", "#009900"};

        String[] backgrounds = {"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT2E2KdMAPbdXlgeRHiyxYz1ZSRVYGGQ0p2ZiidQMN82Npijq0m",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQjUaOFYP3VArzX7OZi6RySkrLiSjjyWzvbi0hpJPgAbkrpCl_x"};

        for(int i=0; i<holes.length; i++){
            Merchant merchant = new Merchant();
            merchant.setBusinessFont(bnFonts[i]);
            merchant.setRewardFont(rwFonts[i]);
            merchant.setBusinessName(businessNames[i]);
            merchant.setRewardName(rewardNames[i]);
            merchant.setLogo(logos[i]);
            merchant.setPunches(holes[i]);
            merchant.setRewards(rewards[i]);
            merchant.setBackgroundImage(backgrounds[i]);
            merchant.setCardColor(String.valueOf(Color.parseColor(cardColors[i])));

            merchants.add(merchant);
        }

        return merchants;
    }

    public void gotoRequestMerchant(Merchant merchant){
        Commons.merchant = merchant;
        Intent intent = new Intent(getActivity(), RequestMerchantActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void getAllCards(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ReqConst.SERVER_URL + "getallcards";
        pd.show();
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response===>", response.toString());
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

            JSONArray data = response.getJSONArray("all_cards");
            for(int i=0; i<data.length(); i++){
                Merchant merchant = new Merchant();
                merchant.set_idx(Integer.parseInt(data.getJSONObject(i).getString("id")));
                merchant.set_bidx(Integer.parseInt(data.getJSONObject(i).getString("business_id")));
                merchant.setEmail(data.getJSONObject(i).getString("business_email"));
                merchant.setBusinessName(data.getJSONObject(i).getString("business_name"));
                merchant.setRewardName(data.getJSONObject(i).getString("reward"));
                merchant.setPhoneNumber(data.getJSONObject(i).getString("business_phonenumber"));
                merchant.setWebsiteUrl(data.getJSONObject(i).getString("website_url"));
                merchant.setSocialMediaLink(data.getJSONObject(i).getString("social_media_link"));
                merchant.setBusinessFont(data.getJSONObject(i).getString("font1"));
                merchant.setRewardFont(data.getJSONObject(i).getString("font2"));
                merchant.setNotes(data.getJSONObject(i).getString("notes"));
                merchant.setPunches(data.getJSONObject(i).getInt("cards_for_free"));
                merchant.setCardColor(data.getJSONObject(i).getString("color"));
                merchant.setLogo(data.getJSONObject(i).getString("logo"));
                merchant.setBackgroundImage(data.getJSONObject(i).getString("background"));
                merchant.setCardNumbers(data.getJSONObject(i).getInt("card_numbers"));

                merchants.add(merchant);
            }
            adapter.setDatas(merchants);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}

























