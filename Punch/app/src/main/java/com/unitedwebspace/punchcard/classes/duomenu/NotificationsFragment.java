package com.unitedwebspace.punchcard.classes.duomenu;

/**
 * Created by sonback123456 on 4/15/2018.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.firebase.client.Firebase;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.adapters.NotificationListAdapter;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.main.BuyCardActivity;
import com.unitedwebspace.punchcard.models.Card;

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
public class NotificationsFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {

    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ListView listView;
    LinearLayout alertDialog, alertDialogBuy;
    FrameLayout layout;
    TextView email, acceptButton, denyButton, cancelButton, buyButton;
    NotificationListAdapter adapter = new NotificationListAdapter(this);
    ArrayList<String> data = new ArrayList<>();
    ProgressDialog pd;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getChildAt(1).setVisibility(View.GONE);
        toolbar.getChildAt(0).setVisibility(View.GONE);

        alertDialog = (LinearLayout)view.findViewById(R.id.alertDialog);
        layout = (FrameLayout) view.findViewById(R.id.layout);

        alertDialogBuy = (LinearLayout)view.findViewById(R.id.alertDialogBuy);
        cancelButton = (TextView) view.findViewById(R.id.cancelButton);
        buyButton = (TextView) view.findViewById(R.id.buyButton);

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
                Intent intent=new Intent(getActivity(), BuyCardActivity.class);
                intent.putExtra("newBuyFlag", true);
                startActivity(intent);
            }
        });

        email = (TextView) view.findViewById(R.id.email);
        acceptButton = (TextView) view.findViewById(R.id.acceptButton);
        denyButton = (TextView) view.findViewById(R.id.denyButton);

        ui_RefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        listView=(ListView)view.findViewById(R.id.list);
        adapter.setDatas(data);
        listView.setAdapter(adapter);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alertDialog.getVisibility() == View.VISIBLE){
                    alertDialog.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    email.setText("");
                }
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                punchNewCard();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineRequest();
            }
        });

        getNotifications();

        return view;
    }

    public void showAlertDialog(String eml){
        alertDialog.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        email.setText(eml);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void getNotifications() {

        String url = ReqConst.SERVER_URL + "getNotifications";

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
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("business_id", String.valueOf(Commons.thisMerchant.get_bidx()));

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
                JSONArray jsonArray = response.getJSONArray("notifications");
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String customer_email = "";
                    customer_email = jsonObject.getString("customer_email");

                    data.add(customer_email);
                }
                if(data.isEmpty()) Toast.makeText(getActivity(), "No notification you have", Toast.LENGTH_SHORT).show();
                adapter.setDatas(data);
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

    public void punchNewCard() {

        String url = ReqConst.SERVER_URL + "punchNewCard";

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
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

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

    public void parseResponse(String json) {

        pd.dismiss();
        Intent intent;
        alertDialog.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        email.setText("");
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getActivity(), "Accepted!", Toast.LENGTH_SHORT).show();
                sendNotification("accepted");
                refresh();
            }
            else if (result_code == 106) {
                //    showAlertDialogPunch();
                alertDialogBuy.setVisibility(View.VISIBLE);
                layout.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void declineRequest() {

        String url = ReqConst.SERVER_URL + "declineRequest";

        pd.show();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseDeclineResponse(response);

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

                params.put("business_id", String.valueOf(Commons.thisMerchant.get_bidx()));
                params.put("customer_email", email.getText().toString().trim());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseDeclineResponse(String json) {

        pd.dismiss();
        Intent intent;
        alertDialog.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        email.setText("");
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                Toast.makeText(getActivity(), "Denied!", Toast.LENGTH_SHORT).show();
                sendNotification("denied");
                refresh();
            }
            else {
                Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Processing failed", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    private void refresh(){
        data.clear();
        Commons.notiF = false;
        Commons.notificationManager.cancelAll();
        Commons.notificationManager.cancel(0);
        getNotifications();
    }

    Firebase reference;

    public void sendNotification(String content){
        Firebase.setAndroidContext(getActivity());
        reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/" +
                email.getText().toString().replace(".com", "").replace(".", "ddoott")+
                "/" + Commons.thisMerchant.getEmail().replace(".com", "").replace(".", "ddoott"));

        Map<String, String> map = new HashMap<String, String>();
        map.put("sender", Commons.thisMerchant.getBusinessName());
        map.put("senderId", String.valueOf(Commons.thisMerchant.get_bidx()));
        map.put("msg", content);
        reference.removeValue();
        reference.push().setValue(map);
    }
}


























