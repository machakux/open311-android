package com.github.codetanzania.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.codetanzania.model.ServiceRequest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import tz.co.codetanzania.R;

public class ServiceRequestsAdapter extends
        ClickAwareRecyclerViewAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /* A list of open311Service requests by the civilian */
    private final List<ServiceRequest> mServiceRequests;

    /* Title of the issues */
    private final String mTitle;

    /* context allows us to access to resources as in ordinary first class citizen components */
    private Context mContext;

    /* constructor */
    public ServiceRequestsAdapter(
            Context mContext, String title, List<ServiceRequest> serviceRequests, OnItemClickListener<ServiceRequest> onItemClickListener) {
        super(onItemClickListener);
        this.mTitle = title;
        this.mServiceRequests = serviceRequests;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* inflate view and return view holder */
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.issue_ticket, parent, false);
            return new ServiceRequestViewHolder(view, mClickListener);
        } else if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.issue_ticket_groups_title, parent, false);
            return new ServiceHeaderViewHolder(view);
        }

        throw new UnsupportedOperationException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /* bind data to the views */
        if (holder instanceof ServiceHeaderViewHolder) {
            ((ServiceHeaderViewHolder)holder).tvHeader.setText(mTitle);
        } else if (holder instanceof ServiceRequestViewHolder) {
            ServiceRequest serviceRequest = this
                    .mServiceRequests.get(position);
            ((ServiceRequestViewHolder)holder).tvServiceReqTitle.setText(serviceRequest.open311Service.name);
            ((ServiceRequestViewHolder)holder).tvServiceReqTicket.setText(String.format("%s, %s", serviceRequest.open311Service.code, serviceRequest.address));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String lastActionDateStr;

            if (serviceRequest.resolvedAt != null) {
                lastActionDateStr = sdf.format(serviceRequest.resolvedAt);
            } else {
                lastActionDateStr = sdf.format(serviceRequest.status.updatedAt);
            }

            /*((ServiceRequestViewHolder)holder).tvStatus.setCompoundDrawables(
                    null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_warning_24dp), null);*/

            ((ServiceRequestViewHolder)holder).vwStatusView.setBackgroundColor(Color.parseColor(serviceRequest.status.color));
            ((ServiceRequestViewHolder)holder).tvServiceReqResolvedAt.setText(lastActionDateStr);
            ((ServiceRequestViewHolder)holder).tvServiceReqCode.setText(serviceRequest.open311Service.name.substring(0,2).toUpperCase());

            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_circular_lbl);
            drawable.setColorFilter(Color.parseColor(serviceRequest.open311Service.color), PorterDuff.Mode.MULTIPLY);
            ((ServiceRequestViewHolder)holder).tvServiceReqCode.setBackground(drawable);
            ((ServiceRequestViewHolder)holder).bind(serviceRequest, ((ServiceRequestViewHolder)holder).crdTicketItem);
        }
    }

    @Override
    public int getItemViewType(int pos) {
        return pos == TYPE_HEADER ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        /* Size of the requests */
        return mServiceRequests.size();
    }

    private static class ServiceRequestViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceReqCode;
        TextView tvServiceReqTitle;
        TextView tvServiceReqTicket;
        TextView tvServiceReqResolvedAt;
        TextView tvStatus;
        View     vwStatusView;
        View     crdTicketItem;

        private OnItemClickListener<ServiceRequest> mClickListener;

        ServiceRequestViewHolder(View itemView, OnItemClickListener<ServiceRequest> mClickListener) {
            super(itemView);

            this.mClickListener = mClickListener;

            tvServiceReqCode = (TextView) itemView.findViewById(R.id.tv_serviceReqCode);
            tvServiceReqTitle = (TextView) itemView.findViewById(R.id.tv_serviceReqTitle);
            tvServiceReqResolvedAt = (TextView) itemView.findViewById(R.id.tv_serviceReqResolvedAt);
            tvServiceReqTicket = (TextView) itemView.findViewById(R.id.tv_serviceReqTicket);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_Status);
            vwStatusView = itemView.findViewById(R.id.vw_serviceReqStatus);
            crdTicketItem = itemView.findViewById(R.id.crd_TicketItem);
        }

        void bind(final ServiceRequest request, View view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(request);
                }
            });
        }
    }

    private static class ServiceHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;

        ServiceHeaderViewHolder (View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.tv_serviceReqHeaderName);
        }
    }

}