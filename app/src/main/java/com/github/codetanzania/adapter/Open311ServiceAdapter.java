package com.github.codetanzania.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.codetanzania.model.Service;
import com.github.codetanzania.ui.view.ServiceCardView;
import com.github.codetanzania.util.SelectOneObservable;

import tz.co.codetanzania.R;

public class Open311ServiceAdapter extends RecyclerView.Adapter<Open311ServiceAdapter.ViewHolder> {

    private static final String TAG = "Open311ServiceAdapter";

    private final Context mContext;
    private final SelectOneObservable<Service> mSelectOneObservable;

    public static final int NO_PRIVATE_ISSUES = 0;
    public static final int NO_PUBLIC_ISSUES = 1;

    public Open311ServiceAdapter(Context ctx, SelectOneObservable<Service> selectOneObservable) {
        this.mContext = ctx;
        this.mSelectOneObservable = selectOneObservable;
        // select first item by default
        if (!this.mSelectOneObservable.isEmpty()) {
            this.mSelectOneObservable.select(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ViewHolder(
                inflater.inflate(R.layout.card_view_service, parent, false), mSelectOneObservable);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Service service = mSelectOneObservable.getSelectableAt(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return this.mSelectOneObservable.size();
    }

    public void applyFilter(int filterId) {
        Log.d(TAG, String.format("Applying filter %d", filterId));
        // todo: filter items using the criteria and then notify observable
        // notifyItemsChanged()
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvOpen311ServiceCode;
        private TextView tvOpen311ServiceName;
        private ServiceCardView crdOpen311Service;
        private SelectOneObservable<Service> mSelectOneObservable;

        ViewHolder(View itemView, SelectOneObservable<Service> selectOneObservable) {
            super(itemView);
            tvOpen311ServiceCode = (TextView) itemView.findViewById(R.id.tv_ServiceCode);
            tvOpen311ServiceName = (TextView) itemView.findViewById(R.id.tv_ServiceName);
            crdOpen311Service = (ServiceCardView) itemView;
            mSelectOneObservable = selectOneObservable;
        }

        public void bind(Service mOpen311Service) {
            crdOpen311Service.setSelectedBackground(Color.parseColor(mOpen311Service.color));
            crdOpen311Service.bindToService(mOpen311Service);
            tvOpen311ServiceName.setText(mOpen311Service.name);
            tvOpen311ServiceCode.setText(mOpen311Service.code);
            crdOpen311Service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // un select previous
                }
            });
            mSelectOneObservable.addObserver(crdOpen311Service);
        }
    }
}
