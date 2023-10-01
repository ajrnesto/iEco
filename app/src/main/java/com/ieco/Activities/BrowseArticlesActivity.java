package com.ieco.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.ieco.Fragments.ViewArticleFragment;
import com.ieco.Objects.Article;
import com.ieco.R;

import java.util.ArrayList;

public class BrowseArticlesActivity extends AppCompatActivity implements ArticleAdapter.OnClick {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    RecyclerView rvArticles;
    ArrayList<Article> arrArticles;
    ArticleAdapter articleAdapter;
    ArticleAdapter.OnClick onClick = this;
    MaterialButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_browse_articles);

        initializeFirebase();
        initializeViews();
        loadListOfArticles();

        btnMenu.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_zoom_pop_enter, R.anim.fade_zoom_still);
    }

    private void initializeViews() {
        rvArticles = findViewById(R.id.rvArticles);
        btnMenu = findViewById(R.id.btnMenu);
    }

    private void loadListOfArticles() {
        arrArticles = new ArrayList<>();
        rvArticles.setHasFixedSize(true);
        rvArticles.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        Query queryAlphabetical = DB.collection("articles").orderBy("articleTitle", Query.Direction.ASCENDING);

        queryAlphabetical.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

        articleAdapter = new ArticleAdapter(BrowseArticlesActivity.this, BrowseArticlesActivity.this, arrArticles, onClick);
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

        /*Fragment viewArticleFragment = new ViewArticleFragment();
        viewArticleFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, viewArticleFragment, "VIEW_ARTICLE_FRAGMENT");
        fragmentTransaction.addToBackStack("VIEW_ARTICLE_FRAGMENT");
        fragmentTransaction.commit();*/

        Intent iViewArticle = new Intent(BrowseArticlesActivity.this, ViewArticleActivity.class);
        iViewArticle.putExtras(args);
        startActivity(iViewArticle);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_pop_enter);
    }
}