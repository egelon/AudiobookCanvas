package com.nimbusbg.audiobookcanvas.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.databinding.ProjectListFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import java.util.List;

public class ProjectListFragment extends Fragment
{
    
    private ProjectListFragmentBinding binding;
    private ProjectWithMetadataViewModel projectWithMetadataViewModel;
    
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        binding = ProjectListFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    
    //TODO: Can't I use Fragment.findNavController() here?
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int optionItemID = item.getItemId();
        switch (optionItemID)
        {
            case R.id.action_settings:
                //Navigation.findNavController(this.getView()).navigate(R.id.SettingsFragment);
                NavHostFragment.findNavController(this).navigate(R.id.SettingsFragment);
                //((MainActivity)this.getActivity()).getNavController().navigate(R.id.SettingsFragment);
                return true;
            case R.id.action_about:
                NavHostFragment.findNavController(this).navigate(R.id.AboutFragment);
                //((MainActivity)this.getActivity()).getNavController().navigate(R.id.AboutFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        String bundleKey_projID = "projectID";
        String bundleKey_isNewProj = "isNewProject";
        
        //TODO: TEST IF THIS WORKS
        //NavController navController = Fragment.findNavController(this);
        //NavController navController = NavHostFragment.findNavController(this);
        //NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.nav_graph);
        
        //TODO: TRY THE FOLLOWING:

        //projectWithMetadataViewModel = new ViewModelProvider(requireActivity()).get(ProjectWithMetadataViewModel.class);
        
        // The ViewModel is scoped to the `nav_graph` Navigation graph
        projectWithMetadataViewModel = new ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.nav_graph)).get(ProjectWithMetadataViewModel.class);
        
        RecyclerView projectRecyclerView = binding.projectsRecyclerView;
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        projectRecyclerView.setHasFixedSize(true);
        
        final ProjectAdapter projectAdapter = new ProjectAdapter();
        projectRecyclerView.setAdapter(projectAdapter);
    
    
        projectAdapter.setOnProjectClickListener(new ProjectAdapter.OnProjectClickListener()
        {
            @Override
            public void onProjectClicked(ProjectWithMetadata project)
            {
                Bundle bundle = new Bundle();
                bundle.putInt(bundleKey_projID, project.project.getId());
                bundle.putBoolean(bundleKey_isNewProj, false);

                Navigation.findNavController(getView()).navigate(R.id.actionProjectSelected, bundle);
            }
        });
        
        //load all projects into the recycler view
        projectWithMetadataViewModel.getAllProjectsWithMetadata().observe(getViewLifecycleOwner(),
                new Observer<List<ProjectWithMetadata>>()
                {
                    @Override
                    public void onChanged(@Nullable List<ProjectWithMetadata> projects)
                    {
                        //update our recycler view
                        projectAdapter.setProjects(projects);
                    }
                });
        
        binding.fabAddProject.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Navigation.findNavController(getView()).navigate(R.id.actionProjectAdded);
            }
        });
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}