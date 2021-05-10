package com.unitedwebspace.punchcard.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.classes.duomenu.CustomerMerchantsFragment;
import com.unitedwebspace.punchcard.models.Merchant;

import java.util.ArrayList;

/**
 * Created by sonback123456 on 4/16/2018.
 */

public class MerchantListAdapter extends BaseAdapter {

    private CustomerMerchantsFragment _context;
    private ArrayList<Merchant> _datas = new ArrayList<>();
    private ArrayList<Merchant> _alldatas = new ArrayList<>();

    public MerchantListAdapter(CustomerMerchantsFragment context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Merchant> datas) {

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
            convertView = inflater.inflate(R.layout.merchant_list_item, parent, false);

            holder.bsName = (TextView) convertView.findViewById(R.id.bsName);
            holder.rwName = (TextView) convertView.findViewById(R.id.rwName);
            holder.backgroundImage = (NetworkImageView) convertView.findViewById(R.id.backgroundImage);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Merchant merchant = (Merchant) _datas.get(position);

        holder.bsName.setText(merchant.getBusinessName());
        holder.rwName.setText(merchant.getRewardName());
        if(merchant.getBackgroundImage().length() > 0)
            holder.backgroundImage.setImageUrl(merchant.getBackgroundImage(), PunchApplication.getInstance().getImageLoader());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.gotoRequestMerchant(merchant);
            }
        });

        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();

        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {

            for (Merchant merchant : _alldatas){

                if (merchant instanceof Merchant) {

                    String value = ((Merchant) merchant).getBusinessName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(merchant);
                    }else {
                        String value1 = ((Merchant) merchant).getRewardName().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(merchant);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {
        NetworkImageView backgroundImage;
        TextView bsName, rwName;
    }
}











