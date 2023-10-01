package com.ieco.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.ieco.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class ViewArticleActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseStorage STORAGE;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        STORAGE = FirebaseStorage.getInstance();
        USER = AUTH.getCurrentUser();
    }

    RoundedImageView imgArticle;
    TextView tvArticleTitle, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_article);

        initializeFirebase();
        initializeViews();
        loadArticleDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_zoom_pop_enter, R.anim.fade_zoom_still);
    }

    private void initializeViews() {
        imgArticle = findViewById(R.id.imgArticle);
        tvArticleTitle = findViewById(R.id.tvArticleTitle);
        tvContent = findViewById(R.id.tvContent);
    }

    private void loadArticleDetails() {
        String articleTitle = getIntent().getExtras().getString("articleTitle");
        String content = getIntent().getExtras().getString("content");
        String imageFileName = getIntent().getExtras().getString("imageFileName");

        if (imageFileName == null) {
            imgArticle.setVisibility(View.GONE);
        }

        // set article name
        tvArticleTitle.setText(articleTitle);

        // set content
        tvContent.setText(content);

        // set banner
        FirebaseStorage.getInstance().getReference().child("images/"+imageFileName).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).resize(600, 400).centerCrop().into(imgArticle);
                })
                .addOnFailureListener(e -> Picasso.get().load("https://place-hold.it/600x400/fffffc/bbbba1?text=No%20Image").fit().into(imgArticle));
    }
}