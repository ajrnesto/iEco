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

import java.text.SimpleDateFormat;

public class ViewEventActivity extends AppCompatActivity {

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

    RoundedImageView imgEvent;
    TextView tvEventName, tvDescription, tvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_event);

        initializeFirebase();
        initializeViews();
        loadEventDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_pop_enter, R.anim.slide_pop_exit);
    }

    private void initializeViews() {
        imgEvent = findViewById(R.id.imgEvent);
        tvEventName = findViewById(R.id.tvEventName);
        tvDescription = findViewById(R.id.tvDescription);
        tvSchedule = findViewById(R.id.tvSchedule);
    }

    private void loadEventDetails() {
        String eventName = getIntent().getExtras().getString("eventName");
        String description = getIntent().getExtras().getString("description");
        String bannerFileName = getIntent().getExtras().getString("bannerFileName");
        long schedule = getIntent().getExtras().getLong("schedule");

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