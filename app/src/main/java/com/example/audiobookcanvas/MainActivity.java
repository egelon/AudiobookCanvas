package com.example.audiobookcanvas;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.audiobookcanvas.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    FloatingActionButton fabMenu, btnOpen, btnEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mainToolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        fabMenu = findViewById(R.id.fabMenu);
        btnOpen = findViewById(R.id.btnOpenFile);
        btnEdit = findViewById(R.id.btnEditProject);

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*
        binding.fastActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_WelcomeFragment_to_SecondFragment);
                // Hide the floating action button
                binding.fastActionBtn.hide();
            }
        });
        */
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item1:
                /*getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, new SecondFragment())
                        .addToBackStack(null)
                        .commit();*/
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_WelcomeFragment_to_SecondFragment);
                // Hide the floating action button
                binding.fastActionBtn.hide();
                return true;

            default:
                return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int optionItemID = item.getItemId();

        switch (optionItemID) {
            case R.id.action_settings:
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.SettingsFragment);
                return true;
            case R.id.action_about:
                //TODO:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Show the floating action button
        binding.fastActionBtn.show();
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Show the floating action button when the back button is pressed and the previous fragment is displayed
        super.onBackPressed();
        // Show the floating action button
        binding.fastActionBtn.show();
    }
}