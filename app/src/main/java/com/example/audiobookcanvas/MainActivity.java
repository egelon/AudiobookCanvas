package com.example.audiobookcanvas;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.audiobookcanvas.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private FloatingActionButton fabMenu, btnOpen, btnEdit;
    private TextView openLabel, editLabel;
    Animation rotateOpenAnim, rotateCloseAnim, fromBottomAnim, toBottomAnim;
    private boolean isFABMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mainToolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //setup the floating action button menu
        fabMenu = findViewById(R.id.fabMenu);
        btnOpen = findViewById(R.id.btnOpenFile);
        btnEdit = findViewById(R.id.btnEditProject);

        openLabel = findViewById(R.id.label_OpenTxtFile);
        editLabel = findViewById(R.id.label_EditProject);

        rotateOpenAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateCloseAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottomAnim = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottomAnim = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFABMenuClicked();
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Open new text file", Toast.LENGTH_SHORT).show();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Edit project", Toast.LENGTH_SHORT).show();
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

    private void onFABMenuClicked() {
        setVisibility(isFABMenuOpen);
        setAnimation(isFABMenuOpen);
        //toggle the state of the fab menu
        isFABMenuOpen = !isFABMenuOpen;
    }

    private void setAnimation(boolean isMenuClosed) {
        if(isMenuClosed)
        {
            fabMenu.startAnimation(rotateCloseAnim);
            btnOpen.startAnimation(toBottomAnim);
            openLabel.startAnimation(toBottomAnim);
            btnEdit.startAnimation(toBottomAnim);
            editLabel.startAnimation(toBottomAnim);
        }
        else
        {
            fabMenu.startAnimation(rotateOpenAnim);
            btnOpen.startAnimation(fromBottomAnim);
            openLabel.startAnimation(fromBottomAnim);
            btnEdit.startAnimation(fromBottomAnim);
            editLabel.startAnimation(fromBottomAnim);
        }
    }

    private void setVisibility(boolean isMenuClosed) {
        if(isMenuClosed)
        {
            btnOpen.setVisibility(View.INVISIBLE);
            openLabel.setVisibility(View.INVISIBLE);
            btnEdit.setVisibility(View.INVISIBLE);
            editLabel.setVisibility(View.INVISIBLE);
        }
        else
        {
            btnOpen.setVisibility(View.VISIBLE);
            openLabel.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.VISIBLE);
            editLabel.setVisibility(View.VISIBLE);
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
        fabMenu.hide();
        switch (optionItemID) {
            case R.id.action_settings:
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.SettingsFragment);
                return true;
            case R.id.action_about:
                Toast.makeText(getApplicationContext(), "About page", Toast.LENGTH_SHORT).show();
                fabMenu.show(); //TODO: REMOVE ME!!!
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Show the floating action button
        fabMenu.show();
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Show the floating action button when the back button is pressed and the previous fragment is displayed
        super.onBackPressed();
        // Show the floating action button
        fabMenu.show();
    }
}