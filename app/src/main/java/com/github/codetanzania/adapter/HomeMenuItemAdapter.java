package com.github.codetanzania.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.codetanzania.ui.HomeMenu;

import java.util.ArrayList;
import java.util.List;

import tz.co.codetanzania.R;

public class HomeMenuItemAdapter extends RecyclerView.Adapter<HomeMenuItemAdapter.ViewHolder>{

    private final HomeMenu mHomeMenu;
    private final List<HomeMenu.HomeMenuItem> mItems;
    private final HomeMenu.OnClickListener mListener;
    private final Context mContext;

    public HomeMenuItemAdapter(Context ctx, HomeMenu homeMenu, HomeMenu.OnClickListener listener) {
        this.mContext = ctx;
        this.mHomeMenu = homeMenu;
        this.mItems = new ArrayList<>(mHomeMenu.getMenuItems());
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ViewHolder(inflater.inflate(R.layout.home_menu_item_grid_content, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeMenu.HomeMenuItem item = mItems.get(position);
        item.setOnClickListener(mListener);
        holder.bind(mContext, item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mMenuItem;
        TextView mMenuItemContent;
        ImageView mMenuIcon;

        ViewHolder(View itemView) {
            super(itemView);
            mMenuItem = itemView.findViewById(R.id.crd_MenuItem);
            mMenuItemContent = (TextView) itemView.findViewById(R.id.tv_MenuItemContent);
            mMenuIcon = (ImageView) itemView.findViewById(R.id.img_MenuIcon);
        }

        public void bind(Context ctx, HomeMenu.HomeMenuItem menuItem) {
            mMenuItemContent.setText(menuItem.getTitle());
            mMenuIcon.setImageResource(menuItem.getIconResourceId());
            menuItem.bindClickEventFromView(mMenuItem);
        }
    }
}
