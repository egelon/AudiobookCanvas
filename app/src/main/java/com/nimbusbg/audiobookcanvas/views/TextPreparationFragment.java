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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.entities.BlockState;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.repository.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.repository.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedMultipleItemsListener;
import com.nimbusbg.audiobookcanvas.data.repository.TtsListener;
import com.nimbusbg.audiobookcanvas.databinding.TextPreparationFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProcessTextFileViewModel;
import com.nimbusbg.audiobookcanvas.viewmodelfactories.ProcessTextFileViewModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                        Navigation.findNavController(getView()).navigate(R.id.actionProcessedTextBlockSelected, textBlockReviewBundle);
                        //Toast.makeText(requireActivity(), "Block ID " + String.valueOf(textBlock.getId()), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case NOT_REQUESTED:
                    {
                        queryCharacters(textBlock);
                        break;
                    }
                    case WAITING_RESPONSE:
                    case ERROR:
                    default:
                    {
                        //TODO: we should check if we're still waiting for a response here
                        //for now we'll just add another response
                        queryCharacters(textBlock);
                        break;
                    }
                }
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
    
        processTextFileViewModel.initTTS(new TtsListener()
        {
            @Override
            public void OnInitSuccess()
            {
        
            }
    
            @Override
            public void OnInitFailure()
            {
        
            }
    
            @Override
            public void OnUtteranceStart(String s)
            {
        
            }
    
            @Override
            public void OnUtteranceDone(String s)
            {
        
            }
    
            @Override
            public void OnUtteranceError(String s)
            {
        
            }
    
    
        });
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
                textBlockAdapter.setTextBlocks(textBlockData);
                //TODO: and here we would start a background worker to query the text blocks in paralel?
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
    
    private void queryCharacters(TextBlock textBlock)
    {
        processTextFileViewModel.setTextBlockStateById(textBlock.getId(), BlockState.WAITING_RESPONSE);
        //TODO: For test purposes only
        processTextFileViewModel.performNamedEntityRecognition(textBlock.getText(), "TAG_" + String.valueOf(textBlock.getId()), new ApiResponseListener()
        {
            @Override
            public void OnResponse(JSONObject response)
            {
                //TODO: either do something with the fetched character, or move on?
                OnFetchedCharacters(response, textBlock);
                processTextFileViewModel.setTextBlockStateById(textBlock.getId(), BlockState.NOT_REVIEWED);
            }
    
            @Override
            public void OnError(VolleyError error)
            {
                processTextFileViewModel.setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
                OnFetchCharacterError(error.toString());
            }
    
            @Override
            public void OnException(JSONException ex)
            {
                processTextFileViewModel.setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
                OnQueryException(ex.toString());
            }
        });
    }
    
    private void OnFetchedCharacters(JSONObject apiResponse, TextBlock textBlock)
    {
        try
        {
            processTextFileViewModel.storeCharactersForTextBlock(apiResponse, textBlock, new InsertedMultipleItemsListener()
            {
                @Override
                public void onInsert(List<Long> itemIds)
                {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireActivity(), "Added " + String.valueOf(itemIds.size()) + " character lines", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            
        }
        catch (JSONException ex)
        {
            Toast.makeText(requireActivity(), "Exception: " + ex, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void OnFetchCharacterError(String error)
    {
        Toast.makeText(requireActivity(), "Fetch Error: " + error, Toast.LENGTH_SHORT).show();
    }
    
    private void OnQueryException(String exception)
    {
        Toast.makeText(requireActivity(), "Query Exception: " + exception, Toast.LENGTH_SHORT).show();
    }
        
        @Override
    public void onDestroyView()
    {
        processTextFileViewModel.destroyTTS();
        super.onDestroyView();
        binding = null;
    }
}