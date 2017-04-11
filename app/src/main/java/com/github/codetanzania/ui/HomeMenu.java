package com.github.codetanzania.ui;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeMenu {

    private static final String TAG = "HomeMenu";

    private List<HomeMenuItem> mItems;

    public enum MenuItemState {
        ENABLED, DISABLED
    }

    public HomeMenu() {
        this.mItems = new ArrayList<>();
    }

    public void addMenuItem(HomeMenuItem item) {
        Log.d(TAG, "In Home Menu. Item is " + item);
        Log.d(TAG, "And Items map is " + this.mItems);
        Log.d(TAG, "Item title is " + item.getTitle());
        this.mItems.add(item);
    }

    public Collection<HomeMenuItem> getMenuItems() {
        return this.mItems;
    }

    public HomeMenuItemBuilder getNewMenuItemBuilder() {
        return new HomeMenuItemBuilder();
    }

    public HomeMenuItem getMenuItem(int position) {
        return this.mItems.get(position);
    }

    public class HomeMenuItemBuilder {
        private int id;
        private String title;
        private int icon;
        private OnClickListener listener;

        private MenuItemState state = MenuItemState.ENABLED;

        public HomeMenuItemBuilder() {
            this(null);
        }

        public HomeMenuItemBuilder(String title) {
            this.title = title;
        }

        public HomeMenuItemBuilder  applyTitle(String title) {
            Log.d(TAG, "Applying Item Title " + title);
            this.title = title;
            return this;
        }

        public HomeMenuItemBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public HomeMenuItemBuilder applyIconResourceId(int icon) {
            this.icon = icon;
            return this;
        }

        public HomeMenuItemBuilder setState(MenuItemState state) {
            this.state = state;
            return this;
        }

        public HomeMenuItemBuilder overrideOnClickListener(OnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public HomeMenuItem create() {
            HomeMenuItem item = new HomeMenuItem(id, title, icon, listener);
            if (state == MenuItemState.DISABLED) {
                item.disable();
            }
            return item;
        }
    }

    public class ClickEvent {
        public View view;
        public HomeMenuItem item;
    }

    public interface OnClickListener {
        void onClickPerformed(ClickEvent evt);
    }

    public final class HomeMenuItem {

        private int id;
        private String mTitle;
        private int mIconResId;
        private OnClickListener listener;
        private boolean enabled;

        public HomeMenuItem(int id, @NonNull String mTitle, @IdRes int mIconResId) {
            this(id, mTitle, mIconResId, null);
        }

        public HomeMenuItem(int id, @NonNull String title, @IdRes int mIconResId, OnClickListener listener) {
            this.mTitle = title;
            this.mIconResId = mIconResId;
            this.listener = listener;
            this.enabled = true;
            this.id = id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public void setTitle(String title) {
            this.mTitle = title;
        }

        public void setOnClickListener(OnClickListener listener) {
            this.listener = listener;
        }

        public void setIconResourceId(int mIconResId) {
            this.mIconResId = mIconResId;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public void disable() {
            this.enabled = false;
        }

        public void enable() {
            this.enabled = true;
        }

        public int getIconResourceId() {
            return this.mIconResId;
        }

        public void bindClickEventFromView(final View view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickEvent evt = new ClickEvent();
                    evt.item = HomeMenuItem.this;
                    evt.view = v;
                    listener.onClickPerformed(evt);
                }
            });
        }

        public OnClickListener getClickListener() {
            return this.listener;
        }
    }
}