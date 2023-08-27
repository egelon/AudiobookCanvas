package com.nimbusbg.audiobookcanvas.views;

import android.content.Context;
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
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.databinding.TextPreparationFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProcessTextFileViewModel;
import com.nimbusbg.audiobookcanvas.viewmodelfactories.ProcessTextFileViewModelFactory;

public class TextPreparationFragment extends Fragment
{
    TextPreparationFragmentBinding binding;
    Context appActivityContext;
    int projectId;
    String textFileURI;
    ProcessTextFileViewModel processTextFileViewModel;
    final TextBlockAdapter textBlockAdapter = new TextBlockAdapter();
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        Bundle navigateBackBundle = new Bundle();
        navigateBackBundle.putInt("projectID", getArguments().getInt("projectID"));
        navigateBackBundle.putBoolean("isNewProject", false);
        navigateBackBundle.putString("txtFileUri", getArguments().getString("txtFileUri"));
        
        if (getArguments() != null)
        {
            projectId = getArguments().getInt("projectID");
            textFileURI = getArguments().getString("txtFileUri");
        }
        
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */)
        {
            @Override
            public void handleOnBackPressed()
            {
                Navigation.findNavController(getView()).navigate(R.id.actionBackFromTextProcessing, navigateBackBundle);
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
        
        loadTextChunks();
    }
    
    private void loadTextChunks()
    {
        //load all projects into the recycler view
        processTextFileViewModel.getProjectWithTextBlocks().observe(getViewLifecycleOwner(), new Observer<ProjectWithTextBlocks>()
        {
            @Override
            public void onChanged(@Nullable ProjectWithTextBlocks textBlockData)
            {
                if(textBlockData != null)
                {
                    // Update your UI here.
                    toggleLoadingBarVisibility(View.GONE);
                    //update the recycler view
                    //TODO: and here we would start a background worker to query the text blocks in paralel?
    
                    textBlockAdapter.setTextBlocks(textBlockData);
                }
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
    
            processTextFileViewModel.waitForTTS(new TtsInitListener()
            {
                @Override
                public void OnInitSuccess()
                {
                    binding.enqueueBlocksBtn.setVisibility(View.VISIBLE);
                    //binding.retryErrorBlocksBtn.setVisibility(View.VISIBLE);
                }
        
                @Override
                public void OnInitFailure()
                {
                    binding.enqueueBlocksBtn.setVisibility(View.GONE);
                    binding.retryErrorBlocksBtn.setVisibility(View.GONE);
                }
            });
            
        }
        else if(loadingBarVisibility == View.VISIBLE)
        {
            binding.textBlocksRecyclerView.setVisibility(View.GONE);
            binding.stopProcessingBtn.setVisibility(View.GONE);
            binding.enqueueBlocksBtn.setVisibility(View.GONE);
            binding.retryErrorBlocksBtn.setVisibility(View.GONE);
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