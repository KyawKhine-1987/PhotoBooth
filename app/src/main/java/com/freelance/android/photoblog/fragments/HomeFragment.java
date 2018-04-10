package com.freelance.android.photoblog.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freelance.android.photoblog.R;
import com.freelance.android.photoblog.adapters.BlogRecyclerAdapter;
import com.freelance.android.photoblog.models.BlogPost;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String LOG_TAG = HomeFragment.class.getName();

    private FirebaseFirestore mFirestore;
    private List<BlogPost> mBlogPostList;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter adapter;

    public HomeFragment() {
        Log.i(LOG_TAG, "Test : HomeFragment() is called...");
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreateView() is called...");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBlogPostList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_blogList);

        adapter = new BlogRecyclerAdapter(getActivity(), mBlogPostList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                Log.i(LOG_TAG, "Test : onEvent() is called...");

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                        mBlogPostList.add(blogPost);

                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }
}
