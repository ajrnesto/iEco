package com.ieco.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.rpc.context.AttributeContext;
import com.ieco.Fragments.AboutUsFragment;
import com.ieco.Fragments.BrowseArticlesFragment;
import com.ieco.Fragments.BrowseFaqsFragment;
import com.ieco.Fragments.CalendarFragment;
import com.ieco.Fragments.EmptyFragment;
import com.ieco.Fragments.HomeFragment;
import com.ieco.Fragments.ManageLearnArticlesFragment;
import com.ieco.Fragments.SchoolInformationFragment;
import com.ieco.Fragments.ViewArticleFragment;
import com.ieco.Fragments.ViewEventFragment;
import com.ieco.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Declare variables for the drawer layout
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navView;
    View headerView;
    MenuItem miHome, miSchoolInformation, miFaq, miContactUs;
    TextView tvTitle, tvUserName, tvUserEmail;
    MaterialButton btnLogout;
    MaterialButton btnMenu;
    AppCompatImageView ivProfile;

    // handle clicks on menu button
    View.OnClickListener clickListenerMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.open();
        }
    };

    // handle clicks on back button
    View.OnClickListener clickListenerBack = view -> onBackPressed();

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onResume() {
        super.onResume();
        startupHomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();
        initializeMenuDrawer();
        startupHomeFragment();
        listenToNavigationChanges();
        listenToButtonClicks();
    }

    private void initializeVariables() {
        tvTitle = findViewById(R.id.tvTitle);
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        headerView = navView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.tvName);
        tvUserEmail = headerView.findViewById(R.id.tvEmail);
        btnLogout = headerView.findViewById(R.id.btnSignOut);
        ivProfile = headerView.findViewById(R.id.ivProfile);
        btnMenu = findViewById(R.id.btnMenu);
        miHome = findViewById(R.id.miHome);
        miSchoolInformation = findViewById(R.id.miSchoolInformation);
        miFaq = findViewById(R.id.miFaq);
    }

    private void startupHomeFragment() {
        // set "home fragment" as the startup fragment
        Fragment shopFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "HOME_FRAGMENT");
        fragmentTransaction.addToBackStack("HOME_FRAGMENT");
        fragmentTransaction.commit();
        navView.setCheckedItem(R.id.miHome);

        btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
        btnMenu.setOnClickListener(view -> {
            drawerLayout.open();
        });

        tvTitle.setText("iEco");
    }

    private void initializeMenuDrawer() {
        // listen to navigation drawer item clicks
        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.miHome) {
                if (Objects.requireNonNull(navView.getCheckedItem()).getItemId() != R.id.miHome) {
                    Fragment shopFragment = new HomeFragment();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, shopFragment, "HOME_FRAGMENT");
                    fragmentTransaction.addToBackStack("HOME_FRAGMENT");
                    fragmentTransaction.commit();
                    drawerLayout.close();
                }
            }
            else if (item.getItemId() == R.id.miSchoolInformation) {
                if (Objects.requireNonNull(navView.getCheckedItem()).getItemId() != R.id.miSchoolInformation) {
                    Fragment schoolInformationFragment = new SchoolInformationFragment();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, schoolInformationFragment, "SCHOOL_INFORMATION_FRAGMENT");
                    fragmentTransaction.addToBackStack("SCHOOL_INFORMATION_FRAGMENT");
                    fragmentTransaction.commit();
                    drawerLayout.close();
                }
            }
            else if (item.getItemId() == R.id.miAboutUs) {
                if (Objects.requireNonNull(navView.getCheckedItem()).getItemId() != R.id.miAboutUs) {
                    Fragment aboutUsFragment = new AboutUsFragment();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, aboutUsFragment, "ABOUT_US_FRAGMENT");
                    fragmentTransaction.addToBackStack("ABOUT_US_FRAGMENT");
                    fragmentTransaction.commit();
                    drawerLayout.close();
                }
            }
            else if (item.getItemId() == R.id.miFaq) {
                if (Objects.requireNonNull(navView.getCheckedItem()).getItemId() != R.id.miFaq) {
                    Fragment browseFaqsFragment = new BrowseFaqsFragment();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHolder, browseFaqsFragment, "BROWSE_FAQS_FRAGMENT");
                    fragmentTransaction.addToBackStack("BROWSE_FAQS_FRAGMENT");
                    fragmentTransaction.commit();
                    drawerLayout.close();
                }
            }
            else if (item.getItemId() == R.id.miLogin) {
                drawerLayout.close();
                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            }
            else {
                drawerLayout.close();
                Toast.makeText(this, "Not added yet", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void listenToNavigationChanges() {
        // this code block will switch the top action button to:
        // - a menu button - when the current fragment is the home fragment
        // - a back button - if otherwise
        // it will also change the title text depending on which page is currently active
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT");
            CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag("CALENDAR_FRAGMENT");
            BrowseArticlesFragment browseArticlesFragment = (BrowseArticlesFragment) getSupportFragmentManager().findFragmentByTag("BROWSE_ARTICLES_FRAGMENT");
            ViewEventFragment viewEventFragment = (ViewEventFragment) getSupportFragmentManager().findFragmentByTag("VIEW_EVENT_FRAGMENT");
            ViewArticleFragment viewArticleFragment = (ViewArticleFragment) getSupportFragmentManager().findFragmentByTag("VIEW_ARTICLE_FRAGMENT");
            SchoolInformationFragment schoolInformationFragment = (SchoolInformationFragment) getSupportFragmentManager().findFragmentByTag("SCHOOL_INFORMATION_FRAGMENT");
            BrowseFaqsFragment browseFaqsFragment = (BrowseFaqsFragment) getSupportFragmentManager().findFragmentByTag("BROWSE_FAQS_FRAGMENT");
            AboutUsFragment aboutUsFragment = (AboutUsFragment) getSupportFragmentManager().findFragmentByTag("ABOUT_US_FRAGMENT");

            // handle selected item on navigation drawer
            if (homeFragment != null && homeFragment.isVisible()) {
                navView.setCheckedItem(R.id.miHome);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
                btnMenu.setOnClickListener(clickListenerMenu);
                tvTitle.setText("iEco");
            }
            else if (schoolInformationFragment != null && schoolInformationFragment.isVisible()) {
                navView.setCheckedItem(R.id.miSchoolInformation);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
                btnMenu.setOnClickListener(clickListenerMenu);
                tvTitle.setText("School Info");
            }
            else if (aboutUsFragment != null && aboutUsFragment.isVisible()) {
                navView.setCheckedItem(R.id.miAboutUs);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
                btnMenu.setOnClickListener(clickListenerMenu);
                tvTitle.setText("About Us");
            }
            else if (browseFaqsFragment != null && browseFaqsFragment.isVisible()) {
                navView.setCheckedItem(R.id.miFaq);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.menu_24));
                btnMenu.setOnClickListener(clickListenerMenu);
                tvTitle.setText("FAQs");
            }
            else if (calendarFragment != null && calendarFragment.isVisible()) {
                navView.setCheckedItem(R.id.miHome);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.angle_left_24));
                btnMenu.setOnClickListener(clickListenerBack);
                tvTitle.setText("iEco Calendar");
            }
            else if (viewEventFragment != null && viewEventFragment.isVisible()) {
                navView.setCheckedItem(R.id.miHome);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.angle_left_24));
                btnMenu.setOnClickListener(clickListenerBack);
                tvTitle.setText("Event Details");
            }
            else if (browseArticlesFragment != null && browseArticlesFragment.isVisible()) {
                navView.setCheckedItem(R.id.miHome);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.angle_left_24));
                btnMenu.setOnClickListener(clickListenerBack);
                tvTitle.setText("Learn How");
            }
            else if (viewArticleFragment != null && viewArticleFragment.isVisible()) {
                navView.setCheckedItem(R.id.miHome);

                btnMenu.setIcon(getResources().getDrawable(R.drawable.angle_left_24));
                btnMenu.setOnClickListener(clickListenerBack);
                tvTitle.setText("Read Article");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        }

        super.onBackPressed();
    }

    private void listenToButtonClicks() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}