package com.example.mypets;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.mypets.auth.LoginActivity;
import com.example.mypets.databinding.ActivityMainBinding;
import com.example.mypets.ui.Vaccin.VaccineReminderWorker;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;


public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Navigation
        String userRole = getIntent().getStringExtra("USER_ROLE");
        setupNavigation(userRole);

        // Setup Toolbar và Navigation Drawer
        setupToolbarAndDrawer();

        // Xử lý menu theo role
        setupMenuBasedOnRole(userRole);
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.phongKhamFragment
        )
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setupMenuBasedOnRole(String userRole) {
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();

        // Ẩn tất cả group chính
        menu.setGroupVisible(R.id.group_user, false);
        menu.setGroupVisible(R.id.group_clinic, false);

        // Hiển thị group theo role
        if (userRole != null && userRole.equals("clinic")) {
            menu.setGroupVisible(R.id.group_clinic, true);
        } else {
            menu.setGroupVisible(R.id.group_user, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            handleLogout();
            return true;
        }
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finishAffinity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setupNavigation(String userRole) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        if (navHostFragment != null) {
            NavInflater inflater = navHostFragment.getNavController().getNavInflater();
            NavGraph graph;

            if (userRole != null && userRole.equals("clinic")) {
                graph = inflater.inflate(R.navigation.nav_graph_clinic);
                graph.setStartDestination(R.id.phongKhamFragment);
            } else {
                graph = inflater.inflate(R.navigation.mobile_navigation);
                graph.setStartDestination(R.id.nav_home);
            }

            navHostFragment.getNavController().setGraph(graph);
        }
    }
}