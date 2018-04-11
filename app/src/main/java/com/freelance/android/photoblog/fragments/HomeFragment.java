package com.freelance.android.photoblog.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.freelance.android.photoblog.R;
import com.freelance.android.photoblog.adapters.BlogRecyclerAdapter;
import com.freelance.android.photoblog.models.BlogPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String LOG_TAG = HomeFragment.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private List<BlogPost> mBlogPostList;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter adapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public HomeFragment() {
        Log.i(LOG_TAG, "Test : HomeFragment() is called...");
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreateView() is called...");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBlogPostList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_blogList);

        mAuth = FirebaseAuth.getInstance();

        adapter = new BlogRecyclerAdapter(getActivity(), mBlogPostList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        if (mAuth.getCurrentUser() != null) {

            mFirestore = FirebaseFirestore.getInstance();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    Log.i(LOG_TAG, "Test : onScrolled() is called...");
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean isReachedBottom = !recyclerView.canScrollVertically(1); //! meant that reached the post bottom.
                    if (isReachedBottom) {

                        String desc = lastVisible.getString("desc");
                        Toast.makeText(container.getContext(), "Reached : " + desc, Toast.LENGTH_SHORT).show();

                        loadMorePost();
                    }
                }
            });

            Query firstQuery = mFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    Log.i(LOG_TAG, "Test : onEvent() is called...");

                    //when hit the logout in main_menu.xml, which is occurred the error name is "firebase on a null object reference" ===>  loadMorePost method & also need to put this "getActivity()".

                    if (isFirstPageFirstLoad) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                            if (isFirstPageFirstLoad) {
                                mBlogPostList.add(blogPost);
                            } else {
                                mBlogPostList.add(0, blogPost);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            });
        }
        return view;
    }

    public void loadMorePost() {
        Log.i(LOG_TAG, "Test : nextQuery() is called...");

        Query nextQuery = mFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        //startAfter meant that 1 2 3, 4 5 6, 7 8 9
        //startAt meant that 1 2 3, 3 4 5, 5 6 7

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                Log.i(LOG_TAG, "Test : onEvent() is called...");

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            mBlogPostList.add(blogPost);

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}
