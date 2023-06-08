package com.nimbusbg.audiobookcanvas.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.repository.FIleOperationListener;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.databinding.NewProjectFragmentBinding;
import com.nimbusbg.audiobookcanvas.databinding.TextPreparationFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProcessTextFileViewModel;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import java.util.ArrayList;

public class TextPreparationFragment extends Fragment
{
    TextPreparationFragmentBinding binding;
    Context appActivityContext;
    int projectId;
    boolean isNewProject;
    String textFileURI;
    ProcessTextFileViewModel processTextFileViewModel;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            projectId = getArguments().getInt("projectID");
            isNewProject = getArguments().getBoolean("isNewProject");
            textFileURI = getArguments().getString("txtFileUri");
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = TextPreparationFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    
        appActivityContext = this.getActivity();
    
        processTextFileViewModel = new ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.nav_graph)).get(ProcessTextFileViewModel.class);
        setStartAndEndDialogueChar();
        Uri fileUri = Uri.parse(textFileURI);
        binding.progressBar.setVisibility(View.VISIBLE);
    
        if(isNewProject)
        {
            //load chunks into the database
            processTextFileViewModel.chunkInputFile(fileUri, new FIleOperationListener()
            {
                @Override
                public void OnFileLoaded(String data)
                {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireActivity(), "Loaded", Toast.LENGTH_SHORT).show();
                    });
                }
        
                @Override
                public void OnFileChunked(ArrayList<String> chunks)
                {
                    processTextFileViewModel.setTextChunks(chunks);
    
                    processTextFileViewModel.insertTextBlocks(projectId, new InsertedItemListener()
                    {
                        @Override
                        public void onInsert(int itemId)
                        {
                            //TODO: do we even do anything here?
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireActivity(), String.valueOf(processTextFileViewModel.getNumberTextChunks()) + " Text Blocks added", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);
                            });
                        }
                    });
                    
                    
                    
                    
                    
                }
            });
            
            
        }
        
        //load the chunks from the database
        loadTextChunks();
    }
    
    private void loadTextChunks()
    {
        //TODO: finish me
    }
    
    private void setStartAndEndDialogueChar()
    {
        //TODO: we need some good way of detecting this, and this needs to be stored with the project settings
        processTextFileViewModel.setDialogueStartChar('“');
        processTextFileViewModel.setDialogueEndChar('”');
    }
        
        @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}