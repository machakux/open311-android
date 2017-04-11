package com.github.codetanzania.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.github.codetanzania.model.Service;
import com.github.codetanzania.model.ServiceGroup;

import java.util.List;

import tz.co.codetanzania.R;

public class ServiceGroupItemsAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<ServiceGroup> mServiceGroupItems;

    public ServiceGroupItemsAdapter(
            Context mContext, List<ServiceGroup> mServiceGroupItems) {
        this.mContext = mContext;
        this.mServiceGroupItems = mServiceGroupItems;
    }

    @Override
    public int getGroupCount() {
        return mServiceGroupItems.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mServiceGroupItems.get(i).getServices().size();
    }

    @Override
    public Object getGroup(int i) {
        return mServiceGroupItems.get(i);
    }

    @Override
    public Object getChild(int gPos, int cPos) {
        return mServiceGroupItems.get(gPos).getServices().get(cPos);
    }

    @Override
    public long getGroupId(int pos) {
        return pos;
    }

    @Override
    public long getChildId(int gPos, int cPos) {
        return cPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(
            int gPos, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = ((ServiceGroup)getGroup(gPos)).name;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            convertView = inflater.inflate(R.layout.service_group, parent, false);
        }

        TextView tvGroupTitle = (TextView) convertView.findViewById(R.id.tv_serviceReqTitle);
        tvGroupTitle.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int gPos, int cPos, boolean isExpanded, View convertView, ViewGroup parent) {
        String desc = ((Service)getChild(gPos, cPos)).description ;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.service_group_item, parent, false);
        }

        TextView tvGroupDesc = (TextView) convertView.findViewById(R.id.tv_serviceReqTicket);
        tvGroupDesc.setText(desc);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int gPos, int cPos) {
        return true;
    }
}
