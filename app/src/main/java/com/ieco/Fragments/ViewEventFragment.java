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
import com.ieco.Objects.Event;
import com.ieco.R;
import com.ieco.Utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ViewEventFragment extends Fragment {

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
    RoundedImageView imgEvent;
    TextView tvEventName, tvDescription, tvSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_event, container, false);

        initializeFirebase();
        initializeViews();
        loadEventDetails();

        return view;
    }

    private void initializeViews() {
        imgEvent = view.findViewById(R.id.imgEvent);
        tvEventName = view.findViewById(R.id.tvEventName);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvSchedule = view.findViewById(R.id.tvSchedule);
    }

    private void loadEventDetails() {
        String eventName = requireArguments().getString("eventName");
        String description = requireArguments().getString("description");
        String bannerFileName = requireArguments().getString("bannerFileName");
        long schedule = requireArguments().getLong("schedule");

        if (bannerFileName == null) {
            imgEvent.setVisibility(View.GONE);
        }

        // set event name
        tvEventName.setText(eventName);

        // set description
        tvDescription.setText(description);

        // set date schedule
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMMM d, yyyy h:mm aa");
        tvSchedule.setText(sdfDate.format(schedule));

        // set banner
        FirebaseStorage.getInstance().getReference().child("images/"+bannerFileName).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).resize(600, 400).centerCrop().into(imgEvent);
                })
                .addOnFailureListener(e -> Picasso.get().load("https://place-hold.it/600x400/fffffc/bbbba1?text=No%20Image").fit().into(imgEvent));
    }
}