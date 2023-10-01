package com.ieco.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ieco.Activities.EditEventActivity;
import com.ieco.Adapters.EventAdapter;
import com.ieco.Objects.Event;
import com.ieco.R;

import java.util.ArrayList;

public class ManageEventsFragment extends Fragment implements EventAdapter.OnClick {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;
    ExtendedFloatingActionButton btnAddEvent;
    RecyclerView rvEvents;
    ArrayList<Event> arrEvents;
    EventAdapter eventAdapter;
    EventAdapter.OnClick onClick = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_events, container, false);

        initializeFirebase();
        initializeViews();
        handleButtonClicks();
        loadListOfEvents();

        return view;
    }

    private void initializeViews() {
        btnAddEvent = view.findViewById(R.id.btnAddEvent);
        rvEvents = view.findViewById(R.id.rvEvents);
    }

    private void loadListOfEvents() {
        arrEvents = new ArrayList<>();
        rvEvents.setHasFixedSize(true);
        rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));

        DB.collection("events")
                /*.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                })*/
                .orderBy("schedule", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(value -> {
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
                });

        eventAdapter = new EventAdapter(requireContext(), requireActivity(), arrEvents, onClick);
        rvEvents.setAdapter(eventAdapter);
    }

    private void handleButtonClicks() {
        btnAddEvent.setOnClickListener(view -> {
            // old code...
                /*Fragment editEventFragment = new EditEventFragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit, R.anim.anim_pop_enter, R.anim.anim_pop_exit);
                fragmentTransaction.replace(R.id.fragmentHolder, editEventFragment, "EDIT_EVENT_FRAGMENT");
                fragmentTransaction.addToBackStack("EDIT_EVENT_FRAGMENT");
                fragmentTransaction.commit();*/
            // ...replaced with this new code
            Intent iAddEvent = new Intent(requireActivity(), EditEventActivity.class);
            startActivity(iAddEvent);
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

        // old code...
            /*Fragment editEventFragment = new EditEventFragment();
            editEventFragment.setArguments(args);
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit, R.anim.anim_pop_enter, R.anim.anim_pop_exit);
            fragmentTransaction.replace(R.id.fragmentHolder, editEventFragment, "EDIT_EVENT_FRAGMENT");
            fragmentTransaction.addToBackStack("EDIT_EVENT_FRAGMENT");
            fragmentTransaction.commit();*/
        // ...replaced with this new code
        Intent iEditEvent = new Intent(requireActivity(), EditEventActivity.class);
        iEditEvent.putExtras(args);
        startActivity(iEditEvent);
        requireActivity().overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}