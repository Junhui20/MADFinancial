package com.uccd3223.madfinancial;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class lawMainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private ImageView burgerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure TabLayout and ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Find the 3-dot menu ImageView
        ImageView ivMenu = findViewById(R.id.ivMenu);


        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        burgerMenu = findViewById(R.id.burgerMenu);

        // Burger menu click listener
        burgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle drawer when burger menu is clicked
                if (!drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.openDrawer(navigationView);
                } else {
                    drawerLayout.closeDrawer(navigationView);
                }
            }
        });


        // Navigation item click listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.nav_home) {
                            Toast.makeText(lawMainActivity.this,
                                    "Home Selected", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_profile) {
                            Toast.makeText(lawMainActivity.this,
                                    "Profile Selected", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_share) {
                            Toast.makeText(lawMainActivity.this,
                                    "Share Selected", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_feedback) {
                            Toast.makeText(lawMainActivity.this,
                                    "Feedback Selected", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_logout) {
                            Toast.makeText(lawMainActivity.this,
                                    "Logout Selected", Toast.LENGTH_SHORT).show();
                        }

                        drawerLayout.closeDrawer(navigationView);
                        return true;
                    }
                });


        // Set a click listener for the 3-dot menu
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu and attach it to the ivMenu button
                PopupMenu popupMenu = new PopupMenu(lawMainActivity.this, ivMenu);
                popupMenu.getMenuInflater().inflate(R.menu.drawer_menu, popupMenu.getMenu());

                // Handle menu item clicks using if-else
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.action_account) {
                            // Handle Account menu item click
                            Intent accountIntent = new Intent(lawMainActivity.this, AccountActivity.class);
                            startActivity(accountIntent);
                            return true;

                        } else if (itemId == R.id.action_help) {
                            // Handle Help menu item click
                            Intent helpIntent = new Intent(lawMainActivity.this, HelpActivity.class);
                            startActivity(helpIntent);
                            return true;

                        } else if (itemId == R.id.action_support) {
                            // Handle Support menu item click
                            Intent supportIntent = new Intent(lawMainActivity.this, SupportActivity.class);
                            startActivity(supportIntent);
                            return true;

                        } else {
                            return false; // No action for unrecognized menu items
                        }
                    }
                });

                // Show the popup menu
                popupMenu.show();
            }
        });

        // Attach a FragmentStateAdapter for switching between Accounts and Budgets pages
        viewPager.setAdapter(new

                MainPagerAdapter(this));

        // Link TabLayout to ViewPager2
        new

                TabLayoutMediator(tabLayout, viewPager, (tab, position) ->

        {
            // Set tab titles
            tab.setText(position == 0 ? "ACCOUNTS" : "TRAVEL & GOALS");
        }).

                attach();

        // Handle FloatingActionButton click for adding Income/Expense
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(view ->

        {
            // Open an Add Income/Expense Activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // Example: Handle TabLayout click listener (optional)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Optional
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional
            }
        });


    }


    public class MainPagerAdapter extends FragmentStateAdapter {
        public MainPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new AccountsFragment(); // Accounts tab
            } else {
                return new BudgetsGoalsFragment(); // Budgets & Goals tab
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Number of tabs
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

