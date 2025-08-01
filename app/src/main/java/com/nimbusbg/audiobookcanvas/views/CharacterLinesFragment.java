package com.nimbusbg.audiobookcanvas.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.listeners.MixingProcessListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.databinding.CharacterLinesFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodelfactories.CharacterLinesViewModelFactory;
import com.nimbusbg.audiobookcanvas.viewmodels.CharacterLinesViewModel;

import java.util.ArrayList;
import java.util.List;

public class CharacterLinesFragment extends Fragment
{
    private CharacterLinesFragmentBinding binding;
    private int textblockId;
    private int projectId;
    
    private CharacterLinesViewModel characterLinesViewModel;
    
    private List<String> storyCharacterNames;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = CharacterLinesFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    
        if (getArguments() != null)
        {
            textblockId = getArguments().getInt("textblockID");
            projectId = getArguments().getInt("projectID");
            //Toast.makeText(requireActivity(), "textblockID: " + String.valueOf(textblockId), Toast.LENGTH_SHORT).show();
        }
        else
        {
            textblockId = -1;
            projectId = -1;
            Toast.makeText(requireActivity(), "Missing textblockID!", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        characterLinesViewModel = new ViewModelProvider(this, new CharacterLinesViewModelFactory(requireActivity().getApplication(), textblockId, projectId)).get(CharacterLinesViewModel.class);
        storyCharacterNames = new ArrayList<String>();
    
        binding.generateAudioBtn.setVisibility(View.GONE);
        binding.generateAudioProgressBar.setVisibility(View.GONE);
        binding.generateAudioBtn.setOnClickListener(new View.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(View view)
                                                        {
                                                            binding.generateAudioProgressBar.setVisibility(View.VISIBLE);
                                                            characterLinesViewModel.recordAllCharacterLines(new MixingProcessListener()
                                                            {
                                                                @Override
                                                                public void onProgress(double progress)
                                                                {
                                                                    binding.generateAudioProgressBar.setProgress((int) (progress * 10000));
                                                                }
            
                                                                @Override
                                                                public void onEnd()
                                                                {
                                                                    binding.generateAudioProgressBar.setProgress(10000);
                                                                }
                                                            });
                                                        }
                                                    });
    
        Log.d("CharacterLinesFragment", "Trying to observe tts status");
    
    
        characterLinesViewModel.getProjectMetadata().observe(getViewLifecycleOwner(), new Observer<ProjectWithMetadata>()
        {
            @Override
            public void onChanged(ProjectWithMetadata projectWithMetadata)
            {
                    characterLinesViewModel.getTtsInitStatus().observe(getViewLifecycleOwner(), isInitialized -> {
                    if (isInitialized)
                    {
                        Log.d("CharacterLinesFragment", "getTtsInitStatus: " + isInitialized);
                        loadAllCharacters();
                    }
                    else
                    {
                        Log.d("CharacterLinesFragment", "getTtsInitStatus: " + isInitialized);
                    }
                });
            }
        });
        
        characterLinesViewModel.getProcessedUtterances().observe(getViewLifecycleOwner(), processedUtterances ->{
            int numCharacterLines = characterLinesViewModel.getNumCharacterLines();
            float progress = 0.0f;
            if(processedUtterances == numCharacterLines && numCharacterLines > 0)
            {
                binding.generateAudioProgressBar.setProgress(10000);
                characterLinesViewModel.combineVoices(new MixingProcessListener()
                {
                    @Override
                    public void onProgress(double progress)
                    {
                        Log.d("CharacterLinesViewModel", "combineVoices progress: " + progress);
    
    
    
                        requireActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                binding.generateAudioProgressBar.setBackgroundColor(getResources().getColor(R.color.textblock_waiting_api));
                                binding.generateAudioProgressBar.setProgress((int) (progress * 10000));
                            }
                        });
                        
                        
                    }
    
                    @Override
                    public void onEnd()
                    {
                        binding.generateAudioProgressBar.setProgress(10000);
                        binding.generateAudioProgressBar.setBackgroundColor(getResources().getColor(R.color.textblock_done));
                        characterLinesViewModel.setCurrentTextblockDone();
                    }
                });
            }
            else
            {
                try
                {
                    progress = characterLinesViewModel.mapIntToFloatRange(processedUtterances, characterLinesViewModel.getNumCharacterLines());
                }
                catch (IllegalArgumentException e)
                {
                    Log.e("CharacterLinesViewModel", "recordAllCharacterLines, " + e.getMessage());
                }
                binding.generateAudioProgressBar.setProgress((int) (progress * 10000));
            }
        });
        
        characterLinesViewModel.getWavFilesStitched().observe(getViewLifecycleOwner(), areFilesStitched -> {
            if(areFilesStitched)
            {
                Log.d("CharacterLinesFragment", "areFilesStitched: " + areFilesStitched);
                characterLinesViewModel.addBackgroundMusic(new MixingProcessListener()
                {
                    @Override
                    public void onProgress(double progress)
                    {
                        binding.generateAudioProgressBar.setProgress((int) (progress * 10000));
                    }
    
                    @Override
                    public void onEnd()
                    {
                        binding.generateAudioProgressBar.setProgress(10000);
                        binding.generateAudioProgressBar.setBackgroundColor(getResources().getColor(R.color.textblock_done));
                        characterLinesViewModel.setCurrentTextblockDone();
                    }
                });
            }
            else
            {
                Log.d("CharacterLinesFragment", "Waiting to stitch files");
            }
        });
        
    }
    
    private void loadAllCharacters()
    {
        characterLinesViewModel.getAllCharacters().observe(getViewLifecycleOwner(), new Observer<List<StoryCharacter>>()
        {
            @Override
            public void onChanged(List<StoryCharacter> storyCharacters)
            {
                if(storyCharacters != null)
                {
                    for (StoryCharacter storyCharacter : storyCharacters)
                    {
                        storyCharacterNames.add(storyCharacter.getName());
                    }
    
                    loadCharacterLines();
                }
            }
        });
    }
    
    private void loadCharacterLines()
    {
        characterLinesViewModel.getTextBlockWithData().observe(getViewLifecycleOwner(), new Observer<TextBlockWithData>()
        {
            @Override
            public void onChanged(TextBlockWithData textBlockWithData)
            {
                characterLinesViewModel.setNumCharacterLines(textBlockWithData.characterLines.size());
                populateList(textBlockWithData.characterLines, storyCharacterNames);
                binding.generateAudioBtn.setVisibility(View.VISIBLE);
            }
        });
    }
    
    private void populateList(List<CharacterLine> characterLines, List<String> characterNames)
    {
        binding.CharacterLineLayout.removeAllViews();
        int i=0;
        for (CharacterLine line : characterLines)
        {
            View itemView = getLayoutInflater().inflate(R.layout.character_line_item, binding.CharacterLineLayout, false);
            Spinner spinner = itemView.findViewById(R.id.character_item_name);
            TextView textView = itemView.findViewById(R.id.character_item_line);
        
            // Set up the spinner with an ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, characterNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setTag(i);
            i++;
    
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                boolean isSpinnerInitialSelected = true;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (isSpinnerInitialSelected)
                    {
                        isSpinnerInitialSelected = false;
                    }
                    else
                    {
                        String selectedCharacter = (String) parent.getItemAtPosition(position);
                        int itemIndex = (int) parent.getTag();
                        // Here you can call your function using the selected item
                        characterLinesViewModel.updateCharacter(selectedCharacter, itemIndex, textblockId);
                    }
                }
    
                @Override
                public void onNothingSelected(AdapterView<?> adapterView)
                {
        
                }
            });
        
            // Set the pre-selected value for the spinner
            int spinnerPosition = adapter.getPosition(line.getCharacterName());
            spinner.setSelection(spinnerPosition);
        
            // Set up the text view
            textView.setText(line.getLine());
        
            // Add the view to the linear layout
            binding.CharacterLineLayout.addView(itemView);
        }
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
