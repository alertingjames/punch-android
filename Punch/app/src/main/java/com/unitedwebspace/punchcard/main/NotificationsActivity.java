package com.unitedwebspace.punchcard.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.adapters.NotificationListAdapter;
import com.unitedwebspace.punchcard.adapters.NotificationListAdapter2;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class NotificationsActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener  {

    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ListView listView;
    LinearLayout alertDialog;
    FrameLayout layout;
    TextView email, acceptButton, denyButton;
    NotificationListAdapter2 adapter = new NotificationListAdapter2(this);
    ArrayList<String> data = new ArrayList<>();
    ProgressDialog pd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        alertDialog = (LinearLayout)findViewById(R.id.alertDialog);
        layout = (FrameLayout) findViewById(R.id.layout);

        email = (TextView) findViewById(R.id.email);
        acceptButton = (TextView) findViewById(R.id.acceptButton);
        denyButton = (TextView) findViewById(R.id.denyButton);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        listView=(ListView)findViewById(R.id.list);
        adapter.setDatas(data);
        listView.setAdapter(adapter);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alertDialog.getVisibility() == VISIBLE){
                    alertDialog.setVisibility(GONE);
                    layout.setVisibility(GONE);
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
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

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
                if(data.isEmpty()) Toast.makeText(this, "No notification you have", Toast.LENGTH_SHORT).show();
                adapter.setDatas(data);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(this, "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Processing failed", Toast.LENGTH_SHORT).show();

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

    public void parseResponse(String json) {

        pd.dismiss();
        Intent intent;
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                alertDialog.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                email.setText("");
                Toast.makeText(this, "Accepted!", Toast.LENGTH_SHORT).show();
                refresh();
            }
            else {
                Toast.makeText(this, "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Processing failed", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

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
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
                alertDialog.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                email.setText("");
                Toast.makeText(this, "Denied!", Toast.LENGTH_SHORT).show();
                refresh();
            }
            else {
                Toast.makeText(this, "Processing failed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Processing failed", Toast.LENGTH_SHORT).show();

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
}


























