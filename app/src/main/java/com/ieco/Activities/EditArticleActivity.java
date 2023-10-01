package com.ieco.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ieco.Fragments.ManageLearnArticlesFragment;
import com.ieco.Objects.Article;
import com.ieco.R;
import com.ieco.Utils.Utils; 
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EditArticleActivity extends AppCompatActivity {

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
    
    ActivityResultLauncher<Intent> activityResultLauncher;
    MaterialButton btnSelectImage, btnSaveArticle, btnDelete;
    RoundedImageView imgArticle;
    TextInputLayout tilArticleName, tilContent;
    TextInputEditText etArticleName, etContent;
    MaterialCardView cvProgress;
    CircularProgressIndicator progressBar;
    TextView tvProgress;

    Uri uriSelected = null;
    Uri uriLoaded = null;
    boolean EDIT_MODE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_article);

        initializeFirebase();
        initializeActivityResultLauncher();
        initializeViews();
        checkForArticleArgs();
        handleButtonClicks();
    }

    @Override
    protected void onPause() {
        if (EDIT_MODE) {
            overridePendingTransition(R.anim.slide_pop_enter, R.anim.slide_pop_exit);
        }
        else {
            overridePendingTransition(R.anim.fade_zoom_pop_enter, R.anim.fade_zoom_still);
        }

        super.onPause();
    }

    private void initializeViews() {
        imgArticle = findViewById(R.id.imgArticle);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        tilArticleName = findViewById(R.id.tilArticleName);
        etArticleName = findViewById(R.id.etArticleName);
        tilContent = findViewById(R.id.tilContent);
        etContent = findViewById(R.id.etContent);
        btnSaveArticle = findViewById(R.id.btnSaveArticle);
        cvProgress = findViewById(R.id.cvProgress);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void checkForArticleArgs() {
        EDIT_MODE = getIntent().getExtras() != null;
        if (EDIT_MODE) {
            loadArticleDetails();
        }
        else {
            Picasso.get().load("https://place-hold.it/600x400/fffffc/bbbba1?text=No%20Image").fit().into(imgArticle);
        }
    }

    private void loadArticleDetails() {
        String articleTitle = getIntent().getStringExtra("articleTitle");
        String content = getIntent().getStringExtra("content");
        String imageFileName = getIntent().getStringExtra("imageFileName");
        long schedule = getIntent().getLongExtra("schedule", 0);

        // show delete button
        btnDelete.setVisibility(View.VISIBLE);

        // set article titlle
        etArticleName.setText(articleTitle);

        // set content
        etContent.setText(content);

        // set IMAGE
        FirebaseStorage.getInstance().getReference().child("images/"+imageFileName).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    uriLoaded = uri;
                    uriSelected = uri;
                    Picasso.get().load(uri).resize(600, 400).centerCrop().into(imgArticle);
                })
                .addOnFailureListener(e -> Picasso.get().load("https://place-hold.it/600x400/fffffc/bbbba1?text=No%20Image").fit().into(imgArticle));
    }

    private void handleButtonClicks() {
        btnSelectImage.setOnClickListener(view -> {
            selectImageFromDevice();
        });

        btnSaveArticle.setOnClickListener(view -> {
            Utils.hideKeyboard(EditArticleActivity.this);
            validateInputs();
        });

        btnDelete.setOnClickListener(view -> {
            Utils.hideKeyboard(EditArticleActivity.this);
            deleteArticle();
        });
    }

    private void validateInputs() {
        String articleTitle = String.valueOf(etArticleName.getText());
        String content = String.valueOf(etContent.getText());

        if (articleTitle.isEmpty()) {
            tilArticleName.setError("Please enter an article titlle.");
        }
        else {
            tilArticleName.setError(null);
        }

        if (content.isEmpty()) {
            tilContent.setError("Please provide a body content for the article.");
        }
        else {
            tilContent.setError(null);
        }

        if (articleTitle.isEmpty() ||  content.isEmpty()) {
            return;
        }

        saveArticle(articleTitle, content);
    }

    private void saveArticle(String articleTitle, String content) {
        cvProgress.setVisibility(View.VISIBLE);

        DocumentReference refArticle;
        if (!EDIT_MODE) {
            refArticle = DB.collection("articles").document();
        }
        else {
            refArticle = DB.collection("articles").document(getIntent().getStringExtra("id"));
        }

        boolean AN_IMAGE_WAS_SELECTED = uriSelected != null;
        boolean IMAGE_WAS_REPLACED = uriSelected != uriLoaded;
        String imageFileName = null;

        if (AN_IMAGE_WAS_SELECTED) {
            if (IMAGE_WAS_REPLACED) {
                imageFileName = String.valueOf(System.currentTimeMillis());
            }
            else {
                imageFileName = getIntent().getStringExtra("imageFileName");
            }
        }

        Article article = new Article(
                refArticle.getId(),
                articleTitle,
                content,
                imageFileName
        );

        StorageReference IMAGEReference = STORAGE.getReference().child("images/"+imageFileName);

        if (AN_IMAGE_WAS_SELECTED && IMAGE_WAS_REPLACED) {
            UploadTask uploadTask = IMAGEReference.putFile(uriSelected);

            uploadTask.addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Failed to upload IMAGE. Please try again.", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                refArticle.set(article)
                        .addOnSuccessListener(unused -> {
                            /*FragmentManager fragmentManager = EditArticleActivity.this.getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment manageArticlesFragment = new ManageLearnArticlesFragment();
                            fragmentTransaction.replace(R.id.fragmentHolder, manageArticlesFragment, "MANAGE_LEARN_ARTICLES_FRAGMENT");
                            fragmentTransaction.addToBackStack("MANAGE_LEARN_ARTICLES_FRAGMENT");
                            fragmentTransaction.commit();*/

                            cvProgress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Saved article", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                Log.d("DEBUG", "Upload is " + progress + "% done");

                if (progress > 0 && progress < 100) {
                    progressBar.setProgress((int) progress);
                    tvProgress.setText("("+ (int) progress +"%) Uploading article data...");
                }
            });
        }
        else {
            refArticle.set(article)
                    .addOnSuccessListener(unused -> {
                        /*FragmentManager fragmentManager = EditArticleActivity.this.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment manageArticlesFragment = new ManageLearnArticlesFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, manageArticlesFragment, "MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.commit();*/

                        cvProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Saved article", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }

    private void deleteArticle() {
        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(EditArticleActivity.this);
        dialogDelete.setTitle("Delete Article");
        dialogDelete.setMessage("Do you want to delete the article \""+getIntent().getStringExtra("articleTitle")+"\"?");
        dialogDelete.setNeutralButton("Cancel", (dialogInterface, i) -> { });
        dialogDelete.setNegativeButton("Delete", (dialogInterface, i) -> {
            DocumentReference refArticle = DB.collection("articles").document(getIntent().getStringExtra("id"));
            refArticle.delete().addOnSuccessListener(unused -> {
                        /*FragmentManager fragmentManager = EditArticleActivity.this.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment manageArticlesFragment = new ManageLearnArticlesFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, manageArticlesFragment, "MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.commit();*/

                        Toast.makeText(getApplicationContext(), "Article was successfully deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to delete article. Please try again.", Toast.LENGTH_SHORT).show());
        });
        dialogDelete.show();
    }

    private void selectImageFromDevice() {
        Intent iImageSelect = new Intent();
        iImageSelect.setType("image/*");
        iImageSelect.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(Intent.createChooser(iImageSelect, "Select Article IMAGE"));
    }

    private void initializeActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uriRetrieved = Objects.requireNonNull(data).getData();
                        uriSelected = uriRetrieved;

                        // display selected image
                        Picasso.get().load(uriRetrieved).resize(350,175).centerCrop().into(imgArticle);
                    }
                }
        );
    }
}