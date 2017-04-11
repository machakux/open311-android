package com.github.codetanzania.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.codetanzania.model.Comment;
import com.github.codetanzania.util.Util;

import java.util.List;
import java.util.Locale;

import tz.co.codetanzania.R;

public class InternalNotesAdapter extends
        RecyclerView.Adapter<InternalNotesAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    public InternalNotesAdapter(Context mContext, List<Comment> mComments) {
        this.mContext = mContext;
        // todo: sort comment by date in a descending order
        this.mComments = mComments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View logView = inflater.inflate(R.layout.internal_note_content, parent, false);
        return new ViewHolder(logView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // retrieve comment
        Comment comment = mComments.get(position);
        // bind data
        holder.bind(mContext, comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        // ticket title
        private TextView mLogContent;
        // ticket id
        private TextView mLogExtras;

        ViewHolder(View itemView) {
            super(itemView);
            mLogContent = (TextView) itemView.findViewById(R.id.tv_LogContent);
            mLogExtras  = (TextView) itemView.findViewById(R.id.tv_LogExtras);
        }

        void bind(Context context, Comment comment) {
            mLogContent.setText(comment.content);
            String formatted = Util.formatDate(comment.timestamp, "MM-dd-yy HH:mm:ss");
            mLogExtras.setText(String.format(Locale.getDefault(), "%s @%s"
                    /*context.getString(R.string.text_log_extras)*/, comment.commentor, formatted));
        }
    }
}
