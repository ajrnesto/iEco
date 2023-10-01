package com.ieco.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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

public class EditEventActivity extends AppCompatActivity {

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
    MaterialButton btnSelectImage, btnSaveEvent, btnDelete;
    RoundedImageView imgEvent;
    TextInputLayout tilEventName, tilDate, tilTime, tilDescription;
    TextInputEditText etEventName, etDate, etTime, etDescription;
    MaterialDatePicker.Builder<Long> bSchedule;
    MaterialDatePicker<Long> dpSchedule;
    MaterialTimePicker tpSchedule;
    long tpScheduleSelection = 0;
    long dpScheduleSelection = 0;
    MaterialCardView cvProgress;
    CircularProgressIndicator progressBar;
    TextView tvProgress;

    Uri uriSelected = null;
    Uri uriLoaded = null;
    boolean EDIT_MODE = false;
    int SELECT_IMAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_event);

        initializeFirebase();
        initializeViews();
        initializeActivityResultLauncher();
        checkForEventArgs();
        initializeDatePicker();
        initializeTimePicker();
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
        imgEvent = findViewById(R.id.imgEvent);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        tilEventName = findViewById(R.id.tilEventName);
        etEventName = findViewById(R.id.etEventName);
        tilDate = findViewById(R.id.tilDate);
        etDate = findViewById(R.id.etDate);
        tilTime = findViewById(R.id.tilTime);
        etTime = findViewById(R.id.etTime);
        tilDescription = findViewById(R.id.tilDescription);
        etDescription = findViewById(R.id.etDescription);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        cvProgress = findViewById(R.id.cvProgress);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void checkForEventArgs() {
        EDIT_MODE = getIntent().getExtras() != null;
        if (EDIT_MODE) {
            loadEventDetails();
        }
        else {
            Picasso.get().load("https://place-hold.it/600x400/fffffc/bbbba1?text=No%20Image").fit().into(imgEvent);
        }
    }

    private void loadEventDetails() {
        String eventName = getIntent().getStringExtra("eventName");
        String description = getIntent().getStringExtra("description");
        String bannerFileName = getIntent().getStringExtra("bannerFileName");
        long schedule = getIntent().getLongExtra("schedule", 0);

        // show delete button
        btnDelete.setVisibility(View.VISIBLE);

        // set event name
        etEventName.setText(eventName);

        // set description
        etDescription.setText(description);

        // set date schedule
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yy");
        etDate.setText(sdfDate.format(schedule));
        dpScheduleSelection = schedule;

        // set time schedule
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
        etTime.setText(sdfTime.format(schedule));
        tpScheduleSelection = schedule;

        // set banner
        FirebaseStorage.getInstance().getReference().child("images/"+bannerFileName).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    uriLoaded = uri;
                    uriSelected = uri;
                    Picasso.get().load(uri).resize(600, 400).centerCrop().into(imgEvent);
                })
                .addOnFailureListener(e -> Picasso.get().load("https://place-hold.it/600x400/fffffc/bbbba1?text=No%20Image").fit().into(imgEvent));
    }

    private void initializeDatePicker() {
        CalendarConstraints.DateValidator constraintsDatePickerStart = DateValidatorPointForward.now();
        CalendarConstraints calendarConstraints = new CalendarConstraints.Builder().setValidator(constraintsDatePickerStart).build();

        // start date
        bSchedule = MaterialDatePicker.Builder.datePicker();
        bSchedule.setTitleText("Select Event Date")
                .setCalendarConstraints(calendarConstraints)
                .setSelection(System.currentTimeMillis());
        dpSchedule = bSchedule.build();
        dpSchedule.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
            dpScheduleSelection = dpSchedule.getSelection();
            etDate.setText(sdf.format(dpScheduleSelection));
            etDate.setEnabled(true);
            // DO CODE
        });
        dpSchedule.addOnNegativeButtonClickListener(view -> {
            etDate.setEnabled(true);
        });
        dpSchedule.addOnCancelListener(dialogInterface -> {
            etDate.setEnabled(true);
        });
    }

    private void initializeTimePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        tpSchedule = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Set Event Time")
                .setTheme(R.style.iEco_TimePicker)
                .build();
        tpSchedule.addOnPositiveButtonClickListener(view -> {
            calendar.set(Calendar.HOUR_OF_DAY, tpSchedule.getHour());
            calendar.set(Calendar.MINUTE, tpSchedule.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            tpScheduleSelection = calendar.getTimeInMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            etTime.setText(sdf.format(tpScheduleSelection));
            etTime.setEnabled(true);
        });
        tpSchedule.addOnNegativeButtonClickListener(view -> {
            etTime.setEnabled(true);
        });
        tpSchedule.addOnCancelListener(dialogInterface -> {
            etTime.setEnabled(true);
        });
    }

    private void handleButtonClicks() {
        btnSelectImage.setOnClickListener(view -> {
            selectImageFromDevice();
        });

        etDate.setOnClickListener(view -> {
            etDate.setEnabled(false);

            dpSchedule.show(getSupportFragmentManager(), "SCHEDULE_DATE_PICKER");
        });

        etTime.setOnClickListener(view -> {
            etTime.setEnabled(false);
            tpSchedule.show(getSupportFragmentManager(), "SCHEDULE_TIME_PICKER");
        });

        btnSaveEvent.setOnClickListener(view -> {
            Utils.hideKeyboard(EditEventActivity.this);
            validateInputs();
        });

        btnDelete.setOnClickListener(view -> {
            Utils.hideKeyboard(EditEventActivity.this);
            deleteEvent();
        });
    }

    private void validateInputs() {
        String eventName = String.valueOf(etEventName.getText()).trim();
        String strDate = String.valueOf(etDate.getText()).trim();
        String strTime = String.valueOf(etDate.getText()).trim();
        String description = String.valueOf(etDescription.getText()).trim();

        if (eventName.isEmpty()) {
            tilEventName.setError("Please enter an event name.");
        }
        else {
            tilEventName.setError(null);
        }

        if (strDate.isEmpty()) {
            tilDate.setError("Please select a date for the schedule.");
        }
        else {
            tilDate.setError(null);
        }

        if (strTime.isEmpty()) {
            tilTime.setError("Please select a time for the schedule.");
        }
        else {
            tilTime.setError(null);
        }

        if (description.isEmpty()) {
            tilDescription.setError("Please provide an event description.");
        }
        else {
            tilDescription.setError(null);
        }

        if (eventName.isEmpty() || strDate.isEmpty() || strTime.isEmpty() || description.isEmpty() || dpScheduleSelection == 0) {
            return;
        }

        // merge date and time into one variable
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTimeInMillis(tpScheduleSelection);

        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTimeInMillis(dpScheduleSelection);
        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));

        saveEvent(eventName, description, calendarDate.getTimeInMillis());
    }

    private void saveEvent(String eventName, String description, long schedule) {
        cvProgress.setVisibility(View.VISIBLE);

        DocumentReference refEvent;
        if (!EDIT_MODE) {
            refEvent = DB.collection("events").document();
        }
        else {
            refEvent = DB.collection("events").document(getIntent().getStringExtra("id"));
        }

        boolean A_BANNER_WAS_SELECTED = uriSelected != null;
        boolean BANNER_WAS_REPLACED = uriSelected != uriLoaded;
        String bannerFileName = null;

        if (A_BANNER_WAS_SELECTED) {
            if (BANNER_WAS_REPLACED) {
                bannerFileName = String.valueOf(System.currentTimeMillis());
            }
            else {
                bannerFileName = getIntent().getStringExtra("bannerFileName");
            }
        }

        // build query key from schedule
        SimpleDateFormat sdfQueryKey = new SimpleDateFormat("yyyy-MM-dd");

        Event event = new Event(
                refEvent.getId(),
                eventName,
                description,
                bannerFileName,
                schedule,
                sdfQueryKey.format(schedule)
        );

        StorageReference bannerReference = STORAGE.getReference().child("images/"+bannerFileName);

        if (A_BANNER_WAS_SELECTED && BANNER_WAS_REPLACED) {
            UploadTask uploadTask = bannerReference.putFile(uriSelected);

            uploadTask.addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Failed to upload banner. Please try again.", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                refEvent.set(event)
                        .addOnSuccessListener(unused -> {
                            /*FragmentManager fragmentManager = EditEventActivity.this.getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment manageEventsFragment = new ManageEventsFragment();
                            fragmentTransaction.replace(R.id.fragmentHolder, manageEventsFragment, "MANAGE_EVENTS_FRAGMENT");
                            fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                            fragmentTransaction.commit();*/

                            cvProgress.setVisibility(View.GONE);

                            // -- insert code here

                            Toast.makeText(getApplicationContext(), "Saved event", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                Log.d("DEBUG", "Upload is " + progress + "% done");

                if (progress > 0 && progress < 100) {
                    progressBar.setProgress((int) progress);
                    tvProgress.setText("("+ (int) progress +"%) Uploading event data...");
                }
            });
        }
        else {
            refEvent.set(event)
                    .addOnSuccessListener(unused -> {
                        /*FragmentManager fragmentManager = EditEventActivity.this.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment manageEventsFragment = new ManageEventsFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, manageEventsFragment, "MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.commit();*/

                        cvProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Saved event", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }

    private void deleteEvent() {
        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(EditEventActivity.this);
        dialogDelete.setTitle("Delete Event");
        dialogDelete.setMessage("Do you want to delete the event \""+getIntent().getStringExtra("eventName")+"\"?");
        dialogDelete.setNeutralButton("Cancel", (dialogInterface, i) -> { });
        dialogDelete.setNegativeButton("Delete", (dialogInterface, i) -> {
            DocumentReference refEvent = DB.collection("events").document(getIntent().getStringExtra("id"));
            refEvent.delete().addOnSuccessListener(unused -> {
                        /*FragmentManager fragmentManager = EditEventActivity.this.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment manageEventsFragment = new ManageEventsFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, manageEventsFragment, "MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                        fragmentTransaction.commit();*/

                        Toast.makeText(getApplicationContext(), "Event was successfully deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to delete event. Please try again.", Toast.LENGTH_SHORT).show());
        });
        dialogDelete.show();
    }

    private void selectImageFromDevice() {
        Intent iImageSelect = new Intent();
        iImageSelect.setType("image/*");
        iImageSelect.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(Intent.createChooser(iImageSelect, "Select Event Banner"));

        /*startActivityForResult(Intent.createChooser(iImageSelect, "Select Event Banner"), SELECT_IMAGE_CODE);*/
    }

    private void initializeActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uriRetrieved = Objects.requireNonNull(data).getData();
                        uriSelected = uriRetrieved;

                        Toast.makeText(getApplicationContext(), "Yooo", Toast.LENGTH_SHORT).show();
                        // display selected image
                        Picasso.get().load(uriRetrieved).resize(350,175).centerCrop().into(imgEvent);
                    }
                }
        );
    }

    /*public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_IMAGE_CODE) {
            if (data == null) {
                return;
            }

            Uri uriRetrieved = data.getData();
            uriSelected = uriRetrieved;

            Toast.makeText(getApplicationContext(), "Yooo", Toast.LENGTH_SHORT).show();
            // display selected image
            *//*Picasso.get().load(uriRetrieved).resize(350,175).centerCrop().into(imgEvent);*//*
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/
}