package com.unitedwebspace.punchcard.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.classes.duomenu.CustomerCardsFragment;
import com.unitedwebspace.punchcard.classes.duomenu.CustomerMerchantsFragment;
import com.unitedwebspace.punchcard.classes.duomenu.CustomerProfileFragment;
import com.unitedwebspace.punchcard.classes.duomenu.MenuAdapter;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.models.Customer;
import com.unitedwebspace.punchcard.models.Merchant;
import com.unitedwebspace.punchcard.preference.PrefConst;
import com.unitedwebspace.punchcard.preference.Preference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import me.leolin.shortcutbadger.ShortcutBadger;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class CustomerHomeActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {
    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    Timer mTimer = new Timer();
    final Handler mHandler = new Handler();
    LinearLayout alertDialog;
    TextView msg, okButton;
    FrameLayout layout;

    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.customerMenuOptions)));

        alertDialog = (LinearLayout) findViewById(R.id.alertDialog);
        layout = (FrameLayout) findViewById(R.id.layout);
        msg = (TextView) findViewById(R.id.msg);
        okButton = (TextView)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                reference.removeValue();
            }
        });

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        goToFragment(new CustomerCardsFragment(), false);
        mMenuAdapter.setViewSelected(0, true);
        setTitle(mTitles.get(0));

        mTimer.schedule(doAsynchronousTask, 0, 6000);

        login();
        updateToken(Preference.getInstance().getValue(this, PrefConst.TOKEN, ""));
        getNotification();
    }

    TimerTask doAsynchronousTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // getNotifications();
                }
            });
        }
    };

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onFooterClicked() {
        Toast.makeText(this, "onFooterClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClicked() {
        Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show();
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title
        setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {
            case 0:
                goToFragment(new CustomerCardsFragment(), false);
                break;
            case 1:
                goToFragment(new CustomerMerchantsFragment(), false);
                break;
            case 2:
                goToFragment(new CustomerProfileFragment(), false);
                break;
            case 3:
                logout();
                break;
            default:
                goToFragment(new CustomerCardsFragment(), false);
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.customer_drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    public void logout(){
        Preference.getInstance().put(getApplicationContext(), PrefConst.EMAIL, "");
        Preference.getInstance().put(getApplicationContext(), PrefConst.PASSWORD, "");
        Preference.getInstance().put(getApplicationContext(), PrefConst.USER, "");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void login() {

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

                params.put("email", Preference.getInstance().getValue(getApplicationContext(), PrefConst.EMAIL, ""));
                params.put("password", Preference.getInstance().getValue(getApplicationContext(), PrefConst.PASSWORD, ""));
                String user = Preference.getInstance().getValue(getApplicationContext(), PrefConst.USER, "");
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
                Customer customer = new Customer();
                customer.set_idx(userInfo.getInt("id"));
                customer.setName(userInfo.getString("name"));
                customer.setEmail(userInfo.getString("email"));
                Commons.thisCustomer = customer;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateToken(final String token) {

        String url = ReqConst.SERVER_URL + "updateToken";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateTokenResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                Toast.makeText(getApplicationContext(), "Processing failed", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                if(Commons.user.equals("business"))
                    params.put("user_id", String.valueOf(Commons.thisMerchant.get_idx()));
                else
                    params.put("user_id", String.valueOf(Commons.thisCustomer.get_idx()));
                params.put("user_token", token);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PunchApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseUpdateTokenResponse(String json) {

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            Log.d("result===",String.valueOf(result_code));

            if (result_code == 200) {
            }
            else {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    String content = "", businessName = "";
    Firebase reference = null;

    private void getNotification(){
        reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/"+
                Commons.thisCustomer.getEmail().replace(".com","").replace(".","ddoott"));

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Firebase reference1 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/"+ Commons.thisCustomer.getEmail().replace(".com","").replace(".","ddoott")+"/"+dataSnapshot.getKey());

                reference1.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = dataSnapshot.getValue(Map.class);
                        try{
                            content = map.get("msg").toString();
                            businessName = map.get("sender").toString();
                            if(content.equals("accepted"))
                                showAlertDialog("Your punch card accepted by " + businessName);
                            else showAlertDialog("Your punch card declined by " + businessName);

                        }catch (NullPointerException e){e.printStackTrace();}
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }

    private void showAlertDialog(String content){
        alertDialog.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        msg.setText(content);
    }
}





















