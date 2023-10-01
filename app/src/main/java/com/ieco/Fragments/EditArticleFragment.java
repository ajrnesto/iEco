package com.ieco.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ieco.Objects.Article;
import com.ieco.R;
import com.ieco.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class EditArticleFragment extends Fragment {

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

    View view;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_article, container, false);

        initializeFirebase();
        initializeActivityResultLauncher();
        initializeViews();
        checkForArticleArgs();
        handleButtonClicks();

        return view;
    }

    private void initializeViews() {
        imgArticle = view.findViewById(R.id.imgArticle);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        tilArticleName = view.findViewById(R.id.tilArticleName);
        etArticleName = view.findViewById(R.id.etArticleName);
        tilContent = view.findViewById(R.id.tilContent);
        etContent = view.findViewById(R.id.etContent);
        btnSaveArticle = view.findViewById(R.id.btnSaveArticle);
        cvProgress = view.findViewById(R.id.cvProgress);
        progressBar = view.findViewById(R.id.progressBar);
        tvProgress = view.findViewById(R.id.tvProgress);
        btnDelete = view.findViewById(R.id.btnDelete);
    }

    private void checkForArticleArgs() {
        EDIT_MODE = getArguments() != null;
        if (EDIT_MODE) {
            loadArticleDetails();
        }
        else {
            Picasso.get().load("https://place-hold.it/600x400/fffffc/bbbba1?text=No%20Image").fit().into(imgArticle);
        }
    }

    private void loadArticleDetails() {
        String articleTitle = requireArguments().getString("articleTitle");
        String content = requireArguments().getString("content");
        String imageFileName = requireArguments().getString("imageFileName");
        long schedule = requireArguments().getLong("schedule");

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
            Utils.hideKeyboard(requireActivity());
            validateInputs();
        });

        btnDelete.setOnClickListener(view -> {
            Utils.hideKeyboard(requireActivity());
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
            refArticle = DB.collection("articles").document(requireArguments().getString("id"));
        }

        boolean AN_IMAGE_WAS_SELECTED = uriSelected != null;
        boolean IMAGE_WAS_REPLACED = uriSelected != uriLoaded;
        String imageFileName = null;

        if (AN_IMAGE_WAS_SELECTED) {
            if (IMAGE_WAS_REPLACED) {
                imageFileName = String.valueOf(System.currentTimeMillis());
            }
            else {
                imageFileName = requireArguments().getString("imageFileName");
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
                Toast.makeText(requireContext(), "Failed to upload IMAGE. Please try again.", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                refArticle.set(article)
                        .addOnSuccessListener(unused -> {
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment manageArticlesFragment = new ManageLearnArticlesFragment();
                            fragmentTransaction.replace(R.id.fragmentHolder, manageArticlesFragment, "MANAGE_LEARN_ARTICLES_FRAGMENT");
                            fragmentTransaction.addToBackStack("MANAGE_LEARN_ARTICLES_FRAGMENT");
                            fragmentTransaction.commit();

                            cvProgress.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Saved article", Toast.LENGTH_SHORT).show();
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
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment manageArticlesFragment = new ManageLearnArticlesFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, manageArticlesFragment, "MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.commit();

                        cvProgress.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Saved article", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void deleteArticle() {
        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(requireContext());
        dialogDelete.setTitle("Delete Article");
        dialogDelete.setMessage("Do you want to delete the article \""+requireArguments().getString("articleTitle")+"\"?");
        dialogDelete.setNeutralButton("Cancel", (dialogInterface, i) -> { });
        dialogDelete.setNegativeButton("Delete", (dialogInterface, i) -> {
            DocumentReference refArticle = DB.collection("articles").document(requireArguments().getString("id"));
            refArticle.delete().addOnSuccessListener(unused -> {
                Toast.makeText(requireContext(), "Article was successfully deleted.", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment manageArticlesFragment = new ManageLearnArticlesFragment();
                fragmentTransaction.replace(R.id.fragmentHolder, manageArticlesFragment, "MANAGE_EVENTS_FRAGMENT");
                fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                fragmentTransaction.commit();
            })
            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to delete article. Please try again.", Toast.LENGTH_SHORT).show());
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