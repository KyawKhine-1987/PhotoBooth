package com.freelance.android.photoblog.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.freelance.android.photoblog.R;
import com.freelance.android.photoblog.activities.CommentsActivity;
import com.freelance.android.photoblog.models.BlogPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Kyaw Khine on 04/10/2018.
 */

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.MyViewHolder> {

    private static final String LOG_TAG = BlogRecyclerAdapter.class.getName();

    private Context mContext;
    private List<BlogPost> mBlogPostList;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

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
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Log.i(LOG_TAG, "Test : onBindViewHolder() is called...");

        mContext = holder.mView.getContext(); //test code for Context April 12, 2018
        holder.setIsRecyclable(false);

        String userId = mBlogPostList.get(position).getUser_id();
        //User Data will be retrieved here...
        mFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i(LOG_TAG, "Test : onComplete() in onBindViewHolder() is called...");

                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setBlogUserData(userName, userImage);

                } else {

                    //Firebase Exception
                }
            }
        });

        try {
            long millisecond = mBlogPostList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setBlogDate(dateString);
        } catch (Exception e) {
            Toast.makeText(mContext, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        String desc = mBlogPostList.get(position).getDesc();
        holder.setBlogDesc(desc);

        String image_url = mBlogPostList.get(position).getImage_url();
        String thumbUri = mBlogPostList.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        final String blogPostId = mBlogPostList.get(position).BlogPostId;
        final String currentUserId = mAuth.getCurrentUser().getUid();

        //Get Likes Count
        mFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                Log.i(LOG_TAG, "Test : onEvent() is called...");

                if (!querySnapshot.isEmpty()) {
                    int count = querySnapshot.size();
                    holder.updateLikesCount(count);
                } else {
                    holder.updateLikesCount(0);
                }
            }
        });

        //Get Likes
        mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                Log.i(LOG_TAG, "Test : onEvent() is called...");

                if (documentSnapshot.exists()) {
                    holder.ivBlogLike.setImageDrawable(mContext.getDrawable(R.drawable.action_like_accent));
                } else {
                    holder.ivBlogLike.setImageDrawable(mContext.getDrawable(R.drawable.action_like_gray));
                }
            }
        });

        //Likes Feature
        holder.ivBlogLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in ivBlogLike is called...");

                mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i(LOG_TAG, "Test : onComplete() in ivBlogLike is called...");

                        if (!task.getResult().exists()) {

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        } else {

                            mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        //Comments
        holder.ivBlogComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() is called...");

                Intent comment = new Intent(mContext, CommentsActivity.class);
                comment.putExtra("blogPostId", blogPostId); //This parameter is sent to CommentsActivity.java.
                mContext.startActivity(comment);
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.i(LOG_TAG, "Test : getItemCount() is called...");

        return mBlogPostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final String LOG_TAG = MyViewHolder.class.getName();

        private View mView;
        private TextView tvBlogUserName, tvBlogDate, tvBlogDesc, tvBlogLikeCount;
        private ImageView ivBlog, ivBlogLike, ivBlogComment;
        private CircleImageView civBlogUserImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.i(LOG_TAG, "Test : MyViewHolder() constructor is called...");

            mView = itemView;
            ivBlogLike = mView.findViewById(R.id.ivBlogLike);
            ivBlogComment = mView.findViewById(R.id.ivBlogComment);
        }

        public void setBlogUserData(String name, String image) {
            Log.i(LOG_TAG, "Test : setBlogUserData() constructor is called...");

            tvBlogUserName = mView.findViewById(R.id.tvBlogUserName);
            tvBlogUserName.setText(name);

            civBlogUserImage = mView.findViewById(R.id.civBlogUserImage);
            RequestOptions placeHolderOptions = new RequestOptions();
            placeHolderOptions.placeholder(R.drawable.profile);
            Glide.with(mContext)
                    .applyDefaultRequestOptions(placeHolderOptions)
                    .load(image)
                    .into(civBlogUserImage);
        }

        public void setBlogDate(String date) {
            Log.i(LOG_TAG, "Test : setBlogDate() constructor is called...");

            tvBlogDate = mView.findViewById(R.id.tvBlogDate);
            tvBlogDate.setText(date);
        }

        public void setBlogImage(String downloadUri, String thumbUri) {
            Log.i(LOG_TAG, "Test : setBlogImage() constructor is called...");

            ivBlog = mView.findViewById(R.id.ivBlogImage);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.empty_photo);

            Glide.with(mContext)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(downloadUri)
                    .thumbnail(Glide.with(mContext).load(thumbUri))
                    .into(ivBlog);
        }

        public void setBlogDesc(String descText) {
            Log.i(LOG_TAG, "Test : setBlogDesc() constructor is called...");

            tvBlogDesc = mView.findViewById(R.id.tvBlogDesp);
            tvBlogDesc.setText(descText);
        }

        public void updateLikesCount(int count) {
            Log.i(LOG_TAG, "Test : updateLikesCount() constructor is called...");

            tvBlogLikeCount = mView.findViewById(R.id.tvBlogLikeCount);
            tvBlogLikeCount.setText(count + " Likes");
        }

    }

}
