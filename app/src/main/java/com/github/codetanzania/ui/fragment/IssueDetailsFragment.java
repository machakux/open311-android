package com.github.codetanzania.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.codetanzania.adapter.AttachmentCardViewAdapter;
import com.github.codetanzania.adapter.OnItemClickListener;
import com.github.codetanzania.model.ServiceRequest;
import com.github.codetanzania.util.AppConfig;
import com.github.codetanzania.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tz.co.codetanzania.R;

public class IssueDetailsFragment extends Fragment {

    // makes sense to make this singleton since we don't have many data
    // private static IssueDetailsFragment self;

    // reference to the service request
    private ServiceRequest mServiceRequest;

    // reference to the recycler view. used to show attachments
    private RecyclerView mAttachmentsRecyclerView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_issue_details, group, false);
    }

    public static IssueDetailsFragment getInstance(Bundle args) {
        IssueDetailsFragment inst = new IssueDetailsFragment();
        inst.setArguments(args);
        return inst;
    }

    @Override
    public void onViewCreated(View fragView, Bundle savedInstanceState) {
        Bundle args = getArguments();
        ServiceRequest serviceRequest = args.getParcelable(AppConfig.Const.TICKET);

        // bind data
        TextView tvTicketId = (TextView) fragView.findViewById(R.id.tv_TicketID);
        tvTicketId.setText(serviceRequest.address);
        TextView tvReporter = (TextView) fragView.findViewById(R.id.tv_Reporter);
        tvReporter.setText(serviceRequest.reporter.name);
        TextView tvReportTimestamp = (TextView) fragView.findViewById(R.id.tv_ReportTimestamp);
        String timestamp = "Unknown time";
        if (serviceRequest.createdAt != null) {
            timestamp = "  " + Util.formatDate(serviceRequest.createdAt, "yy-MM-dd HH:mm:ss");
        }
        tvReportTimestamp.setText(timestamp);
        TextView tvTicketTitle = (TextView) fragView.findViewById(R.id.tv_TicketTitle);
        tvTicketTitle.setText(serviceRequest.service.name);
        TextView tvLocation = (TextView) fragView.findViewById(R.id.tv_Location);
        tvLocation.setText(serviceRequest.jurisdiction.name);
        TextView tvDescription = (TextView) fragView.findViewById(R.id.tv_Description);
        tvDescription.setText(serviceRequest.description);

        mAttachmentsRecyclerView = (RecyclerView)
                fragView.findViewById(R.id.rv_Attachments);
        // todo: get attachments from arguments bundle
        List<String> attachments = serviceRequest.attachments;
        if (attachments == null || attachments.isEmpty()) {
            attachments = new ArrayList<>();
            attachments.add("92934000xskle.mp3");
            attachments.add("2dkaclla.mp4");
        }
        TextView tvAttachments = (TextView) fragView.findViewById(R.id.tv_Attachments);
        // tvAttachments.setVisibility(attachments.isEmpty() ? View.GONE : View.VISIBLE);
        tvAttachments.setText(
                String.format(Locale.getDefault(), "%s (%d)", getString(R.string.text_issue_attachment), attachments.size()));
        mAttachmentsRecyclerView.setVisibility(attachments.isEmpty() ? View.GONE : View.VISIBLE);
        // create adapter
        AttachmentCardViewAdapter attachmentCardViewAdapter =
                new AttachmentCardViewAdapter(getActivity(),
                        attachments, new OnItemClickListener<String>() {
                    @Override
                    public void onItemClick(String theItem) {

                    }
                });
        // setup recycler view
        mAttachmentsRecyclerView.setAdapter(
            attachmentCardViewAdapter);
        // lineary layout manager
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        // setup recycler view
        mAttachmentsRecyclerView.setLayoutManager(layoutManager);
    }
}
