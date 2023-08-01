package com.nimbusbg.audiobookcanvas.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.listeners.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.databinding.TextPreparationFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProcessTextFileViewModel;
import com.nimbusbg.audiobookcanvas.viewmodelfactories.ProcessTextFileViewModelFactory;

import java.util.ArrayList;

public class TextPreparationFragment extends Fragment
{
    TextPreparationFragmentBinding binding;
    Context appActivityContext;
    int projectId;
    boolean isNewProject;
    boolean areTextBlocksInserted;
    String textFileURI;
    ProcessTextFileViewModel processTextFileViewModel;
    final TextBlockAdapter textBlockAdapter = new TextBlockAdapter();
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        Bundle navigateBackBundle = new Bundle();
        if (getArguments() != null)
        {
            projectId = getArguments().getInt("projectID");
            isNewProject = getArguments().getBoolean("isNewProject");
            textFileURI = getArguments().getString("txtFileUri");
    
            if(isNewProject)
            {
                //this is used so we can load the project setup fragment as if we've selected this new project from the project list
                navigateBackBundle.putInt("projectID", getArguments().getInt("projectID"));
                navigateBackBundle.putBoolean("isNewProject", false);
                navigateBackBundle.putString("txtFileUri", getArguments().getString("txtFileUri"));
            }
        }
        
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(isNewProject)
                {
                    Navigation.findNavController(getView()).navigate(R.id.actionBackFromNewProjectTextProcessing, navigateBackBundle);
                }
                else
                {
                    Navigation.findNavController(getView()).navigateUp();
                }
            }
        });
        
        
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
    
        processTextFileViewModel = new ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.nav_graph), new ProcessTextFileViewModelFactory(requireActivity().getApplication(), projectId)).get(ProcessTextFileViewModel.class);
        
        //TODO: we need some good way of detecting this, and this needs to be stored with the project settings
        processTextFileViewModel.setDialogueStartChar('“');
        processTextFileViewModel.setDialogueEndChar('”');
    
        
        
        
        Uri fileUri = Uri.parse(textFileURI);
    
        areTextBlocksInserted = false;
        
        toggleLoadingBarVisibility(View.VISIBLE);
    
        RecyclerView textBlocksRecyclerView = binding.textBlocksRecyclerView;
        textBlocksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        textBlocksRecyclerView.setHasFixedSize(true);
    
        textBlocksRecyclerView.setAdapter(textBlockAdapter);
    
        textBlockAdapter.setOnTextBlockClickListener(new TextBlockAdapter.OnTextBlockClickListener()
        {
            @Override
            public void onTextBlockClicked(TextBlock textBlock)
            {
                switch(textBlock.getState())
                {
                    case NOT_REVIEWED:
                    case REVIEWED:
                    {
                        Bundle textBlockReviewBundle = new Bundle();
                        textBlockReviewBundle.putInt("textblockID", textBlock.getId());
                        textBlockReviewBundle.putInt("projectID", projectId);
                        Navigation.findNavController(getView()).navigate(R.id.actionProcessedTextBlockSelected, textBlockReviewBundle);
                        break;
                    }
                    case NOT_REQUESTED:
                    case WAITING_RESPONSE:
                    case ERROR:
                    default:
                    {
                        break;
                    }
                }
            }
        });
    
        binding.stopProcessingBtn.setVisibility(View.GONE);
        binding.enqueueBlocksBtn.setVisibility(View.GONE);
        binding.retryErrorBlocksBtn.setVisibility(View.GONE);
    
        processTextFileViewModel.waitForTTS(new TtsInitListener()
        {
            @Override
            public void OnInitSuccess()
            {
                binding.enqueueBlocksBtn.setVisibility(View.VISIBLE);
                binding.retryErrorBlocksBtn.setVisibility(View.VISIBLE);
            }
        
            @Override
            public void OnInitFailure()
            {
                binding.enqueueBlocksBtn.setVisibility(View.GONE);
                binding.retryErrorBlocksBtn.setVisibility(View.GONE);
            }
        });
        
        binding.enqueueBlocksBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                
                    processTextFileViewModel.fetchCharactersForUnprocessedTextBlocks();
                    binding.enqueueBlocksBtn.setVisibility(View.GONE);
                
                binding.stopProcessingBtn.setVisibility(View.VISIBLE);
            }
        });
        
        binding.retryErrorBlocksBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                
                    processTextFileViewModel.retryErrorTextBlocks();
                    binding.retryErrorBlocksBtn.setVisibility(View.GONE);
                
                binding.stopProcessingBtn.setVisibility(View.VISIBLE);
            }
        });
    
        binding.stopProcessingBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            
                processTextFileViewModel.stopCharacterRequests();
            
                binding.stopProcessingBtn.setVisibility(View.GONE);
                binding.enqueueBlocksBtn.setText(R.string.continue_processing_btn_label);
                binding.enqueueBlocksBtn.setVisibility(View.VISIBLE);
                binding.retryErrorBlocksBtn.setVisibility(View.VISIBLE);
            }
        });
    
        if(isNewProject)
        {
            binding.loadingText.setText(R.string.loading_text_loading_file);
            //load chunks into the database
            processTextFileViewModel.chunkInputFile(fileUri, new FileOperationListener()
            {
                @Override
                public void OnFileLoaded(String data)
                {
                    requireActivity().runOnUiThread(() -> {
                        binding.loadingText.setText(R.string.loading_text_splitting_file);
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
                            requireActivity().runOnUiThread(() -> {
                                areTextBlocksInserted = true;
                                //load the newly added chunks from the database
                                loadTextChunks();
                            });
                        }
                    });
                }
            });
        }
        else
        {
            //load the chunks from the database
            loadTextChunks();
        }
    }
    
    private void loadTextChunks()
    {
        //load all projects into the recycler view
        binding.loadingText.setText(R.string.loading_text_saved_loading_from_database);
        processTextFileViewModel.getProjectWithTextBlocks().observe(getViewLifecycleOwner(), new Observer<ProjectWithTextBlocks>()
        {
            @Override
            public void onChanged(@Nullable ProjectWithTextBlocks textBlockData)
            {
                // Update your UI here.
                if(isNewProject)
                {
                    if(areTextBlocksInserted && (binding.progressBar.getVisibility() == View.VISIBLE))
                    {
                        toggleLoadingBarVisibility(View.GONE);
                    }
                }
                else
                {
                    toggleLoadingBarVisibility(View.GONE);
                }
                //update the recycler view
                //TODO: and here we would start a background worker to query the text blocks in paralel?
                
                textBlockAdapter.setTextBlocks(textBlockData);
            }
        });
    }
    
    private void toggleLoadingBarVisibility(int loadingBarVisibility)
    {
        binding.progressBar.setVisibility(loadingBarVisibility);
        binding.loadingText.setVisibility(loadingBarVisibility);
        if(loadingBarVisibility == View.GONE)
        {
            binding.textBlocksRecyclerView.setVisibility(View.VISIBLE);
        }
        else if(loadingBarVisibility == View.VISIBLE)
        {
            binding.textBlocksRecyclerView.setVisibility(View.GONE);
        }
    }
        
        @Override
    public void onDestroyView()
    {
        processTextFileViewModel.stopCharacterRequests();
        processTextFileViewModel.destroyTTS();
        super.onDestroyView();
        binding = null;
    }
}