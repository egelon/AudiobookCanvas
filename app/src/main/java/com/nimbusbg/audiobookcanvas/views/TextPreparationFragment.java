package com.nimbusbg.audiobookcanvas.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.repository.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.repository.FIleOperationListener;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.databinding.NewProjectFragmentBinding;
import com.nimbusbg.audiobookcanvas.databinding.TextPreparationFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodels.ProcessTextFileViewModel;
import com.nimbusbg.audiobookcanvas.viewmodels.ProjectWithMetadataViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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
    
        areTextBlocksInserted = false;
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
                            requireActivity().runOnUiThread(() -> {
                                areTextBlocksInserted = true;
                                Toast.makeText(requireActivity(), String.valueOf(processTextFileViewModel.getNumberTextChunks()) + " Text Blocks added", Toast.LENGTH_SHORT).show();
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
        //TODO: finish me
        //load all projects into the recycler view
    
        processTextFileViewModel.getProjectWithTextBlocksById(projectId).observe(getViewLifecycleOwner(), new Observer<ProjectWithTextBlocks>()
        {
            @Override
            public void onChanged(@Nullable ProjectWithTextBlocks textBlockData)
            {
                // Update your UI here.
                if(isNewProject)
                {
                    if(areTextBlocksInserted && (binding.progressBar.getVisibility() == View.VISIBLE))
                    {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
                else
                {
                    binding.progressBar.setVisibility(View.GONE);
                }

                Toast.makeText(requireActivity(), String.valueOf(textBlockData.textBlocks.size()) + " Text Blocks loaded", Toast.LENGTH_SHORT).show();

                queryCharacters(textBlockData);
                //update our recycler view
                //projectAdapter.setProjects(projects);
            }
        });
    }
    
    private void queryCharacters(ProjectWithTextBlocks textBlockData)
    {
        //TODO: For test purposes only
        processTextFileViewModel.performNamedEntityRecognition(textBlockData.textBlocks.get(25).getText(), "test", new ApiResponseListener()
        {
            @Override
            public void OnResponse(JSONObject response)
            {
                OnFetchedCharacters(response);
            }
    
            @Override
            public void OnError(VolleyError error)
            {
                OnFetchCharacterError(error.toString());
            }
    
            @Override
            public void OnException(JSONException ex)
            {
                OnQueryException(ex.toString());
            }
        });
    }
    
    private void OnFetchedCharacters(JSONObject apiResponse)
    {
        try
        {
            String id = apiResponse.getString("id");
            String object = apiResponse.getString("object");
            Date created = new Date(apiResponse.getInt("created"));
            String model = apiResponse.getString("model");
    
            String rawResponseText = "";
            JSONArray choicesArray = apiResponse.getJSONArray("choices");
            for (int i = 0; i < choicesArray.length(); i++)
            {
                JSONObject choiceObject = choicesArray.getJSONObject(i);
                rawResponseText = choiceObject.getString("text");
            }
            JSONObject usageObject = apiResponse.getJSONObject("usage");
            int promptTokens = usageObject.getInt("prompt_tokens");
            int completionTokens = usageObject.getInt("completion_tokens");
            int totalTokens = usageObject.getInt("total_tokens");
            
            JSONObject characterLines = new JSONObject(rawResponseText);
            binding.textView.setText(rawResponseText);
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