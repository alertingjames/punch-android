package com.unitedwebspace.punchcard.adapters;

/**
 * Created by sonback123456 on 4/14/2018.
 */

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unitedwebspace.punchcard.Entities.Font;
import com.unitedwebspace.punchcard.R;
import com.unitedwebspace.punchcard.main.CreateCardActivity;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class FontListAdapter extends BaseAdapter{

    private Font data = new Font();
    private LayoutInflater mInflater;
    private CreateCardActivity context;

    public FontListAdapter(CreateCardActivity context, Font font) {
        this.context = context;
        this.data = font ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return data.getFontTexts().length;
    }

    public Object getItem(int position) {
        return data.getFontTexts()[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.font_list_item, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.fontText);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        final Font entity = data;

        Log.d("TEXTs===>", String.valueOf(entity.getFontTexts().length));
        Log.d("PATHs===>", String.valueOf(entity.getFontPaths().length));

        holder.text.setText(entity.getFontTexts()[position]);

        try{
            Typeface font = Typeface.createFromAsset(context.getAssets(), "font/"+entity.getFontPaths()[position]);
            holder.text.setTypeface(font);
            holder.text.getText().toString().replaceAll(System.getProperty("line.separator"), "");
        }catch (RuntimeException e){e.printStackTrace();}

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.setupFont(entity.getFontPaths()[position]);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }
}
