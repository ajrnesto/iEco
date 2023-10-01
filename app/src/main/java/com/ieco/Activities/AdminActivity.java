package com.ieco.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ieco.Fragments.EditArticleFragment;
import com.ieco.Fragments.EditEventFragment;
import com.ieco.Fragments.EditFaqFragment;
import com.ieco.Fragments.ManageEventsFragment;
import com.ieco.Fragments.ManageFAQsFragment;
import com.ieco.Fragments.ManageLearnArticlesFragment;
import com.ieco.R;
import com.ieco.Utils.Utils;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    // Declare variables for the drawer layout
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navView;
    View headerView;
    MenuItem miHome, miSchoolInformation, miFaq, miContactUs;
    TextView tvTitle, tvName, tvEmail;
    MaterialButton btnSignOut;
    MaterialButton btnMenu;
    AppCompatImageView ivProfile;

    View.OnClickListener clickListenerMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.open();
        }
    };

    View.OnClickListener clickListenerBack = view -> onBackPressed();

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onResume() {
        super.onResume();

        startupFragment();
    }

    // The code block below will run when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initializeFirebase();
        initializeVariables();
        initializeMenuDrawer();
        listenToNavigationChanges();
        listenToButtonClicks();

        btnSignOut.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.VISIBLE);
        tvEmail.setVisibility(View.VISIBLE);
    }

    private void initializeVariables() {
        // assign variable values
        tvTitle = findViewById(R.id.tvTitle);
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        headerView = navView.getHeaderView(0);
        tvName = headerView.findViewById(R.id.tvName);
        tvEmail = headerView.findViewById(R.id.tvEmail);
        btnSignOut = headerView.findViewById(R.id.btnSignOut);
        ivProfile = headerView.findViewById(R.id.ivProfile);
        btnMenu = findViewById(R.id.btnMenu);

        miHome = findViewById(R.id.miHome);
        miSchoolInformation = findViewById(R.id.miSchoolInformation);
        miFaq = findViewById(R.id.miFaq);

        startupFragment();
    }

    private void startupFragment() {
        int EVENTS_TAB = 0,
            ARTICLES_TAB = 1,
            FAQS_TAB = 2;
        // access cache and get last opened tab
        int lastOpenedTab = Utils.Cache.getInt(getApplicationContext(), "last_tab");

        btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
        btnMenu.setOnClickListener(view -> {
            drawerLayout.open();
        });

        if (lastOpenedTab == EVENTS_TAB) {
            // set "manage events fragment" as the startup fragment
            Fragment manageEventsFragment = new ManageEventsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHolder, manageEventsFragment, "MANAGE_EVENTS_FRAGMENT");
            fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
            fragmentTransaction.commit();
            navView.setCheckedItem(R.id.miManageEvents);

            tvTitle.setText("iEco Events");
        }
        else if (lastOpenedTab == ARTICLES_TAB) {
            // set "manage learn articles fragment" as the startup fragment
            Fragment manageLearnArticlesFragment = new ManageLearnArticlesFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHolder, manageLearnArticlesFragment, "MANAGE_LEARN_ARTICLES_FRAGMENT");
            fragmentTransaction.addToBackStack("MANAGE_LEARN_ARTICLES_FRAGMENT");
            fragmentTransaction.commit();
            navView.setCheckedItem(R.id.miManageLearnArticles);

            tvTitle.setText("iEco Articles");
        }
        else if (lastOpenedTab == FAQS_TAB) {
            // set "manage learn articles fragment" as the startup fragment
            Fragment manageFAQsFragment = new ManageFAQsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHolder, manageFAQsFragment, "MANAGE_FAQS_FRAGMENT");
            fragmentTransaction.addToBackStack("MANAGE_FAQS_FRAGMENT");
            fragmentTransaction.commit();
            navView.setCheckedItem(R.id.miManageFaq);

            tvTitle.setText("iEco FAQs");
        }
    }

    private void initializeMenuDrawer() {
        // header
        DB.collection("users").document(USER.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot data = task.getResult();

                        String email = String.valueOf(data.get("email"));
                        String name = String.valueOf(data.get("username"));

                        tvEmail.setText(email);
                        tvName.setText(name);
                    }
                });

        tvEmail.setText(USER.getEmail());
        btnSignOut.setOnClickListener(view -> {
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        });
        // header

        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.miManageEvents) {
                if (Objects.requireNonNull(navView.getCheckedItem()).getItemId() != R.id.miManageEvents) {
                    Utils.Cache.setInt(getApplicationContext(), "last_tab", 0);
                    Fragment manageEventsFragment = new ManageEventsFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, manageEventsFragment, "MANAGE_EVENTS_FRAGMENT");
                    fragmentTransaction.addToBackStack("MANAGE_EVENTS_FRAGMENT");
                    fragmentTransaction.commit();
                    drawerLayout.close();
                }
            }
            else if (item.getItemId() == R.id.miManageLearnArticles) {
                if (Objects.requireNonNull(navView.getCheckedItem()).getItemId() != R.id.miManageLearnArticles) {
                    Utils.Cache.setInt(getApplicationContext(), "last_tab", 1);
                    Fragment manageLearnArticlesFragment = new ManageLearnArticlesFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, manageLearnArticlesFragment, "MANAGE_LEARN_ARTICLES_FRAGMENT");
                    fragmentTransaction.addToBackStack("MANAGE_LEARN_ARTICLES_FRAGMENT");
                    fragmentTransaction.commit();
                    drawerLayout.close();
                }
            }
            else if (item.getItemId() == R.id.miManageFaq) {
                if (Objects.requireNonNull(navView.getCheckedItem()).getItemId() != R.id.miManageFaq) {
                    Utils.Cache.setInt(getApplicationContext(), "last_tab", 2);
                    Fragment manageFAQsFragment = new ManageFAQsFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, manageFAQsFragment, "MANAGE_FAQS_FRAGMENT");
                    fragmentTransaction.addToBackStack("MANAGE_FAQS_FRAGMENT");
                    fragmentTransaction.commit();
                    drawerLayout.close();
                }
            }
            else {
                drawerLayout.close();
                Toast.makeText(this, "Not added yet", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void listenToNavigationChanges() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            ManageEventsFragment manageEventsFragment = (ManageEventsFragment) getSupportFragmentManager().findFragmentByTag("MANAGE_EVENTS_FRAGMENT");
            EditEventFragment editEventFragment = (EditEventFragment) getSupportFragmentManager().findFragmentByTag("EDIT_EVENT_FRAGMENT");
            ManageLearnArticlesFragment manageLearnArticlesFragment = (ManageLearnArticlesFragment) getSupportFragmentManager().findFragmentByTag("MANAGE_LEARN_ARTICLES_FRAGMENT");
            EditArticleFragment editArticleFragment = (EditArticleFragment) getSupportFragmentManager().findFragmentByTag("EDIT_ARTICLE_FRAGMENT");
            ManageFAQsFragment manageFAQsFragment = (ManageFAQsFragment) getSupportFragmentManager().findFragmentByTag("MANAGE_FAQS_FRAGMENT");
            EditFaqFragment editFaqFragment = (EditFaqFragment) getSupportFragmentManager().findFragmentByTag("EDIT_FAQ_FRAGMENT");

            if (manageEventsFragment != null && manageEventsFragment.isVisible()) {
                navView.setCheckedItem(R.id.miManageEvents);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
                btnMenu.setOnClickListener(clickListenerMenu);
                tvTitle.setText("iEco Events");
            }
            else if (editEventFragment != null && editEventFragment.isVisible()) {
                navView.setCheckedItem(R.id.miManageEvents);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.angle_left_24));
                btnMenu.setOnClickListener(clickListenerBack);
                tvTitle.setText("Edit Event");
            }
            else if (manageLearnArticlesFragment != null && manageLearnArticlesFragment.isVisible()) {
                navView.setCheckedItem(R.id.miManageLearnArticles);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
                btnMenu.setOnClickListener(clickListenerMenu);
                tvTitle.setText("iEco Articles");
            }
            else if (editArticleFragment != null && editArticleFragment.isVisible()) {
                navView.setCheckedItem(R.id.miManageLearnArticles);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.angle_left_24));
                btnMenu.setOnClickListener(clickListenerBack);
                tvTitle.setText("Edit Article");
            }
            else if (manageFAQsFragment != null && manageFAQsFragment.isVisible()) {
                navView.setCheckedItem(R.id.miManageFaq);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
                btnMenu.setOnClickListener(clickListenerMenu);
                tvTitle.setText("iEco FAQs");
            }
            else if (editFaqFragment != null && editFaqFragment.isVisible()) {
                navView.setCheckedItem(R.id.miManageFaq);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.angle_left_24));
                btnMenu.setOnClickListener(clickListenerBack);
                tvTitle.setText("Edit FAQ");
            }
            else {
                tvTitle.setText("iEco");
            }
        });
    }

    private void listenToButtonClicks() {
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        }

        super.onBackPressed();
    }
}