package com.nimbusbg.audiobookcanvas.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.databinding.ProjectSetupFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import java.text.DateFormat;
import java.util.Date;

public class ProjectSetupFragment extends Fragment
{
    private ProjectSetupFragmentBinding binding;
    Context appActivityContext;
    private Boolean isNewProject;
    
    private ProjectWithMetadataViewModel projectWithMetadataViewModel;
    
    //TODO: try moving this inside the onclick function of the text select button
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>()
            {
                @Override
                public void onActivityResult(Uri uri)
                {
                    updateTextFileURI(uri);
                }
            });
    
    /*
        private ArrayList<String> contentChunks;
        private int maxChunkSize = 1400;
        String openai_completions_endpoint = "https://api.openai.com/v1/completions";
        public static final String requestTag = "NamedEntityRecognitionRequest";
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = ProjectSetupFragmentBinding.inflate(inflater, container, false);
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
        inflater.inflate(R.menu.save_project_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int optionItemID = item.getItemId();
        if (optionItemID == R.id.action_save_project)
        {
            return isProjectSavedSuccessfully();
        }
        return super.onOptionsItemSelected(item);
    }
    
    private boolean isProjectSavedSuccessfully()
    {
        
        //first, check if this is a new project. If it is, insert it into the database
        if(isNewProject)
        {
            //check if there is a text file selected
            if(binding.textFilePath.getText().toString().isEmpty())
            {
                Toast.makeText(this.getActivity(), "No text file selected", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                projectWithMetadataViewModel.insertNewProject(binding.projName.getText().toString(),
                        binding.bookName.getText().toString(),
                        binding.authorName.getText().toString(),
                        binding.descriptionText.getText().toString(),
                        binding.textFilePath.getText().toString());
                Toast.makeText(this.getActivity(), "Project '" + binding.projName.getText().toString() +"' Saved", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            //check if there is a text file selected
            if(binding.textFilePath.getText().toString().isEmpty())
            {
                Toast.makeText(this.getActivity(), "No text file selected", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                Toast.makeText(this.getActivity(), "Project Updated", Toast.LENGTH_SHORT).show();
            }
            
            //the only things we will update are project name, metadata entries
        }
        
        //TODO: finish this function or better yet CHAGE THE VIEWMODEL!!!!!
        //projectWithMetadataViewModel.updateProjectWithMetadata(projectID, projectNameStr, audiobookNameStr, bookNameStr, authorNameStr, projectDescriptionStr);
        
        //navController.navigate(R.id.actionProjectSaved);
        
        return true;
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        appActivityContext = this.getActivity();
        isNewProject = getArguments().getBoolean("isNewProject");

        projectWithMetadataViewModel = new ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.nav_graph)).get(ProjectWithMetadataViewModel.class);
        
        binding.textFileBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onSelectTxtFileClicked(view);
            }
        });
        
        binding.exportAsXMLBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onExportAsXMLClicked(view);
            }
        });
        
        binding.processTxtBlockBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onProcessChunkClicked(view);
            }
        });
        
        if (isNewProject)
        {
            loadDefaultEmptyProject();
        } else
        {
            loadSelectedProject();
        }
        
        
    }
    
    private void loadDefaultEmptyProject()
    {
        //However, if this is the creation of a brand new project, we need to call insertNewEmptyProject, and fill our fragment with the default data
        //then we need to save it when the user continues with the text file processing
        //TODO: move the select file button to a more prominent place! Make it disappear when a file is selected. If we're opening an already created project, then don't show the button at all
        projectWithMetadataViewModel.createEmptyProject();
        ProjectWithMetadata defaultProject = projectWithMetadataViewModel.getEmptyProject();
        
        binding.projName.setText(defaultProject.project.getProjectName());
        binding.projFileVersion.setText(defaultProject.project.getProjectVersion());
        binding.bookName.setText(defaultProject.audiobookData.getBookTitle());
        binding.authorName.setText(defaultProject.audiobookData.getAuthor());
        binding.descriptionText.setText(defaultProject.audiobookData.getDescription());
        binding.textFilePath.setText(defaultProject.project.getInputFilePath());
        int lastBlockID = defaultProject.project.getLastProcessedBlockId();
        binding.lastProcessedBlock.setText(String.valueOf(lastBlockID));
        binding.processTxtBlockBtn.setText(R.string.start_processing_btn_label);
        
        //TODO: I should probably get rid of this here - I have no way of calculating it here, and seems to be a bit pointless as information
        binding.percentCompleted.setText("0%");
       
        Date lastModifiedDate = defaultProject.project.getLastModified();
        binding.lastEditedOn.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(lastModifiedDate));
    
        Date createdOnDate = defaultProject.project.getCreatedOn();
        binding.createdOn.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(createdOnDate));
    
        binding.appVersion.setText(defaultProject.appInfo.getAppVersion());
        binding.osVersion.setText(defaultProject.appInfo.getOsVersion());
        binding.deviceName.setText(defaultProject.appInfo.getDeviceType());
    }
    
    private void loadSelectedProject()
    {
        int projectID = getArguments().getInt("projectID");
        projectWithMetadataViewModel.getProjectWithMetadataById(projectID).observe(getViewLifecycleOwner(), new Observer<ProjectWithMetadata>()
        {
            @Override
            public void onChanged(@Nullable ProjectWithMetadata projectData)
            {
                // Update your UI here.
                
                
                binding.projName.setText(projectData.project.getProjectName());
                binding.projFileVersion.setText(projectData.project.getProjectVersion());
                binding.bookName.setText(projectData.audiobookData.getBookTitle());
                binding.authorName.setText(projectData.audiobookData.getAuthor());
                binding.descriptionText.setText(projectData.audiobookData.getDescription());
                binding.textFilePath.setText(projectData.project.getInputFilePath());
                
                int lastBlockID = projectData.project.getLastProcessedBlockId();
                binding.lastProcessedBlock.setText(String.valueOf(lastBlockID));
                
                if (lastBlockID == 0)
                {
                    binding.processTxtBlockBtn.setText(R.string.start_processing_btn_label);
                } else
                {
                    binding.processTxtBlockBtn.setText(R.string.continue_processing_btn_label);
                }
                
                //TODO: I should probably get rid of this here - I have no way of calculating it here, and seems to be a bit pointless as information
                if (projectData.project.getCompleted())
                {
                    binding.percentCompleted.setText("100%");
                } else
                {
                    calculateBookCompletion(lastBlockID);
                }
                
                Date lastModifiedDate = projectData.project.getLastModified();
                binding.lastEditedOn.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(lastModifiedDate));
                
                Date createdOnDate = projectData.project.getCreatedOn();
                binding.createdOn.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(createdOnDate));
                
                binding.appVersion.setText(projectData.appInfo.getAppVersion());
                binding.osVersion.setText(projectData.appInfo.getOsVersion());
                binding.deviceName.setText(projectData.appInfo.getDeviceType());
            }
        });
    }
    
    //TODO: I'll probably remove this
    private void calculateBookCompletion(int lastBlockID)
    {
        if (lastBlockID == 0)
        {
            binding.percentCompleted.setText("0%");
        } else
        {
            //TODO: get the max chunks and calculate it
            binding.percentCompleted.setText("45%");
        }
        
    }
    
    private void updateTextFileURI(Uri uri)
    {
        // Handle the returned Uri
        if (uri != null)
        {
            binding.textFilePath.setText(uri.toString());
            //viewModel.readFile(uri);
        }
    }
    
    public void onSelectTxtFileClicked(View view)
    {
        mGetContent.launch("text/*");
    }
    
    public void onExportAsXMLClicked(View view)
    {
        Toast.makeText(this.getActivity(), "Export as XML", Toast.LENGTH_LONG).show();
    }
    
    public void onProcessChunkClicked(View view)
    {
        Toast.makeText(this.getActivity(), "Process chunk", Toast.LENGTH_LONG).show();
        
        //TODO: REMOVE!!! FOR TEST ONLY!!!
        int projectID = getArguments().getInt("projectID");
        projectWithMetadataViewModel.deleteProjectWithMetadataById(projectID);
        
        //WARNING!!! There is a bug here! We will now navigate to the previous fragment. In its onViewCreated, we call rojectWithMetadataViewModel.getAllProjectsWithMetadata().observe
        //HOWEVER, this will only observe the PREVIOUS change to our LiveData (the one we did when we pressed the save button!)
        //Initial assumption: This happens, because deleting from the database is asynchronous, and "hasn't happened yet" at the time we call our observe...and we never call observe again
        //upon further testing, it appears this is incorrect. If we observe the database via the App Inspector, we can verify that the project entry is still there after we navigate away.
        //maybe this has something to do with the viewModel instance losing scope, thus releasing the repository reference, which would prevent it from calling the database in the first place
        //at any rate, some weird stuff is happening if we click save AND delete on the same fragment and we immediately navigate up
        NavHostFragment.findNavController(this).navigateUp();
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}