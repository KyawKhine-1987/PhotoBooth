package com.freelance.android.photoblog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.freelance.android.photoblog.R;
import com.freelance.android.photoblog.models.BlogPost;

import java.util.Date;
import java.util.List;

/**
 * Created by Kyaw Khine on 04/10/2018.
 */

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.MyViewHolder> {

    private static final String LOG_TAG = BlogRecyclerAdapter.class.getName();

    private Context mContext;
    private List<BlogPost> mBlogPostList;

    public BlogRecyclerAdapter(Context context, List<BlogPost> blogPostList) {
        Log.i(LOG_TAG, "Test : BlogRecyclerAdapter() is called...");

        this.mContext = context;
        this.mBlogPostList = blogPostList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(LOG_TAG, "Test : onCreateViewHolder() is called...");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bloglist_item, parent, false);
        mContext = parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i(LOG_TAG, "Test : onBindViewHolder() is called...");

        String userId = mBlogPostList.get(position).getUser_id();
        holder.setBlogUserId(userId);

        long millisecond = mBlogPostList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        holder.setBlogDate(dateString);

        String desc = mBlogPostList.get(position).getDesc();
        holder.setBlogDesc(desc);

        String image_url = mBlogPostList.get(position).getImage_url();
        holder.setBlogImage(image_url);


    }

    @Override
    public int getItemCount() {
        Log.i(LOG_TAG, "Test : getItemCount() is called...");

        return mBlogPostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final String LOG_TAG = MyViewHolder.class.getName();
        private View mView;
        private TextView tvUserId, tvBlogDate, tvDesc;
        private ImageView ivBlog;

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.i(LOG_TAG, "Test : MyViewHolder() is called...");
            mView = itemView;
        }

        public void setBlogUserId(String userId) {

            tvUserId = mView.findViewById(R.id.tvBlogUserName);
            tvUserId.setText(userId);
        }

        public void setBlogDate(String date){

            tvBlogDate = mView.findViewById(R.id.tvBlogDate);
            tvBlogDate.setText(date);
        }

        public void setBlogImage(String downloadUri) {

            ivBlog = mView.findViewById(R.id.ivBlogImage);
            Glide.with(mContext).load(downloadUri).into(ivBlog);
        }

        public void setBlogDesc(String descText) {

            tvDesc = mView.findViewById(R.id.tvBlogDesp);
            tvDesc.setText(descText);
        }

    }

}
