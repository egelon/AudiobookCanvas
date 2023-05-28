package com.nimbusbg.audiobookcanvas.views;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nimbusbg.audiobookcanvas.BuildConfig;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.databinding.ProjectListFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectListFragment extends Fragment {

    private ProjectListFragmentBinding binding;
    private ProjectWithMetadataViewModel projectWithMetadataViewModel;
    private FloatingActionButton addProjectFAB;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ProjectListFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.nav_graph);

        // The ViewModel is scoped to the `nav_graph` Navigation graph
        projectWithMetadataViewModel = new ViewModelProvider(backStackEntry).get(ProjectWithMetadataViewModel.class);

        RecyclerView projectRecyclerView = binding.projectsRecyclerView;
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        projectRecyclerView.setHasFixedSize(true);

        final ProjectAdapter projectAdapter = new ProjectAdapter();
        projectRecyclerView.setAdapter(projectAdapter);

        //load all projects into the recycler view
        projectWithMetadataViewModel.getAllProjectsWithMetadata().observe(this,
                new Observer<List<ProjectWithMetadata>>() {
                    @Override
                    public void onChanged(@Nullable List<ProjectWithMetadata> projects) {
                        //update our recycler view
                        projectAdapter.setProjects(projects);
                    }
                });

        addProjectFAB = binding.fabAddProject;
        addProjectFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onAddProjectClicked(view);
            }});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return (manufacturer + " " + model);
        }
    }

    public void onAddProjectClicked(View view) {

        Date currentTime = Calendar.getInstance().getTime();

        AudiobookProject testProject = new AudiobookProject(getString(R.string.xmlProjectFileVersion),
                false,
                0,
                getString(R.string.defaultAudiobookProjName),
                "",
                getString(R.string.defaultAudiobookProjName) + ".xml",
                getString(R.string.defaultAudiobookTitle) + ".mp3",
                currentTime,
                currentTime);
        AppInfo testAppInfo = new AppInfo(0, BuildConfig.VERSION_NAME, getAndroidVersion(), getDeviceName());
        AudiobookData testData = new AudiobookData(0,getString(R.string.defaultAudiobookTitle), "", Locale.getDefault().toLanguageTag(), "");

        projectWithMetadataViewModel.insertProjectWithMetadata(testProject, testAppInfo, testData);
    }
}