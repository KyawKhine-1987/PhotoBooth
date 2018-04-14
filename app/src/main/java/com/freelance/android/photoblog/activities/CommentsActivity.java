package com.freelance.android.photoblog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.freelance.android.photoblog.R;
import com.freelance.android.photoblog.adapters.CommentsRecyclerAdapter;
import com.freelance.android.photoblog.models.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private static final String LOG_TAG = CommentsActivity.class.getName();

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private List<Comments> mCommentsList;
    private RecyclerView recyclerView;
    private CommentsRecyclerAdapter adapter;

    private Toolbar tb;
    private EditText etComment;
    private ImageView ivCommentPost;
    private String blogPostId;
    private String currentUserId;

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

        recyclerView = this.findViewById(R.id.rv_commentList);
        etComment = this.findViewById(R.id.etComment);
        ivCommentPost = this.findViewById(R.id.ivCommentPost);

        //RecyclerView Firebase List
        mCommentsList = new ArrayList<>();
        adapter = new CommentsRecyclerAdapter(this, mCommentsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mFirestore.collection("Posts/" + blogPostId + "/Comments")
                .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String commentId = doc.getDocument().getId();
                                    Comments comments = doc.getDocument().toObject(Comments.class);
                                    mCommentsList.add(comments);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });

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
                            } else {
                                etComment.setText("");
                            }
                        }
                    });
                }
            }
        });
    }
}
