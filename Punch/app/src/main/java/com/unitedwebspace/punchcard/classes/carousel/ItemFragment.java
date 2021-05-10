package com.unitedwebspace.punchcard.classes.carousel;

/**
 * Created by sonback123456 on 4/15/2018.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.commons.Constants;
import com.unitedwebspace.punchcard.commons.ReqConst;
import com.unitedwebspace.punchcard.main.BuyCardActivity;
import com.unitedwebspace.punchcard.main.HomeActivity;
import com.unitedwebspace.punchcard.models.Plan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.vivek.inapppurchaselib.utils.IabHelper;
import app.vivek.inapppurchaselib.utils.Purchase;
import app.vivek.inapppurchaselib.v3.VKInAppConstants;
import app.vivek.inapppurchaselib.v3.VKInAppProperties;
import app.vivek.inapppurchaselib.v3.VKInAppPurchaseActivity;
import app.vivek.inapppurchaselib.v3.VKLogger;

import static android.app.Activity.RESULT_OK;

public class ItemFragment extends Fragment {

    private static final String POSITON = "position";
    private static final String SCALE = "scale";
    private static final String PRICE = "price";
    private static final String CARDS = "cards";
    private static final String PRODUCT_ID = "product_id";
    private static final String PLAN = "plan";

    private int screenWidth;
    private int screenHeight;

    ProgressDialog pd;

    int plan = 0;
    String product;

    public static Fragment newInstance(BuyCardActivity context, int pos, Plan plan, float scale) {
        Bundle b = new Bundle();
        b.putInt(POSITON, pos);
        b.putFloat(SCALE, scale);
        b.putFloat(PRICE, plan.getAmount());
        b.putInt(CARDS, plan.getCards());
        b.putString(PRODUCT_ID, plan.getProductID());
        b.putInt(PLAN, plan.getPlan());

        return Fragment.instantiate(context, ItemFragment.class.getName(), b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        VKInAppProperties.BASE_64_KEY = getString(R.string.InAppPurchaseKey);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Processing");
        pd.setMessage("Wait");

        final int postion = this.getArguments().getInt(POSITON);
        float scale = this.getArguments().getFloat(SCALE);

        final int c = this.getArguments().getInt(CARDS);
        float p = this.getArguments().getFloat(PRICE);

        product = this.getArguments().getString(PRODUCT_ID);

        plan = this.getArguments().getInt(PLAN);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth * 2/ 3, screenHeight / 2);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_image, container, false);

        TextView buyButton = (TextView) linearLayout.findViewById(R.id.buyButton);
        TextView price = (TextView) linearLayout.findViewById(R.id.price);
        TextView cards = (TextView) linearLayout.findViewById(R.id.cards);

        price.setText("$ "+String.valueOf(p));
        cards.setText(String.valueOf(c) + " Cards");

        CarouselLinearLayout root = (CarouselLinearLayout) linearLayout.findViewById(R.id.root_container);
        FrameLayout layout = (FrameLayout) linearLayout.findViewById(R.id.pagerImg);

        layout.setLayoutParams(layoutParams);

        //handling click event
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
       //         buyProduct();
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyProduct();
            }
        });

        root.setScaleBoth(scale);

        return linearLayout;
    }

    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    private void buyProduct(){
        Intent mIntent=new Intent(getActivity(), VKInAppPurchaseActivity.class);
        mIntent.putExtra(VKInAppConstants.INAPP_SKU_ID, product);
        mIntent.putExtra(VKInAppConstants.INAPP_SKU_TYPE, IabHelper.ITEM_TYPE_INAPP);
        mIntent.putExtra(VKInAppConstants.INAPP_PRODUCT_TYPE,VKInAppConstants.INAPP_CONSUMABLE);
        startActivityForResult(mIntent, 101);
    }

    public void upgradeCards() {

        String url = ReqConst.SERVER_URL + "upgradeCards";

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("business_id", String.valueOf(Commons.thisMerchant.get_bidx()));
                params.put("plan", String.valueOf(plan));

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
                Toast.makeText(getActivity(), "Plan upgraded", Toast.LENGTH_SHORT).show();
            }
            else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==101){
                String mInapSkuId=data.getExtras().getString(VKInAppConstants.INAPP_SKU_ID);
                if(data.getExtras().getString(VKInAppConstants.INAPP_PURCHASE_TOKEN)!=null)
                    VKLogger.e("Purchase Token",data.getExtras().getString(VKInAppConstants.INAPP_PURCHASE_TOKEN));
                Purchase purchaseReceipt;
                if(data.getExtras().containsKey(VKInAppConstants.INAPP_PURCHASE_INFO)){
                    purchaseReceipt= (Purchase) data.getExtras().getSerializable(VKInAppConstants.INAPP_PURCHASE_INFO);
                    VKLogger.e("OrderId:- "+purchaseReceipt.getOrderId()+"\n Token:-"+purchaseReceipt.getToken());
                }

                // {"productId":"appsinvo_day_sub_test","type":"subs","price":"₹ 10.00","price_amount_micros":10000000,"price_currency_code":"INR","title":"OneDaySubscription (InApp Test)","description":"Testing Purpose"}
                int value=data.getExtras().getInt("response_code");
                switch (value) {
                    case VKInAppConstants.RESULT_PRODUCT_CONSUME_SUCCESSFULLY:
                        responseAlertDialog("You have successfully consume "+mInapSkuId+" product.");
                        break;
                    case VKInAppConstants.RESULT_PRODUCT_PURCHASE_CONSUME_SUCCESSFULLY:
//                        responseAlertDialog("You have successfully purchase "+mInapSkuId+" product.");
                        upgradeCards();
                        break;
                    case VKInAppConstants.RESULT_PROPUR_SUCC_CONSUME_FAIL:
                        responseAlertDialog("You have failed to consume "+mInapSkuId+" product.");
                        break;
                    case VKInAppConstants.RESULT_SUBS_CONTINUE:
                        responseAlertDialog("Your subsription is continue for id "+mInapSkuId+" product.");
                        break;
                    case VKInAppConstants.ERROR_DEVICE_NOT_SUPPORT_SUBS:
                        responseAlertDialog(getString(R.string.error_msg_not_support_subs));
                        break;
                    case VKInAppConstants.ERROR_BASE_64_KEY_NOT_SETUP:
                        responseAlertDialog(getString(R.string.error_msg_base64key));
                        break;
                    case VKInAppConstants.ERROR_PACKAGE_NAME:
                        responseAlertDialog(getString(R.string.error_msg_package_name));
                        break;
                    case VKInAppConstants.ERROR_DEVICE_NOT_SUPPORT_INAPP:
                        responseAlertDialog(getString(R.string.error_msg_not_support_inapp));
                        break;
                    case VKInAppConstants.ERROR_PRODUCT_PURCHASE:
                        responseAlertDialog(getString(R.string.error_msg_in_purchase));
                        break;

                    default:
                        responseAlertDialog("Error is occured "+value);
                        break;
                }


            }
        }
    }


    /**
     * Show the InApp purchase status dialog
     * @param message
     */
    private void responseAlertDialog(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
        bld.setMessage(message);
        bld.setCancelable(false);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        bld.create().show();
    }

}






































