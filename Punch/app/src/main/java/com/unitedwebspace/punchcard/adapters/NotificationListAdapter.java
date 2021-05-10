package com.unitedwebspace.punchcard.adapters;

/**
 * Created by sonback123456 on 4/16/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.classes.duomenu.NotificationsFragment;

import java.util.ArrayList;

/**
 * Created by sonback123456 on 12/23/2017.
 */

public class NotificationListAdapter extends BaseAdapter {

    private NotificationsFragment _context;
    private ArrayList<String> _datas = new ArrayList<>();
    private ArrayList<String> _alldatas = new ArrayList<>();


    public NotificationListAdapter(NotificationsFragment context){
        super();
        this._context = context;
    }

    public void setDatas(ArrayList<String> datas) {

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



    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notification_list_item, parent, false);

            holder.content = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final String cont = (String) _datas.get(position);
        holder.content.setText(cont);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.showAlertDialog(cont);
            }
        });

        return convertView;
    }

    class CustomHolder {
        public TextView content;
    }
}








