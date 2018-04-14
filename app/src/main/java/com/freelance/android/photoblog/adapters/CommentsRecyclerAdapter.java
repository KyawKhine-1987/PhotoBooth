package com.freelance.android.photoblog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freelance.android.photoblog.R;
import com.freelance.android.photoblog.models.Comments;

import java.util.List;

/**
 * Created by Kyaw Khine on 04/11/2018.
 */

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.MyViewHolder> {

    private static final String LOG_TAG = CommentsRecyclerAdapter.class.getName();

    private Context mContext;
    private List<Comments> mCommentsList;

    public CommentsRecyclerAdapter(Context context, List<Comments> commentsList) {
        Log.i(LOG_TAG, "Test : onCreateViewHolder() is called...");

        this.mContext = context;
        this.mCommentsList = commentsList;
    }

    @Override
    public CommentsRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(LOG_TAG, "Test : onCreateViewHolder() is called...");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_commentlist_item, parent, false);
        mContext = parent.getContext();
        return new CommentsRecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.MyViewHolder holder, int position) {
        Log.i(LOG_TAG, "Test : onBindViewHolder() is called...");

        String commentMessage = mCommentsList.get(position).getMessage();
        holder.setCommentMessage(commentMessage);
    }

    @Override
    public int getItemCount() {
        Log.i(LOG_TAG, "Test : getItemCount() is called...");

        if (mCommentsList != null) {
            return mCommentsList.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final String LOG_TAG = CommentsRecyclerAdapter.class.getName();

        private View mView;
        private TextView tvCommentMessage;

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.i(LOG_TAG, "Test : MyViewHolder() is called...");

            mView = itemView;
        }

        public void setCommentMessage(String message){
            Log.i(LOG_TAG, "Test : MyViewHolder() is called...");

            tvCommentMessage = mView.findViewById(R.id.tvCommentMessage);
            tvCommentMessage.setText(message);
        }
    }
}
