package com.unitedwebspace.punchcard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.unitedwebspace.punchcard.Entities.Font;
import com.unitedwebspace.punchcard.PunchApplication;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.classes.duomenu.CustomerCardsFragment;
import com.unitedwebspace.punchcard.commons.Commons;
import com.unitedwebspace.punchcard.models.Card;
import com.unitedwebspace.punchcard.models.Merchant;

import java.util.ArrayList;

/**
 * Created by sonback123456 on 4/16/2018.
 */

public class AcceptedCardListAdapter extends BaseAdapter {

    private CustomerCardsFragment _context;
    private ArrayList<Card> _datas = new ArrayList<>();
    private ArrayList<Card> _alldatas = new ArrayList<>();

    public AcceptedCardListAdapter(CustomerCardsFragment context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Card> datas) {

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
            convertView = inflater.inflate(R.layout.acceptedcard_list_item, parent, false);

            holder.cardFrame = (FrameLayout) convertView.findViewById(R.id.cardFrame);
            holder.cardHoleFrame = (LinearLayout) convertView.findViewById(R.id.cardHoleFrame);
            holder.holeFrame1 = (LinearLayout) convertView.findViewById(R.id.holeFrame1);
            holder.holeFrame2 = (LinearLayout) convertView.findViewById(R.id.holeFrame2);

            holder.bsName = (TextView) convertView.findViewById(R.id.bsName);
            holder.rwName = (TextView) convertView.findViewById(R.id.rwName);
            holder.imageFrame = (TextView) convertView.findViewById(R.id.imageFrame);
            holder.cardLogo = (ImageView) convertView.findViewById(R.id.cardLogo);
            holder.cardLogoNet = (NetworkImageView) convertView.findViewById(R.id.cardLogoNet);
            holder.rewards = (TextView) convertView.findViewById(R.id.rewards);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Card card = (Card) _datas.get(position);

        holder.bsName.setText(card.getBusinessName());
        holder.rwName.setText(card.getRewardName());
        if(card.getLogo().length() > 0)
            holder.cardLogoNet.setImageUrl(card.getLogo(), PunchApplication.getInstance().getImageLoader());

        try{
            Typeface font = Typeface.createFromAsset(_context.getActivity().getAssets(), "font/"+((new Font().getFontPathFromMap(card.getBusinessFont()))));
            holder.bsName.setTypeface(font);
            font = Typeface.createFromAsset(_context.getActivity().getAssets(), "font/"+((new Font().getFontPathFromMap(card.getRewardFont()))));
            holder.rwName.setTypeface(font);
        }catch (RuntimeException e){e.printStackTrace();}

        holder.rewards.setText(String.valueOf(card.getRewards()));

        int color = Color.parseColor(card.getCardColor());

        setupCardFrameBackground(holder.cardFrame, 0xffffffff, color);
        setupCardHoleFrameBackground(holder.cardHoleFrame, color, color);
        setupCardBackground(holder.imageFrame, 0xffffffff, color);

        holder.holeFrame1.removeAllViews();
        holder.holeFrame2.removeAllViews();

        int holes = card.getPunches();

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
            for(int i = holes+1; i< card.getBusinessHoles()+1; i++) {
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
            if(card.getBusinessHoles()<=10){
                for(int i=holes+1; i<card.getBusinessHoles()+1; i++) {
                    View hole =  _context.getLayoutInflater().inflate(R.layout.empty_holebox, holder.holeFrame2, false);
                    holder.holeFrame2.addView(hole);
                }
            }else {
                for(int i=holes+1; i<11; i++) {
                    View hole =  _context.getLayoutInflater().inflate(R.layout.empty_holebox, holder.holeFrame2, false);
                    holder.holeFrame1.addView(hole);
                }
                for(int i=11; i<card.getBusinessHoles()+1; i++) {
                    View hole =  _context.getLayoutInflater().inflate(R.layout.empty_holebox, holder.holeFrame2, false);
                    holder.holeFrame2.addView(hole);
                }
            }
        }else {

        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.gotoRequestCardPage(card);
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

            for (Card card : _alldatas){

                if (card instanceof Card) {

                    String value = ((Card) card).getBusinessName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(card);
                    }else {
                        String value1 = ((Card) card).getRewardName().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(card);
                        }else {
                            String value2 = String.valueOf(((Card) card).getPunches());
                            if (value2.contains(charText)) {
                                _datas.add(card);
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
        NetworkImageView cardLogoNet;
        ImageView cardLogo;
        TextView imageFrame, bsName, rwName, rewards;
    }
}





























