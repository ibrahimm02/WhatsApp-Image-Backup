package com.ibrahim.whatsappbackup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class GridviewImageAdapter extends BaseAdapter
{
    private Context context;
    private List <String> lis;

    public GridviewImageAdapter(Context c, List <String> li)
    {
        // TODO Auto-generated method stub
        context = c;
        lis = li;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return lis.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_selectimagetoupload, null);
        }

        String strPath = lis.get(position).toString();

        // Get File Name
        String fileName = strPath.substring( strPath.lastIndexOf('/')+1, strPath.length() );

        // Image Resource
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);

        Glide.with(context).load("file://" + lis.get(position)).centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(imageView);

        // CheckBox
        CheckBox Chkbox = (CheckBox) convertView.findViewById(R.id.checkBox1);
        Chkbox.setTag(fileName);

        return convertView;

    }
}

