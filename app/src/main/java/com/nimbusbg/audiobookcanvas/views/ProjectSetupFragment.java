package com.nimbusbg.audiobookcanvas.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase_Impl;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.databinding.ProjectSetupFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectSetupFragment extends Fragment{
    private ProjectSetupFragmentBinding binding;
    Context appActivityContext;

    int projectID;
    private ProjectWithMetadataViewModel projectWithMetadataViewModel;

    //TODO: try moving this inside the onclick function of the text select button
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.save_project_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int optionItemID = item.getItemId();
        switch (optionItemID) {
            case R.id.action_save_project:
                return isProjectSavedSuccessfully();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isProjectSavedSuccessfully()
    {
        String projectNameStr = binding.projName.getText().toString();
        String audiobookNameStr = binding.projFileVersion.getText().toString();
        String bookNameStr = binding.bookName.getText().toString();
        String authorNameStr = binding.authorName.getText().toString();
        String projectDescriptionStr = binding.descriptionText.getText().toString();

        String textFileNameStr = binding.textFilePath.getText().toString();

        if(textFileNameStr.trim().isEmpty())
        {
            Toast.makeText(this.getActivity(), "No text file selected", Toast.LENGTH_SHORT).show();
            //return false;
        }


        //TODO: finish this function or better yet CHAGE THE VIEWMODEL!!!!!
        projectWithMetadataViewModel.updateProjectWithMetadata(projectID, projectNameStr, audiobookNameStr, bookNameStr, authorNameStr, projectDescriptionStr);


        Toast.makeText(this.getActivity(), "Project Saved", Toast.LENGTH_SHORT).show();
        //navController.navigate(R.id.actionProjectSaved);

        return true;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        appActivityContext = this.getActivity();

        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.nav_graph);

        // The ViewModel is scoped to the `nav_graph` Navigation graph
        projectWithMetadataViewModel = new ViewModelProvider(backStackEntry).get(projectWithMetadataViewModel.class);


        binding.textFileBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onSelectTxtFileClicked(view);
            }});

        binding.exportAsXMLBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onExportAsXMLClicked(view);
            }});

        binding.processTxtBlockBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onProcessChunkClicked(view);
            }});


        //TODO: We need to check what the action of the nav graph was, to get us to this fragment
        //if we tapped an existing project then we need to call this function:

        //setProjectValues();

        //However, if this is the creation of a brand new project, we need to call insertNewEmptyProject, and fill our fragment with the default data
        //then we need to save it when the user continues with the text file processing

        //TODO: move the select file button to a more prominent place! Make it dissappear when a file is selected. If we're opening an already created project, then don't show the button at all
    }

    private void setProjectValues()
    {
        projectID = getArguments().getInt("projectID");

        projectWithMetadataViewModel.getProjectWithMetadataById(projectID).observe(getViewLifecycleOwner(), new Observer<ProjectWithMetadata>() {
            @Override
            public void onChanged(@Nullable ProjectWithMetadata projectData) {
                // Update your UI here.

                binding.projName.setText(projectData.project.getProjectName());
                binding.projFileVersion.setText(projectData.project.getProjectVersion());
                binding.bookName.setText(projectData.audiobookData.getBookTitle());
                binding.authorName.setText(projectData.audiobookData.getAuthor());
                binding.descriptionText.setText(projectData.audiobookData.getDescription());
                binding.textFilePath.setText(projectData.project.getInputFilePath());

                int lastBlockID = projectData.project.getLastProcessedBlockId();
                binding.lastProcessedBlock.setText(String.valueOf(lastBlockID));

                if(lastBlockID == 0)
                {
                    processChunkBtn.setText(R.string.start_processing_btn_label);
                }
                else
                {
                    processChunkBtn.setText(R.string.continue_processing_btn_label);
                }

                //TODO: I should probably get rid of this here - I have no way of calculating it here, and seems to be a bit pointless as information
                if(projectData.project.getCompleted())
                {
                    binding.percentCompleted.setText("100%");
                }
                else
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
        if(lastBlockID == 0)
        {
            binding.percentCompleted.setText("0%");
        }
        else
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
        Toast.makeText(this.getActivity(),"Export as XML", Toast.LENGTH_LONG).show();
    }

    public void onProcessChunkClicked(View view)
    {
        Toast.makeText(this.getActivity(),"Process chunk", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}