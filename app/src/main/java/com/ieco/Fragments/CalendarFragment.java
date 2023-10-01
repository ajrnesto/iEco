package com.ieco.Fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ieco.Adapters.EventAdapter;
import com.ieco.Objects.Event;
import com.ieco.R;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarFragment extends Fragment { //implements EventAdapter.OnClick {
    /*static LocalDate selectedDate = null;

    public static class DayViewContainer extends ViewContainer {
        private final TextView textView;
        private final ConstraintLayout constraintLayout;
        private final AppCompatImageView bullet;
        private CalendarDay day;

        public DayViewContainer(@NonNull View view) {
            super(view);

            textView = view.findViewById(R.id.calendarDayText);
            constraintLayout = view.findViewById(R.id.constraintLayout);
            bullet = view.findViewById(R.id.bullet);

            if (day != null) {
                Log.d("DEBUG", "DAY: "+day.getDate());
            }

            view.setOnClickListener(v -> {
                if (day.getPosition() == DayPosition.MonthDate) { // if user clicks any day of the current month
                    LocalDate currentSelection = selectedDate;
                    cvEvent.setVisibility(View.GONE);

                    // if user selects new date
                    if (currentSelection != day.getDate()) {
                        // if the user clicks a different date
                        selectedDate = day.getDate();
                        calendarView.notifyDateChanged(day.getDate());
                        renderSelectedDateEvent(day.getDate());

                        if (currentSelection != null) {
                            calendarView.notifyDateChanged(currentSelection);
                        }
                    }
                }
            });
        }

        public TextView getTextView() {
            return textView;
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public AppCompatImageView getBullet() {
            return bullet;
        }

        public void setDay(CalendarDay day) {
            this.day = day;
        }

        public CalendarDay getDay() {
            return day;
        }
    }

    public static class MonthViewContainer extends ViewContainer {
        public MonthViewContainer(@NonNull View view) {
            super(view);
        }
    }

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;
    TextView tvMonthYear, tvSelectedDate;
    static CalendarView calendarView;
    MaterialButton btnPrevious, btnNext;
    static RecyclerView rvEvents;

    MaterialCardView cvEvent;
    static ArrayList<Event> arrEvents;
    static EventAdapter eventAdapter;
    EventAdapter.OnClick onClick = this;

    int monthsNavigator = 0;

    @Override
    public void onResume() {
        super.onResume();

        selectedDate = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initializeFirebase();
        initializeViews();
        renderCalendarDays();
        renderCalendarHeader();
        listenForCalendarScroll();
        handleButtonClicks();

        return view;
    }

    private void initializeViews() {
        calendarView = view.findViewById(R.id.calendarView);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        rvEvents = view.findViewById(R.id.rvEvents);
        cvEvent = view.findViewById(R.id.cvEvent);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
    }

    private void renderSelectedDateEvent(LocalDate date) {
        arrEvents = new ArrayList<>();
        rvEvents.setHasFixedSize(true);
        rvEvents.setLayoutManager(new LinearLayoutManager(calendarView.getContext()));

        FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("queryKey", String.valueOf(date))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {
                        arrEvents.clear();

                        for (QueryDocumentSnapshot snapshot : snapshots) {
                            Event event = snapshot.toObject(Event.class);
                            arrEvents.add(event);

                            eventAdapter.notifyDataSetChanged();
                        }

                        if (arrEvents.isEmpty()) {
                            cvEvent.setVisibility(View.INVISIBLE);
                        }
                        else {
                            cvEvent.setVisibility(View.VISIBLE);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                            tvSelectedDate.setText(""+date.format(formatter));
                        }
                    }
                })
                *//*.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        arrEvents.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            Event event = doc.toObject(Event.class);
                            arrEvents.add(event);

                            eventAdapter.notifyDataSetChanged();
                        }

                        if (arrEvents.isEmpty()) {
                            cvEvent.setVisibility(View.INVISIBLE);
                        }
                        else {
                            cvEvent.setVisibility(View.VISIBLE);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                            tvSelectedDate.setText(""+date.format(formatter));
                        }
                    }
                })*//*;

        eventAdapter = new EventAdapter(rvEvents.getContext(), (Activity) rvEvents.getContext(), arrEvents, onClick);
        rvEvents.setAdapter(eventAdapter);
    }

    private void renderCalendarDays() {
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                container.setDay(calendarDay);

                ConstraintLayout constraintLayout = container.getConstraintLayout();
                AppCompatImageView bullet = container.getBullet();
                TextView textView = container.getTextView();
                textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    textView.setVisibility(View.VISIBLE);
                    if (calendarDay.getDate().equals(selectedDate)) {
                        textView.setTextColor(getResources().getColor(R.color.white));
                        constraintLayout.setBackgroundResource(R.drawable.selection_background);
                    }
                    else if (calendarDay.getDate().equals(LocalDate.now())) {
                        textView.setTextColor(Color.BLACK);
                        constraintLayout.setBackgroundResource(R.drawable.today_background);
                    }
                    else {
                        textView.setTextColor(Color.BLACK);
                        constraintLayout.setBackground(null);
                    }

                    DB.collection("events")
                            .whereEqualTo("queryKey", String.valueOf(calendarDay.getDate()))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot snapshots) {
                                    boolean EVENT_EXISTS = !snapshots.isEmpty();
                                    if (EVENT_EXISTS) {
                                        bullet.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        bullet.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void renderCalendarHeader() {
        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<ViewContainer>() {
            @NonNull
            @Override
            public ViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull ViewContainer container, CalendarMonth calendarMonth) {
                LinearLayout calendarDayTitlesContainer = container.getView().findViewById(R.id.calendarDayTitlesContainer);

                if (calendarDayTitlesContainer.getTag() == null) {
                    calendarDayTitlesContainer.setTag(calendarMonth.getYearMonth());

                    for (int i = 0; i < calendarDayTitlesContainer.getChildCount(); i++) {
                        TextView textView = (TextView) calendarDayTitlesContainer.getChildAt(i);
                        DayOfWeek dayOfWeek = calendarMonth.getWeekDays().get(0).get(i).getDate().getDayOfWeek();
                        String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                        textView.setText(title);
                    }
                }
            }
        });
        calendarView.setup(YearMonth.now().minusMonths(100), YearMonth.now().plusMonths(100), DayOfWeek.SUNDAY);
        calendarView.scrollToMonth(YearMonth.now());
    }

    private void listenForCalendarScroll() {
        calendarView.setMonthScrollListener(calendarMonth -> {
            // get calendar's current month and year
            YearMonth yearMonth = YearMonth.of((int) calendarMonth.getYearMonth().getLong(ChronoField.YEAR), (int) calendarMonth.getYearMonth().getLong(ChronoField.MONTH_OF_YEAR));

            // display current month and year
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            tvMonthYear.setText(""+yearMonth.format(formatter));

            return null;
        });
    }

    private void handleButtonClicks() {
        btnNext.setOnClickListener(view -> {
            monthsNavigator++;
            calendarView.smoothScrollToMonth(YearMonth.now().plusMonths(monthsNavigator));
        });

        btnPrevious.setOnClickListener(view -> {
            monthsNavigator--;
            calendarView.smoothScrollToMonth(YearMonth.now().plusMonths(monthsNavigator));
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

        Fragment viewEventFragment = new ViewEventFragment();
        viewEventFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, viewEventFragment, "VIEW_EVENT_FRAGMENT");
        fragmentTransaction.addToBackStack("VIEW_EVENT_FRAGMENT");
        fragmentTransaction.commit();
    }*/
}