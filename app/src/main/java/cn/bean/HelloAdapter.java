package cn.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gddisplaymap.R;

import java.util.LinkedList;

/**
 * Created by Solace on 2018/3/7.
 */

public class HelloAdapter extends BaseAdapter {

    private LinkedList<HelloBean> mData;
    private Context mContext;

    public HelloAdapter(LinkedList<HelloBean> mData, Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.itemIcon);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.itemText);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(mData.get(position).getIcon());
        viewHolder.textView.setText(mData.get(position).getTextView());
        return convertView;
    }
    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
    public void add(HelloBean data){
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(data);
        //删除的话用remove
        notifyDataSetChanged();
    }
    public void remove(HelloBean data){
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.remove();
        //删除的话用remove
        notifyDataSetChanged();
    }
}