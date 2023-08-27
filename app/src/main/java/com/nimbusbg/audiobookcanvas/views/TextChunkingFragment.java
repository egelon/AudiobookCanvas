package com.nimbusbg.audiobookcanvas.views;

import android.content.Context;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.listeners.DeletedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.databinding.FragmentTextChunkingBinding;
import com.nimbusbg.audiobookcanvas.viewmodelfactories.TextChunkingViewModelFactory;
import com.nimbusbg.audiobookcanvas.viewmodels.TextChunkingViewModel;
import java.util.ArrayList;
import java.util.List;

public class TextChunkingFragment extends Fragment
{
    FragmentTextChunkingBinding binding;
    int projectId;
    String textFileURI;
    
    
    private TextChunkingViewModel textChunkingViewModel;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentTextChunkingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    
        if (savedInstanceState != null)
        {
            projectId = savedInstanceState.getInt("projectID");
            textFileURI = savedInstanceState.getString("txtFileUri");
        }
        else if (getArguments() != null)
        {
            projectId = getArguments().getInt("projectID");
            textFileURI = getArguments().getString("txtFileUri");
        }
    
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */)
        {
            @Override
            public void handleOnBackPressed()
            {
                textChunkingViewModel.cleanupProject(() -> requireActivity().runOnUiThread(
                        () -> Navigation.findNavController(getView()).navigate(R.id.actionStopTextChunking)));
            }
        });
    
        textChunkingViewModel = new ViewModelProvider(this, new TextChunkingViewModelFactory(requireActivity().getApplication(), projectId, textFileURI)).get(TextChunkingViewModel.class);
        //TODO: we need some good way of detecting this, and this needs to be stored with the project settings
        textChunkingViewModel.setDialogueStartEndChar('“', '”');
        
        binding.loadingChunksText.setText(R.string.loading_text_loading_file);
        //load chunks into the database
        textChunkingViewModel.chunkInputFile(new FileOperationListener()
        {
            @Override
            public void OnFileLoaded(String data)
            {
                Log.d("TextChunkingFragment", "OnFileLoaded");
            }
    
            @Override
            public void OnFileChunked(ArrayList<String> chunks)
            {
                textChunkingViewModel.insertTextBlocks(chunks, new InsertedItemListener()
                {
                    @Override
                    public void onInsert(int itemId)
                    {
                        textChunkingViewModel.setAreTextBlocksInserted(true);
                    }
                });
            }
    
            @Override
            public void OnChunkingStopped()
            {
                Log.d("TextChunkingFragment", "textChunkingViewModel.chunkInputFile OnChunkingStopped");
            }
        });
    
        textChunkingViewModel.getTextBlocksByProjectId(projectId).observe(getViewLifecycleOwner(), new Observer<List<TextBlock>>()
        {
            @Override
            public void onChanged(List<TextBlock> textBlocks)
            {
                if(textBlocks != null && !textBlocks.isEmpty() && textChunkingViewModel.areTextBlocksInserted())
                {
                    
                        //navigate to the text processing fragment
                        Bundle bundle = new Bundle();
                        bundle.putInt("projectID", projectId);
                        bundle.putBoolean("isNewProject", false);
                        bundle.putString("txtFileUri", textFileURI);
                        Navigation.findNavController(getView()).navigate(R.id.actionTextChunked, bundle);
                    
                }
            }
        });
        
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("projectID", projectId);
        outState.putString("txtFileUri", textFileURI);
    }
    
    
    
    
    
    
    
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
    
    @Override
    public void onStop()
    {
        super.onStop();
    }
}