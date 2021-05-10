package com.unitedwebspace.punchcard.classes.duomenu;

/**
 * Created by sonback123456 on 4/17/2018.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.main.CustomerHomeActivity;
import com.unitedwebspace.punchcard.main.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class CustomerProfileFragment extends Fragment {

    TextView name, email, deleteAccountButton;
    ProgressDialog pd;
    FrameLayout layout;
    LinearLayout alertDialogDelete;
    TextView noButton, deleteButton;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_profile_fragment, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Processing...");
        pd.setMessage("Wait");

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getChildAt(1).setVisibility(View.GONE);
        toolbar.getChildAt(0).setVisibility(View.GONE);

        alertDialogDelete = (LinearLayout)view.findViewById(R.id.alertDialog);
        layout = (FrameLayout) view.findViewById(R.id.layout);
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
        name.setText(Commons.thisCustomer.getName());
        email = (TextView) view.findViewById(R.id.email);
        email.setText(Commons.thisCustomer.getEmail());
        deleteAccountButton = (TextView) view.findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        return view;
    }

    public void showAlertDialog(){
        alertDialogDelete.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
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

                params.put("user_id", String.valueOf(Commons.thisCustomer.get_idx()));

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
                CustomerHomeActivity homeActivity = (CustomerHomeActivity) getActivity();
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
}

