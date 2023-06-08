package com.nimbusbg.audiobookcanvas.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        
        inflater.inflate(R.menu.save_project_menu, menu);
        menu.removeItem(R.id.action_delete_project);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // Handle action bar item clicks here.
        int optionItemID = item.getItemId();
        if (optionItemID == R.id.action_save_project)
        {
            isProjectSaved = isProjectSavedSuccessfully();
            return isProjectSaved;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private boolean isFileSelected()
    {
        return !binding.textFilePath.getText().toString().isEmpty();
    }
    
    private void SaveProject()
    {
        projectWithMetadataViewModel.insertNewProject(binding.projName.getText().toString(),
                binding.bookName.getText().toString(),
                binding.authorName.getText().toString(),
                binding.descriptionText.getText().toString(),
                binding.textFilePath.getText().toString(),
                itemId -> requireActivity().runOnUiThread(() -> {
                    projectId = itemId;
                    Toast.makeText(requireActivity(), "Project '" + binding.projName.getText().toString() +"' Saved", Toast.LENGTH_SHORT).show();
                }));
    }
    
    private boolean isProjectSavedSuccessfully()
    {
        //check if there is a text file selected
        if(isFileSelected())
        {
            SaveProject();
            return true;
        }
        else
        {
            Toast.makeText(this.getActivity(), "No text file selected", Toast.LENGTH_SHORT).show();
            return false;
        }
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
        
        projectWithMetadataViewModel = new ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.nav_graph)).get(ProjectWithMetadataViewModel.class);
        
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
        
        loadDefaultEmptyProject();
    }
    
    private void loadDefaultEmptyProject()
    {
        //this is the creation of a brand new project, we need to call insertNewEmptyProject, and fill our fragment with the default data
        //then we need to save it when the user continues with the text file processing
        projectWithMetadataViewModel.createEmptyProject();
        ProjectWithMetadata defaultProject = projectWithMetadataViewModel.getEmptyProject();
        
        binding.projName.setText(defaultProject.project.getProjectName());
        binding.bookName.setText(defaultProject.audiobookData.getBookTitle());
        binding.authorName.setText(defaultProject.audiobookData.getAuthor());
        binding.descriptionText.setText(defaultProject.audiobookData.getDescription());
        binding.textFilePath.setText(defaultProject.project.getInputFilePath());
        binding.processTxtBlockBtn.setText(R.string.start_processing_btn_label);
    
        binding.projectNameLayout.setVisibility(View.GONE);
        binding.bookNameLayout.setVisibility(View.GONE);
        binding.authorLayout.setVisibility(View.GONE);
        binding.descriptionLayout.setVisibility(View.GONE);
        binding.processTxtBlockBtn.setVisibility(View.GONE);
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