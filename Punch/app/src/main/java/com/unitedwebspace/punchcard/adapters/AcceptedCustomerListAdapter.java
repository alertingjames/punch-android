package com.unitedwebspace.punchcard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.classes.duomenu.CustomersFragment;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.models.Card;
import com.unitedwebspace.punchcard.models.Customer;

import java.util.ArrayList;

/**
 * Created by sonback123456 on 4/16/2018.
 */

public class AcceptedCustomerListAdapter extends BaseAdapter {

    private CustomersFragment _context;
    private ArrayList<Customer> _datas = new ArrayList<>();
    private ArrayList<Customer> _alldatas = new ArrayList<>();

    public AcceptedCustomerListAdapter(CustomersFragment context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Customer> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.acceptedcustomer_list_item, parent, false);

            holder.cardFrame = (FrameLayout) convertView.findViewById(R.id.cardFrame);
            holder.cardHoleFrame = (LinearLayout) convertView.findViewById(R.id.cardHoleFrame);
            holder.holeFrame1 = (LinearLayout) convertView.findViewById(R.id.holeFrame1);
            holder.holeFrame2 = (LinearLayout) convertView.findViewById(R.id.holeFrame2);

            holder.rewards = (TextView) convertView.findViewById(R.id.rewards);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.email = (TextView) convertView.findViewById(R.id.email);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Customer customer = (Customer) _datas.get(position);

        holder.name.setText(customer.getName());
        holder.email.setText(customer.getEmail());

        holder.rewards.setText(String.valueOf(customer.getRewards()));

        int color = Color.parseColor(customer.getCardColor());

        setupCardFrameBackground(holder.cardFrame, 0xffffffff, color);
        setupCardHoleFrameBackground(holder.cardHoleFrame, color, color);

        holder.holeFrame1.removeAllViews();
        holder.holeFrame2.removeAllViews();

        int holes = customer.getPunches();

        if(holes > 10){
            for(int i=1; i<11; i++) {
                View hole = _context.getLayoutInflater().inflate(R.layout.edit_holebox, holder.holeFrame1, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holder.holeFrame1.addView(hole);
            }
            for(int i=11; i<holes+1; i++) {
                View hole = _context.getLayoutInflater().inflate(R.layout.edit_holebox, holder.holeFrame2, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holder.holeFrame2.addView(hole);
            }
            for(int i = holes+1; i< Commons.thisMerchant.getPunches()+1; i++) {
                View hole = _context.getLayoutInflater().inflate(R.layout.empty_holebox, holder.holeFrame2, false);
                holder.holeFrame2.addView(hole);
            }
        }else if (holes > 0 && holes <= 10){
            for(int i=1; i<holes+1; i++) {
                View hole = _context.getLayoutInflater().inflate(R.layout.edit_holebox, holder.holeFrame1, false);
                final TextView holeText = (TextView) hole.findViewById(R.id.hole);
                holeText.setText(String.valueOf(i));
                holder.holeFrame1.addView(hole);
            }
            if(Commons.thisMerchant.getPunches()<=10){
                for(int i=holes+1; i<Commons.thisMerchant.getPunches()+1; i++) {
                    View hole =  _context.getLayoutInflater().inflate(R.layout.empty_holebox, holder.holeFrame2, false);
                    holder.holeFrame2.addView(hole);
                }
            }else {
                for(int i=holes+1; i<11; i++) {
                    View hole =  _context.getLayoutInflater().inflate(R.layout.empty_holebox, holder.holeFrame2, false);
                    holder.holeFrame1.addView(hole);
                }
                for(int i=11; i<Commons.thisMerchant.getPunches()+1; i++) {
                    View hole =  _context.getLayoutInflater().inflate(R.layout.empty_holebox, holder.holeFrame2, false);
                    holder.holeFrame2.addView(hole);
                }
            }
        }else {

        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.gotoPunchCardPage(customer);
            }
        });

        return convertView;
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

    public void filter(String charText){

        charText = charText.toLowerCase();

        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {

            for (Customer customer : _alldatas){

                if (customer instanceof Customer) {

                    String value = ((Customer) customer).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(customer);
                    }else {
                        String value1 = ((Customer) customer).getEmail().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(customer);
                        }else {
                            String value2 = String.valueOf(((Customer) customer).getPunches());
                            if (value2.contains(charText)) {
                                _datas.add(customer);
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {
        LinearLayout holeFrame1, holeFrame2, cardHoleFrame;
        FrameLayout cardFrame;
        TextView name, email, rewards;
    }
}





























