package com.ieco.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ieco.Adapters.ArticleAdapter;
import com.ieco.Objects.Article;
import com.ieco.R;

import java.util.ArrayList;
import java.util.Calendar;

public class BrowseArticlesFragment extends Fragment implements ArticleAdapter.OnClick {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;
    RecyclerView rvArticles;
    ArrayList<Article> arrArticles;
    ArticleAdapter articleAdapter;
    ArticleAdapter.OnClick onClick = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_browse_articles, container, false);

        initializeFirebase();
        initializeViews();
        loadListOfArticles();

        return view;
    }

    private void initializeViews() {
        rvArticles = view.findViewById(R.id.rvArticles);
    }

    private void loadListOfArticles() {
        arrArticles = new ArrayList<>();
        rvArticles.setHasFixedSize(true);
        rvArticles.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        DB.collection("articles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        arrArticles.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            Article article = doc.toObject(Article.class);
                            arrArticles.add(article);

                            articleAdapter.notifyDataSetChanged();
                        }

                        if (arrArticles.isEmpty()) {
                            rvArticles.setVisibility(View.INVISIBLE);
                        }
                        else {
                            rvArticles.setVisibility(View.VISIBLE);
                        }
                    }
                });

        articleAdapter = new ArticleAdapter(requireContext(), requireActivity(), arrArticles, onClick);
        rvArticles.setAdapter(articleAdapter);
    }

    @Override
    public void onClick(int position) {
        Article article = arrArticles.get(position);

        String id = article.getId();
        String articleTitle = article.getArticleTitle();
        String content = article.getContent();
        String imageFileName = article.getImageFileName();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("articleTitle", articleTitle);
        args.putString("content", content);
        args.putString("imageFileName", imageFileName);

        Fragment viewArticleFragment = new ViewArticleFragment();
        viewArticleFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, viewArticleFragment, "VIEW_ARTICLE_FRAGMENT");
        fragmentTransaction.addToBackStack("VIEW_ARTICLE_FRAGMENT");
        fragmentTransaction.commit();
    }
}