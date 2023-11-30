package com.nimbusbg.audiobookcanvas.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.databinding.NewProjectFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

public class NewProjectFragment extends Fragment
{
    private NewProjectFragmentBinding binding;
    Context appActivityContext;
    boolean isProjectSaved;
    int projectId;
    
    private ProjectWithMetadataViewModel projectWithMetadataViewModel;
    private ActivityResultLauncher<String> getUriActivity;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        getUriActivity = registerForActivityResult(
                new ActivityResultContract<String, Uri>() {
                    @NonNull
                    @Override
                    public Intent createIntent(@NonNull Context context, String input)
                    {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType(input);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        return intent;
                    }
                
                    @Override
                    public Uri parseResult(int resultCode, @Nullable Intent intent)
                    {
                        if (intent == null || resultCode != Activity.RESULT_OK)
                        {
                            return null;
                        }
                        return intent.getData();
                    }
                }, new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri)
                    {
                        HandleUIOnTextFileSelection(uri);
                    }
                });
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = NewProjectFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        appActivityContext = this.getActivity();
        isProjectSaved = false;
        
        projectWithMetadataViewModel = new ViewModelProvider(this).get(ProjectWithMetadataViewModel.class);
        
        binding.textFileBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onSelectTxtFileClicked(view);
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
    
        binding.projName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing needed here
            }
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing needed here
            }
    
            @Override
            public void afterTextChanged(Editable s) {
                // Update the ViewModel when the text changes
                projectWithMetadataViewModel.setProjectName(s.toString());
            }
        });
    
        binding.bookName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing needed here
            }
        
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing needed here
            }
        
            @Override
            public void afterTextChanged(Editable s) {
                // Update the ViewModel when the text changes
                projectWithMetadataViewModel.setBookName(s.toString());
            }
        });
    
        binding.authorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing needed here
            }
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing needed here
            }
    
            @Override
            public void afterTextChanged(Editable s) {
                // Update the ViewModel when the text changes
                projectWithMetadataViewModel.setAuthorName(s.toString());
            }
        });
    
        binding.descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing needed here
            }
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing needed here
            }
    
            @Override
            public void afterTextChanged(Editable s) {
                // Update the ViewModel when the text changes
                projectWithMetadataViewModel.setDescriptionText(s.toString());
            }
        });
        
        setProjectData();
    }
    
    private void setProjectData()
    {
        //this is the creation of a brand new project, we need to call insertNewEmptyProject, and fill our fragment with the default data
        //then we need to save it when the user continues with the text file processing
        ProjectWithMetadata newProject = projectWithMetadataViewModel.getNewProject();
        
        binding.projName.setText(newProject.project.getProjectName());
        binding.bookName.setText(newProject.audiobookData.getBookTitle());
        binding.authorName.setText(newProject.audiobookData.getAuthor());
        binding.descriptionText.setText(newProject.audiobookData.getDescription());
        
        if(!newProject.project.getInputFilePath().isEmpty())
        {
            binding.textFilePath.setText(newProject.project.getInputFilePath());
            binding.processTxtBlockBtn.setText(R.string.start_processing_btn_label);
            binding.textFileBtn.setText(R.string.change_txt_file_btn_label);
            binding.projectNameLayout.setVisibility(View.VISIBLE);
            binding.bookNameLayout.setVisibility(View.VISIBLE);
            binding.authorLayout.setVisibility(View.VISIBLE);
            binding.descriptionLayout.setVisibility(View.VISIBLE);
            binding.processTxtBlockBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.projectNameLayout.setVisibility(View.GONE);
            binding.bookNameLayout.setVisibility(View.GONE);
            binding.authorLayout.setVisibility(View.GONE);
            binding.descriptionLayout.setVisibility(View.GONE);
            binding.processTxtBlockBtn.setVisibility(View.GONE);
        }
    }
    
    public void onSelectTxtFileClicked(View view)
    {
        getUriActivity.launch("text/*");
    }
    
    private void HandleUIOnTextFileSelection(Uri uri)
    {
        if (uri != null)
        {
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            requireContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
    
            binding.textFilePath.setText(uri.toString());
            projectWithMetadataViewModel.setSelectedFileUri(uri.toString());
            
            binding.textFileBtn.setText(R.string.change_txt_file_btn_label);
            binding.projectNameLayout.setVisibility(View.VISIBLE);
            binding.bookNameLayout.setVisibility(View.VISIBLE);
            binding.authorLayout.setVisibility(View.VISIBLE);
            binding.descriptionLayout.setVisibility(View.VISIBLE);
            binding.processTxtBlockBtn.setVisibility(View.VISIBLE);
        }
    }
    
    private Bundle MakeBundleForTextProcessing()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("projectID", projectId);
        bundle.putBoolean("isNewProject", true);
        bundle.putString("txtFileUri", binding.textFilePath.getText().toString());
        return bundle;
    }
    
    public void onProcessChunkClicked(View view)
    {
        if(!isProjectSaved)
        {
            projectWithMetadataViewModel.insertNewProject(
                binding.projName.getText().toString(),
                binding.bookName.getText().toString(),
                binding.authorName.getText().toString(),
                binding.descriptionText.getText().toString(),
                binding.textFilePath.getText().toString(),
                    itemId -> requireActivity().runOnUiThread(() -> {
                        projectId = itemId;
                        Toast.makeText(requireActivity(), "Project '" + binding.projName.getText().toString() + "' created", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(getView()).navigate(R.id.actionStartProcessing, MakeBundleForTextProcessing());
                    }));
        }
        else
        {
            Navigation.findNavController(getView()).navigate(R.id.actionStartProcessing, MakeBundleForTextProcessing());
        }
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}