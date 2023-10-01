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
import com.ieco.Objects.Faq;
import com.ieco.R;
import com.ieco.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EditFaqFragment extends Fragment {

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
    MaterialButton btnSaveFaq, btnDelete;
    TextInputLayout tilQuestion, tilAnswer;
    TextInputEditText etQuestion, etAnswer;

    boolean EDIT_MODE = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_faq, container, false);

        initializeFirebase();
        initializeViews();
        checkForFaqArgs();
        handleButtonClicks();

        return view;
    }

    private void initializeViews() {
        tilQuestion = view.findViewById(R.id.tilQuestion);
        etQuestion = view.findViewById(R.id.etQuestion);
        tilAnswer = view.findViewById(R.id.tilAnswer);
        etAnswer = view.findViewById(R.id.etAnswer);
        btnSaveFaq = view.findViewById(R.id.btnSaveFaq);
        btnDelete = view.findViewById(R.id.btnDelete);
    }

    private void checkForFaqArgs() {
        EDIT_MODE = getArguments() != null;
        if (EDIT_MODE) {
            loadFaqDetails();
        }
    }

    private void loadFaqDetails() {
        String question = requireArguments().getString("question");
        String answer = requireArguments().getString("answer");
        String imageFileName = requireArguments().getString("imageFileName");
        long schedule = requireArguments().getLong("schedule");

        // show delete button
        btnDelete.setVisibility(View.VISIBLE);

        // set faq titlle
        etQuestion.setText(question);

        // set answer
        etAnswer.setText(answer);
    }

    private void handleButtonClicks() {
        btnSaveFaq.setOnClickListener(view -> {
            Utils.hideKeyboard(requireActivity());
            validateInputs();
        });

        btnDelete.setOnClickListener(view -> {
            Utils.hideKeyboard(requireActivity());
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
            refFaq = DB.collection("faqs").document(requireArguments().getString("id"));
        }

        Faq faq = new Faq(
                refFaq.getId(),
                question,
                answer
        );

        refFaq.set(faq)
                .addOnSuccessListener(unused -> {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment manageFaqsFragment = new ManageFAQsFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, manageFaqsFragment, "MANAGE_FAQS_FRAGMENT");
                    fragmentTransaction.addToBackStack("MANAGE_FAQS_FRAGMENT");
                    fragmentTransaction.commit();

                    Toast.makeText(requireContext(), "Saved FAQ", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteFaq() {
        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(requireContext());
        dialogDelete.setTitle("Delete FAQ");
        dialogDelete.setMessage("Do you want to delete the faq \""+requireArguments().getString("question")+"\"?");
        dialogDelete.setNeutralButton("Cancel", (dialogInterface, i) -> { });
        dialogDelete.setNegativeButton("Delete", (dialogInterface, i) -> {
            DocumentReference refFaq = DB.collection("faqs").document(requireArguments().getString("id"));
            refFaq.delete().addOnSuccessListener(unused -> {
                Toast.makeText(requireContext(), "FAQ was successfully deleted.", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment manageFaqsFragment = new ManageFAQsFragment();
                fragmentTransaction.replace(R.id.fragmentHolder, manageFaqsFragment, "MANAGE_FAQ_FRAGMENT");
                fragmentTransaction.addToBackStack("MANAGE_FAQ_FRAGMENT");
                fragmentTransaction.commit();
            })
            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to delete FAQ. Please try again.", Toast.LENGTH_SHORT).show());
        });
        dialogDelete.show();
    }
}