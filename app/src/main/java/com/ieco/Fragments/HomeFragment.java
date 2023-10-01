package com.ieco.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ieco.Activities.BrowseArticlesActivity;
import com.ieco.Activities.CalendarActivity;
import com.ieco.Activities.EditArticleActivity;
import com.ieco.Activities.ViewEventActivity;
import com.ieco.Adapters.EventAdapter;
import com.ieco.Objects.Event;
import com.ieco.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment implements EventAdapter.OnClick {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;
    MaterialButton btnCalendar, btnLearn;
    RecyclerView rvEvents;
    ArrayList<Event> arrEvents;
    EventAdapter eventAdapter;
    EventAdapter.OnClick onClick = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeFirebase();
        initializeViews();
        loadListOfEvents();
        handleButtonClicks();

        return view;
    }

    private void initializeViews() {
        btnCalendar = view.findViewById(R.id.btnCalendar);
        btnLearn = view.findViewById(R.id.btnLearn);
        rvEvents = view.findViewById(R.id.rvEvents);
    }

    private void loadListOfEvents() {
        arrEvents = new ArrayList<>();
        rvEvents.setHasFixedSize(true);
        rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));

        // get current day, at 12 AM, in milliseconds
        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.YEAR, calendarNow.get(Calendar.YEAR));
        calendarToday.set(Calendar.MONTH, calendarNow.get(Calendar.MONTH));
        calendarToday.set(Calendar.DAY_OF_MONTH, calendarNow.get(Calendar.DAY_OF_MONTH));
        calendarToday.set(Calendar.HOUR, 0);
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MILLISECOND, 0);

        DB.collection("events")
                .whereGreaterThanOrEqualTo("schedule", calendarToday.getTimeInMillis())
                .orderBy("schedule", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        arrEvents.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            Event event = doc.toObject(Event.class);
                            arrEvents.add(event);

                            eventAdapter.notifyDataSetChanged();
                        }

                        if (arrEvents.isEmpty()) {
                            rvEvents.setVisibility(View.INVISIBLE);
                        }
                        else {
                            rvEvents.setVisibility(View.VISIBLE);
                        }
                    }
                });

        eventAdapter = new EventAdapter(requireContext(), requireActivity(), arrEvents, onClick);
        rvEvents.setAdapter(eventAdapter);
    }

    private void handleButtonClicks() {
        btnCalendar.setOnClickListener(view -> {
            /*FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            Fragment calendarFragment = new CalendarFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, calendarFragment, "CALENDAR_FRAGMENT");
            fragmentTransaction.addToBackStack("CALENDAR_FRAGMENT");
            fragmentTransaction.commit();*/

            Intent iCalendar = new Intent(requireActivity(), CalendarActivity.class);
            startActivity(iCalendar);
            requireActivity().overridePendingTransition(R.anim.fade_zoom_enter, R.anim.fade_zoom_pop_enter);
        });

        btnLearn.setOnClickListener(view -> {
            /*FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            Fragment browseArticlesFragment = new BrowseArticlesFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, browseArticlesFragment, "BROWSE_ARTICLES_FRAGMENT");
            fragmentTransaction.addToBackStack("BROWSE_ARTICLES_FRAGMENT");
            fragmentTransaction.commit();*/

            Intent iLearn = new Intent(requireActivity(), BrowseArticlesActivity.class);
            startActivity(iLearn);
            requireActivity().overridePendingTransition(R.anim.fade_zoom_enter, R.anim.fade_zoom_pop_enter);
        });
    }

    @Override
    public void onClick(int position) {
        Event event = arrEvents.get(position);

        String id = event.getId();
        String eventName = event.getEventName();
        String description = event.getDescription();
        String bannerFileName = event.getBannerFileName();
        long schedule = event.getSchedule();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("eventName", eventName);
        args.putString("description", description);
        args.putString("bannerFileName", bannerFileName);
        args.putLong("schedule", schedule);

        /*Fragment viewEventFragment = new ViewEventFragment();
        viewEventFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, viewEventFragment, "VIEW_EVENT_FRAGMENT");
        fragmentTransaction.addToBackStack("VIEW_EVENT_FRAGMENT");
        fragmentTransaction.commit();*/

        Intent iViewEvent = new Intent(requireActivity(), ViewEventActivity.class);
        iViewEvent.putExtras(args);
        startActivity(iViewEvent);
        requireActivity().overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}