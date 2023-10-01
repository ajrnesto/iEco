package com.ieco.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ieco.Activities.EditArticleActivity;
import com.ieco.Activities.EditEventActivity;
import com.ieco.Adapters.ArticleAdapter;
import com.ieco.Objects.Article;
import com.ieco.Objects.Event;
import com.ieco.R;

import java.util.ArrayList;

public class ManageLearnArticlesFragment extends Fragment implements ArticleAdapter.OnClick {

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
    ExtendedFloatingActionButton btnAddArticle;
    ArrayList<Article> arrArticles;
    ArticleAdapter articleAdapter;
    ArticleAdapter.OnClick onClick = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_learn_articles, container, false);

        initializeFirebase();
        initializeViews();
        handleButtonClicks();
        loadListOfArticles();

        return view;
    }

    private void initializeViews() {
        btnAddArticle = view.findViewById(R.id.btnAddArticle);
        rvArticles = view.findViewById(R.id.rvArticles);
    }

    private void loadListOfArticles() {
        arrArticles = new ArrayList<>();
        rvArticles.setHasFixedSize(true);
        rvArticles.setLayoutManager(new LinearLayoutManager(requireContext()));

        DB.collection("articles")
                /*.addSnapshotListener(new ArticleListener<QuerySnapshot>() {
                    @Override
                    public void onArticle(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                })*/
                .get()
                .addOnSuccessListener(value -> {
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
                });

        articleAdapter = new ArticleAdapter(requireContext(), requireActivity(), arrArticles, onClick);
        rvArticles.setAdapter(articleAdapter);
    }

    private void handleButtonClicks() {
        btnAddArticle.setOnClickListener(view -> {
            // old code...
                /*Fragment editArticleFragment = new EditArticleFragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentHolder, editArticleFragment, "EDIT_ARTICLE_FRAGMENT");
                fragmentTransaction.addToBackStack("EDIT_ARTICLE_FRAGMENT");
                fragmentTransaction.commit();*/
            // ...replaced with this new code
            Intent iAddArticle = new Intent(requireActivity(), EditArticleActivity.class);
            startActivity(iAddArticle);
            requireActivity().overridePendingTransition(R.anim.fade_zoom_enter, R.anim.fade_zoom_pop_enter);
        });
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
        
        // old code...
            /*Fragment editArticleFragment = new EditArticleFragment();
            editArticleFragment.setArguments(args);
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHolder, editArticleFragment, "EDIT_ARTICLE_FRAGMENT");
            fragmentTransaction.addToBackStack("EDIT_ARTICLE_FRAGMENT");
            fragmentTransaction.commit();*/
        // ...replaced with this new code
        Intent iEditArticle = new Intent(requireActivity(), EditArticleActivity.class);
        iEditArticle.putExtras(args);
        startActivity(iEditArticle);
        requireActivity().overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}