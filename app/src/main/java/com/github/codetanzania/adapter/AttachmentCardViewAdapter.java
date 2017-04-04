package com.github.codetanzania.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.codetanzania.util.Util;

import java.util.List;

import tz.co.codetanzania.R;

public class AttachmentCardViewAdapter extends
        RecyclerView.Adapter<AttachmentCardViewAdapter.ViewHolder> {

    private static final String TAG = "AttCardViewAdapter";

    private final Context mContext;
    private final List<String> mAttachments;
    private final OnItemClickListener<String> mClickListener;

    public AttachmentCardViewAdapter(
            @NonNull Context context, @NonNull List<String> attachments,
            @NonNull OnItemClickListener<String> clickListener ) {
        this.mContext = context;
        this.mAttachments = attachments;
        this.mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.cardview_issue_attachment, parent, false), mClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = mAttachments.get(position);
        String contentType = Util.inferContentType(title);
        Log.d(TAG, "INFERRED CONTENT TYPE IS " + contentType);
        if (contentType.startsWith("audio")) {
            holder.mImgAttachmentPreview.setImageDrawable(
                ContextCompat.getDrawable(mContext, R.drawable.ic_mic_black_24dp));
            holder.mTvAttachmentTitle.setText(mContext.getString(R.string.text_audio_attachment));
        } else if (contentType.startsWith("video")) {
            holder.mImgAttachmentPreview.setImageDrawable(
                ContextCompat.getDrawable(mContext, R.drawable.ic_videocam_black_24dp));
            holder.mTvAttachmentTitle.setText(
                mContext.getString(R.string.text_video_attachment));
        } else if (contentType.startsWith("image")) {
            // todo: load image using picasso library
        }
    }

    @Override
    public int getItemCount() {
        return mAttachments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public View mItemView;
        public ImageView mImgAttachmentPreview;
        public TextView  mTvAttachmentTitle;
        private OnItemClickListener<String> mClickListener;

        public ViewHolder(View itemView, OnItemClickListener<String> clickListener) {
            super(itemView);
            this.mItemView = itemView;
            this.mImgAttachmentPreview = (ImageView)
                    itemView.findViewById(R.id.img_AttachmentPreview);
            this.mTvAttachmentTitle = (TextView)
                    itemView.findViewById(R.id.tv_AttachmentName);
            this.mClickListener = clickListener;
        }

        public void bind(final String attachment) {
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(attachment);
                }
            });
        }
    }
}
