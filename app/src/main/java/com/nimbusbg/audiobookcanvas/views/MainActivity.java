package com.nimbusbg.audiobookcanvas.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.nimbusbg.audiobookcanvas.MyAudiobookCanvasApplication;
import com.nimbusbg.audiobookcanvas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    private FloatingActionButton fabMenu, btnOpen, btnEdit;
    private TextView openLabel, editLabel;
    Animation rotateOpenAnim, rotateCloseAnim, fromBottomAnim, toBottomAnim;
    private boolean isFABMenuOpen = false;
    private String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // Request code for selecting a TXT document.
    private static final int PICK_TXT_FILE = 2;
    ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mainToolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_main_content_area);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    onTextFileSelected(result);
                });

        setupFABMenu();
    }

    private void setupFABMenu()
    {
        //setup the object pointers for the floating action button menu
        fabMenu = findViewById(R.id.fabMenu);
        btnOpen = findViewById(R.id.btnOpenFile);
        btnEdit = findViewById(R.id.btnEditProject);
        openLabel = findViewById(R.id.label_OpenTxtFile);
        editLabel = findViewById(R.id.label_EditProject);

        //setup the animation instances
        rotateOpenAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateCloseAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottomAnim = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottomAnim = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
    }

    public void onFABMenuClicked(View view) {
        setVisibility(isFABMenuOpen);
        setAnimation(isFABMenuOpen);
        //toggle the state of the fab menu
        isFABMenuOpen = !isFABMenuOpen;
    }

    private void onTextFileSelected(ActivityResult result)
    {
        if (result.getResultCode() == Activity.RESULT_OK)
        {

            Intent intent1 = result.getData();
            Uri uri = intent1.getData();
            Toast.makeText(getApplicationContext(), "File: " + uri.getPath().toString(), Toast.LENGTH_SHORT).show();

            String txtFilePath = uri.toString();
            String projFilePath = "newProj.xml";
            WelcomeFragmentDirections.ActionTextFileSelected action = WelcomeFragmentDirections.actionTextFileSelected(txtFilePath, projFilePath);
            Navigation.findNavController(this, R.id.nav_host_main_content_area).navigate(action);
        }
        else
        {
            //someting went wrong or the user decided not to select a file

            //show the FAB menu
            toggleFABMenuVisibility(true);
        }
    }

    public void onOpenTxtFileClicked(View view)
    {
        try
        {
            //start picking a file
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            fileIntent.setType("text/plain");
            filePickerLauncher.launch(fileIntent);

            // Hide the floating action button
            toggleFABMenuVisibility(false);
        }
        catch (Exception ex)
        {
            Log.e("Error", ex.getMessage());
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();

            // Show the floating action button
            toggleFABMenuVisibility(true);
        }
    }

    public void onEditProjectClicked(View view)
    {
        MyAudiobookCanvasApplication appReference = (MyAudiobookCanvasApplication) this.getApplication();
        AudiobookRepository repository = new AudiobookRepository(appReference, appReference.getExecutorService());
        AudiobookProject testInsert = new AudiobookProject("1.0.0",
                false,
                0,
                "testProject",
                "inpt.txt",
                "output.xml",
                "audiobook.mp3",
                new Date(2012, 5, 12),
                new Date(2023, 11, 27));


        //repository.deleteAllProjects();

        repository.insert(testInsert);



        //Toast.makeText(getApplicationContext(), "Added Project ID " + repository.getLastInsertedProjectId() , Toast.LENGTH_SHORT).show();
        /*
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
        intent.setType("text/plain");
        this.startActivity(intent);
         */
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

    private void toggleFABMenuVisibility(boolean isVisible) {
        if(isVisible)
        {
            fabMenu.show();
            if(isFABMenuOpen) {
                fabMenu.callOnClick();
            }
        }
        else
        {
            fabMenu.hide();
            if(isFABMenuOpen) {
                fabMenu.callOnClick();
            }
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
        toggleFABMenuVisibility(false);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int optionItemID = item.getItemId();
        switch (optionItemID) {
            case R.id.action_settings:
                NavController navController = Navigation.findNavController(this, R.id.nav_host_main_content_area);
                navController.navigate(R.id.SettingsFragment);
                return true;
            case R.id.action_about:
                Toast.makeText(getApplicationContext(), "About page", Toast.LENGTH_SHORT).show();
                toggleFABMenuVisibility(true); //TODO: REMOVE ME!!!
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_main_content_area);
        // Show the floating action button
        toggleFABMenuVisibility(true);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Show the floating action button when the back button is pressed and the previous fragment is displayed
        super.onBackPressed();
        // Show the floating action button
        toggleFABMenuVisibility(true);
    }
}