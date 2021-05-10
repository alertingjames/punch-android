package com.unitedwebspace.punchcard.classes.duomenu;

/**
 * Created by sonback123456 on 4/15/2018.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.support.v7.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.adapters.AcceptedCustomerListAdapter;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.main.EnterCustomerEmailActivity;
import com.unitedwebspace.punchcard.main.HomeActivity;
import com.unitedwebspace.punchcard.main.PunchCardActivity;
import com.unitedwebspace.punchcard.models.Card;
import com.unitedwebspace.punchcard.models.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class CustomersFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {

    TextView listTitle;
    SwipyRefreshLayout ui_RefreshLayout;
    ListView listView;
    ArrayList<Customer> customers = new ArrayList<>();
    AcceptedCustomerListAdapter adapter = new AcceptedCustomerListAdapter(this);
    ProgressDialog pd;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        listTitle = (TextView) view.findViewById(R.id.listTitle);

        ui_RefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        listView=(ListView)view.findViewById(R.id.list);
//        adapter.setDatas(getAcceptedCustomersData());
        adapter.setDatas(customers);
        listView.setAdapter(adapter);

        view.findViewById(R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnterCustomerEmailActivity.class);
                startActivity(intent);
            }
        });

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
                    getActivity().setTitle("My Customers");
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

        getCustomers();
        return view;
    }

    private ArrayList<Card> getData(){
        ArrayList<Card> cards = new ArrayList<>();
        int[] holes = {8, 16};
        int[] rewards = {2, 3};
        String[] businessNames = {"First Business Name", "Second Business Name"};
        String[] rewardNames = {"First Reward Name", "Second Business Name"};
        String[] bnFonts = {"font/LibreBodoni-BoldItalic.otf", "font/lmmonoproplt10-boldoblique.otf"};
        String[] rwFonts = {"font/vanchrome front.ttf", "font/Spirax-Regular.ttf"};
        String[] logos = {"https://thumbs.dreamstime.com/t/purple-petunia-flowers-bed-beautiful-blurred-nature-background-banner-website-garden-concept-toned-54798543.jpg",
                "https://cdn.wallpapersafari.com/18/17/KZn0LD.jpg"};
        String[] cardColors = {"#9933ff", "#009900"};

        for(int i=0; i<holes.length; i++){
            Card card = new Card();
            card.setBusinessFont(bnFonts[i]);
            card.setRewardFont(rwFonts[i]);
            card.setBusinessName(businessNames[i]);
            card.setRewardName(rewardNames[i]);
            card.setLogo(logos[i]);
            card.setPunches(holes[i]);
            card.setRewards(rewards[i]);
            card.setCardColor(String.valueOf(Color.parseColor(cardColors[i])));

            cards.add(card);
        }

        return cards;
    }

    private ArrayList<Customer> getAcceptedCustomersData(){
        ArrayList<Customer> customers = new ArrayList<>();
        int[] holes = {8, 16};
        int[] rewards = {2, 3};
        String[] names = {"First Customer Name", "Second Customer Name"};
        String[] emails = {"First Customer Email", "Second Customer Email"};
        String[] cardColors = {"#9933ff", "#009900"};

        for(int i=0; i<holes.length; i++){
            Customer customer = new Customer();
            customer.setName(names[i]);
            customer.setEmail(emails[i]);
            customer.setPunches(holes[i]);
            customer.setRewards(rewards[i]);
            customer.setCardColor(String.valueOf(Color.parseColor(cardColors[i])));

            customers.add(customer);
        }

        return customers;
    }

    public void gotoPunchCard(Card card){
        Commons.card = card;
        Intent intent = new Intent(getActivity(), PunchCardActivity.class);
        startActivity(intent);
    }

    public void gotoPunchCardPage(Customer customer){
        Commons.customer = customer;
        Intent intent = new Intent(getActivity(), PunchCardActivity.class);
        startActivity(intent);
        com.unitedwebspace.punchcard.main.HomeActivity activity = (HomeActivity) this.getActivity();
        activity.finish();
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void getCustomers() {

        String url = ReqConst.SERVER_URL + "getMyCustomers";

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

    public void parseRegisterResponse(String json) {

        pd.dismiss();
        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                JSONArray jsonArray = response.getJSONArray("my_customers");
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Customer customer = new Customer();
                    customer.set_idx(jsonObject.getInt("customer_id"));
                    customer.setEmail(jsonObject.getString("customer_email"));
                    customer.setName(jsonObject.getString("customer_name"));
                    int businessHoles = Commons.thisMerchant.getPunches();
                    customer.setPunches(businessHoles - jsonObject.getInt("punchs"));
                    customer.setRewards(jsonObject.getInt("punchs")/businessHoles);
                    customer.setCardColor(Commons.thisMerchant.getCardColor());

                    customers.add(customer);
                }
                if(customers.isEmpty()) listTitle.setText("No exists any accepted customer yet");
                adapter.setDatas(customers);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }
}


























