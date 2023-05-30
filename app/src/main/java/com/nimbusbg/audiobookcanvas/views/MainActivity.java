package com.nimbusbg.audiobookcanvas.views;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.nimbusbg.audiobookcanvas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.databinding.ActivityMainBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // Request code for selecting a TXT document.
    private static final int PICK_TXT_FILE = 2;

    AppBarConfiguration appBarConfiguration;
    NavController navController;

    public NavController getNavController() {
        return navController;
    }

    Toolbar mainToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up our View Binding, so we can use it to access whatever is in our view, instead of having to call findViewById
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get a navcontroller from our activity's navHost fragment
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //set our activity's main_toolbar to be used as the navigation AppBar
        mainToolbar = binding.mainToolbar;
        setSupportActionBar(mainToolbar);
        //create an AppBar configuration object and set it with the root of the NavGraph
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        // Make sure actions in the ActionBar get propagated to the NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }






    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Show the floating action button when the back button is pressed and the previous fragment is displayed
        super.onBackPressed();
    }
}