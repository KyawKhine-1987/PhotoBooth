package com.freelance.android.photoblog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.freelance.android.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private static final String LOG_TAG = CommentsActivity.class.getName();

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private Toolbar tb;
    private EditText etComment;
    private ImageView ivCommentPost;
    private String blogPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        tb = (Toolbar) this.findViewById(R.id.tbComments);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Comments");

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        blogPostId = getIntent().getStringExtra("blogPostId");//Get the parameter from BlogRecyclerAdapter.java.

        etComment = this.findViewById(R.id.etComment);
        ivCommentPost = this.findViewById(R.id.ivCommentPost);

        ivCommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() is called...");

                String commentMessage = etComment.getText().toString();

                if (!commentMessage.isEmpty()) {

                    final Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", commentMessage);
                    commentsMap.put("user_id", currentUserId);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());

                    mFirestore.collection("Posts/" + blogPostId + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(CommentsActivity.this, "Error Posing Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else{
                                etComment.setText("");
                            }
                        }
                    });
                }
            }
        });
    }
}
