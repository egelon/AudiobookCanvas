package com.nimbusbg.audiobookcanvas.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.repository.DeletedItemListener;
import com.nimbusbg.audiobookcanvas.databinding.ProjectSetupFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import java.text.DateFormat;
import java.util.Date;

public class ProjectSetupFragment extends Fragment
{
    private ProjectSetupFragmentBinding binding;
    Context appActivityContext;
    
    String lastProjName;
    String lastBookName;
    String lastAuthorName;
    String lastDescriptionText;
    
    private ProjectWithMetadataViewModel projectWithMetadataViewModel;
    
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
    
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.actionBackToProjectList);
            }
        });
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
            updateCurrentProject();
            return true;
        }
        else if (optionItemID == R.id.action_delete_project)
        {
            deleteProject();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void deleteProject()
    {
        int projectID = getArguments().getInt("projectID");
        projectWithMetadataViewModel.deleteProjectWithMetadataById(projectID,
                () -> requireActivity().runOnUiThread(
                        () -> Navigation.findNavController(getView()).navigate(R.id.actionBackToProjectList)));
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        appActivityContext = this.getActivity();
        
        projectWithMetadataViewModel = new ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.nav_graph)).get(ProjectWithMetadataViewModel.class);
        
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
    
        binding.openAudiobookBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onOpenAudiobookClicked(view);
            }
        });
        
        loadSelectedProject();
        lastProjName = binding.projName.getText().toString();
        lastBookName = binding.bookName.getText().toString();
        lastAuthorName = binding.authorName.getText().toString();
        lastDescriptionText = binding.descriptionText.getText().toString();
    }
    
    private void updateCurrentProject()
    {
        if(lastProjName != binding.projName.getText().toString() ||
        lastAuthorName != binding.authorName.getText().toString() ||
        lastBookName != binding.bookName.getText().toString() ||
        lastDescriptionText != binding.descriptionText.getText().toString())
        {
            int projectID = getArguments().getInt("projectID");
            projectWithMetadataViewModel.updateProjectWithMetadata(projectID,
                    binding.projName.getText().toString(),
                    binding.bookName.getText().toString(),
                    binding.authorName.getText().toString(),
                    binding.descriptionText.getText().toString());
        }
    }
    
    private void loadSelectedProject()
    {
        int projectID = getArguments().getInt("projectID");
        projectWithMetadataViewModel.getProjectWithMetadataById(projectID).observe(getViewLifecycleOwner(), new Observer<ProjectWithMetadata>()
        {
            @Override
            public void onChanged(@Nullable ProjectWithMetadata projectData)
            {
                if(projectData != null)
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
    
                    if (projectData.project.getCompleted() || (lastBlockID == 0))
                    {
                        binding.percentCompletedLabel.setVisibility(View.VISIBLE);
                        binding.percentCompleted.setVisibility(View.VISIBLE);
                    } else
                    {
                        binding.percentCompletedLabel.setVisibility(View.GONE);
                        binding.percentCompleted.setVisibility(View.GONE);
                    }
    
                    if (lastBlockID == 0)
                    {
                        binding.percentCompleted.setText("0%");
                        binding.processTxtBlockBtn.setVisibility(View.VISIBLE);
                        binding.processTxtBlockBtn.setText(R.string.start_processing_btn_label);
                        binding.openAudiobookBtn.setVisibility(View.GONE);
                    } else if (projectData.project.getCompleted())
                    {
                        binding.percentCompleted.setText("100%");
                        binding.processTxtBlockBtn.setVisibility(View.GONE);
                        binding.openAudiobookBtn.setVisibility(View.VISIBLE);
                    } else
                    {
                        binding.processTxtBlockBtn.setVisibility(View.VISIBLE);
                        binding.processTxtBlockBtn.setText(R.string.continue_processing_btn_label);
                        binding.openAudiobookBtn.setVisibility(View.GONE);
                    }
    
                    Date lastModifiedDate = projectData.project.getLastModified();
                    binding.lastEditedOn.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(lastModifiedDate));
    
                    Date createdOnDate = projectData.project.getCreatedOn();
                    binding.createdOn.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(createdOnDate));
    
                    binding.appVersion.setText(projectData.appInfo.getAppVersion());
                    binding.osVersion.setText(projectData.appInfo.getOsVersion());
                    binding.deviceName.setText(projectData.appInfo.getDeviceType());
                }
            }
        });
    }
    
    public void onExportAsXMLClicked(View view)
    {
        Toast.makeText(this.getActivity(), "Export as XML", Toast.LENGTH_LONG).show();
    }
    
    private Bundle MakeBundleForTextProcessing()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("projectID", getArguments().getInt("projectID"));
        bundle.putBoolean("isNewProject", false);
        bundle.putString("txtFileUri", binding.textFilePath.getText().toString());
        return bundle;
    }
    
    public void onProcessChunkClicked(View view)
    {
        Navigation.findNavController(getView()).navigate(R.id.actionContinueProcessing, MakeBundleForTextProcessing());
    }
    
    public void onOpenAudiobookClicked(View view)
    {
        Toast.makeText(this.getActivity(), "Open Audiobook", Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}