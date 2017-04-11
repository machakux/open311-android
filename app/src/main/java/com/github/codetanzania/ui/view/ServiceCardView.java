package com.github.codetanzania.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.github.codetanzania.model.Service;
import com.github.codetanzania.util.SelectOneObservable;

import java.util.Observable;
import java.util.Observer;

public class ServiceCardView extends CardView implements Observer {

    private boolean mSelected;
    private int mSelectedBackground;
    private int mDefaultBackground = Color.WHITE;
    private Service mService;

    public ServiceCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSelectedBackground = mDefaultBackground;
    }

    public int getSelectedBackground() {
        return this.mSelectedBackground;
    }

    public void setSelectedBackground(int color) {
        this.mSelectedBackground = color;
    }

    public int getDefaultBackground() {
        return this.mDefaultBackground;
    }

    public void setDefaultBackground(int color) {
        this.mDefaultBackground = color;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    public void bindToService(Service mService) {
        this.mService = mService;
    }

    @Override
    public void update(Observable o, Object arg) {
        SelectOneObservable<Service> obs = (SelectOneObservable<Service>) o;
        Service selectedService = obs.getSelection();
        setCardBackgroundColor(selectedService == mService ?
            getSelectedBackground() : getDefaultBackground());
    }
}