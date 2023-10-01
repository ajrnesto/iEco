package com.ieco.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.ieco.Fragments.ManageFAQsFragment;
import com.ieco.Objects.Faq;
import com.ieco.R;
import com.ieco.Utils.Utils;

public class EditFaqsActivity extends AppCompatActivity {

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
    MaterialButton btnSaveFaq, btnDelete;
    TextInputLayout tilQuestion, tilAnswer;
    TextInputEditText etQuestion, etAnswer;

    boolean EDIT_MODE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_faq);

        initializeFirebase();
        initializeViews();
        checkForFaqArgs();
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
        tilQuestion = findViewById(R.id.tilQuestion);
        etQuestion = findViewById(R.id.etQuestion);
        tilAnswer = findViewById(R.id.tilAnswer);
        etAnswer = findViewById(R.id.etAnswer);
        btnSaveFaq = findViewById(R.id.btnSaveFaq);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void checkForFaqArgs() {
        EDIT_MODE = getIntent().getExtras() != null;
        if (EDIT_MODE) {
            loadFaqDetails();
        }
    }

    private void loadFaqDetails() {
        String question = getIntent().getExtras().getString("question");
        String answer = getIntent().getExtras().getString("answer");
        String imageFileName = getIntent().getExtras().getString("imageFileName");
        long schedule = getIntent().getExtras().getLong("schedule");

        // show delete button
        btnDelete.setVisibility(View.VISIBLE);

        // set faq titlle
        etQuestion.setText(question);

        // set answer
        etAnswer.setText(answer);
    }

    private void handleButtonClicks() {
        btnSaveFaq.setOnClickListener(view -> {
            Utils.hideKeyboard(EditFaqsActivity.this);
            validateInputs();
        });

        btnDelete.setOnClickListener(view -> {
            Utils.hideKeyboard(EditFaqsActivity.this);
            deleteFaq();
        });
    }

    private void validateInputs() {
        String question = String.valueOf(etQuestion.getText());
        String answer = String.valueOf(etAnswer.getText());

        if (question.isEmpty()) {
            tilQuestion.setError("Please enter an faq titlle.");
        }
        else {
            tilQuestion.setError(null);
        }

        if (answer.isEmpty()) {
            tilAnswer.setError("Please provide a body answer for the faq.");
        }
        else {
            tilAnswer.setError(null);
        }

        if (question.isEmpty() ||  answer.isEmpty()) {
            return;
        }

        saveFaq(question, answer);
    }

    private void saveFaq(String question, String answer) {
        DocumentReference refFaq;
        if (!EDIT_MODE) {
            refFaq = DB.collection("faqs").document();
        }
        else {
            refFaq = DB.collection("faqs").document(getIntent().getExtras().getString("id"));
        }

        Faq faq = new Faq(
                refFaq.getId(),
                question,
                answer
        );

        refFaq.set(faq)
                .addOnSuccessListener(unused -> {
                    /*FragmentManager fragmentManager = EditFaqsActivity.this.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment manageFaqsFragment = new ManageFAQsFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, manageFaqsFragment, "MANAGE_FAQS_FRAGMENT");
                    fragmentTransaction.addToBackStack("MANAGE_FAQS_FRAGMENT");
                    fragmentTransaction.commit();*/

                    Toast.makeText(getApplicationContext(), "Saved FAQ", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void deleteFaq() {
        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(EditFaqsActivity.this);
        dialogDelete.setTitle("Delete FAQ");
        dialogDelete.setMessage("Do you want to delete the faq \""+getIntent().getExtras().getString("question")+"\"?");
        dialogDelete.setNeutralButton("Cancel", (dialogInterface, i) -> { });
        dialogDelete.setNegativeButton("Delete", (dialogInterface, i) -> {
            DocumentReference refFaq = DB.collection("faqs").document(getIntent().getExtras().getString("id"));
            refFaq.delete().addOnSuccessListener(unused -> {
                        /*FragmentManager fragmentManager = EditFaqsActivity.this.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment manageFaqsFragment = new ManageFAQsFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, manageFaqsFragment, "MANAGE_FAQ_FRAGMENT");
                        fragmentTransaction.addToBackStack("MANAGE_FAQ_FRAGMENT");
                        fragmentTransaction.commit();*/

                        Toast.makeText(getApplicationContext(), "FAQ was successfully deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to delete FAQ. Please try again.", Toast.LENGTH_SHORT).show());
        });
        dialogDelete.show();
    }
}