package com.ieco.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ieco.Adapters.FaqAdapter;
import com.ieco.Objects.Faq;
import com.ieco.R;

import java.util.ArrayList;

public class BrowseFaqsFragment extends Fragment implements FaqAdapter.OnClick {

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
    ArrayList<Faq> arrFaqs;
    FaqAdapter faqAdapter;
    FaqAdapter.OnClick onClick = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_browse_faqs, container, false);

        initializeFirebase();
        initializeViews();
        loadListOfFaqs();

        return view;
    }

    private void initializeViews() {
        rvFaqs = view.findViewById(R.id.rvFaqs);
    }

    private void loadListOfFaqs() {
        arrFaqs = new ArrayList<>();
        rvFaqs.setHasFixedSize(true);
        rvFaqs.setLayoutManager(new LinearLayoutManager(requireContext()));

        DB.collection("faqs")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
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
                    }
                });

        faqAdapter = new FaqAdapter(requireContext(), requireActivity(), arrFaqs, onClick);
        rvFaqs.setAdapter(faqAdapter);
    }

    @Override
    public void onClick(int position) {
    }
}