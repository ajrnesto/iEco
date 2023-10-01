package com.ieco.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ieco.Activities.EditFaqsActivity;
import com.ieco.Adapters.FaqAdapter;
import com.ieco.Objects.Faq;
import com.ieco.R;

import java.util.ArrayList;

public class ManageFAQsFragment extends Fragment implements FaqAdapter.OnClick {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;
    RecyclerView rvFaqs;
    ExtendedFloatingActionButton btnAddFaq;
    ArrayList<Faq> arrFaqs;
    FaqAdapter faqAdapter;
    FaqAdapter.OnClick onClick = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_faqs, container, false);

        initializeFirebase();
        initializeViews();
        handleButtonClicks();
        loadListOfFaqs();

        return view;
    }

    private void initializeViews() {
        btnAddFaq = view.findViewById(R.id.btnAddFaq);
        rvFaqs = view.findViewById(R.id.rvFaqs);
    }

    private void loadListOfFaqs() {
        arrFaqs = new ArrayList<>();
        rvFaqs.setHasFixedSize(true);
        rvFaqs.setLayoutManager(new LinearLayoutManager(requireContext()));

        DB.collection("faqs")
                /*.addSnapshotListener(new FaqListener<QuerySnapshot>() {
                    @Override
                    public void onFaq(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                })*/
                .get()
                .addOnSuccessListener(value -> {
                    arrFaqs.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Faq faq = doc.toObject(Faq.class);
                        arrFaqs.add(faq);

                        faqAdapter.notifyDataSetChanged();
                    }

                    if (arrFaqs.isEmpty()) {
                        rvFaqs.setVisibility(View.INVISIBLE);
                    }
                    else {
                        rvFaqs.setVisibility(View.VISIBLE);
                    }
                });

        faqAdapter = new FaqAdapter(requireContext(), requireActivity(), arrFaqs, onClick);
        rvFaqs.setAdapter(faqAdapter);
    }

    private void handleButtonClicks() {
        btnAddFaq.setOnClickListener(view -> {
            /*Fragment editFaqFragment = new EditFaqFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHolder, editFaqFragment, "EDIT_FAQ_FRAGMENT");
            fragmentTransaction.addToBackStack("EDIT_FAQ_FRAGMENT");
            fragmentTransaction.commit();*/

            Intent iAddFaq = new Intent(requireActivity(), EditFaqsActivity.class);
            startActivity(iAddFaq);
            requireActivity().overridePendingTransition(R.anim.fade_zoom_enter, R.anim.fade_zoom_pop_enter);
        });
    }

    @Override
    public void onClick(int position) {
        Faq faq = arrFaqs.get(position);

        String id = faq.getId();
        String question = faq.getQuestion();
        String answer = faq.getAnswer();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("question", question);
        args.putString("answer", answer);

        /*Fragment editFaqFragment = new EditFaqFragment();
        editFaqFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, editFaqFragment, "EDIT_FAQ_FRAGMENT");
        fragmentTransaction.addToBackStack("EDIT_FAQ_FRAGMENT");
        fragmentTransaction.commit();*/

        Intent iAddFaq = new Intent(requireActivity(), EditFaqsActivity.class);
        iAddFaq.putExtras(args);
        startActivity(iAddFaq);
        requireActivity().overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}