package com.nimbusbg.audiobookcanvas.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
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
        populateCharacterLinesList();
        return binding.getRoot();
    }
    
    private void populateCharacterLinesList()
    {
        // Replace with your actual data
        /*
        List<String> data = characterLinesViewModel.getCharacterLines();
    
        for (MyData item : data) {
            View itemView = getLayoutInflater().inflate(R.layout.character_line_item, binding.CharacterLineLayout, false);
            Spinner characterNameSpinner = itemView.findViewById(R.id.character_item_name);
            TextView characterLineView = itemView.findViewById(R.id.character_item_line);
        
            // Set up the spinner with an ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, item.getOptions());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        
            // Set the pre-selected value for the spinner
            int spinnerPosition = adapter.getPosition(item.getSelectedOption());
            spinner.setSelection(spinnerPosition);
        
            // Set up the text view
            textView.setText(item.getText());
        
            // Add the view to the linear layout
            mLinearLayout.addView(itemView);
        }
         */
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
        loadAllCharacters();
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
                populateList(textBlockWithData.textBlock.getTextLines(), textBlockWithData.characterLines, storyCharacterNames);
            }
        });
    }
    
    private void populateList(String[] lines, List<CharacterLine> characterLines, List<String> characterNames)
    {
        for (CharacterLine line : characterLines)
        {
            View itemView = getLayoutInflater().inflate(R.layout.character_line_item, binding.CharacterLineLayout, false);
            Spinner spinner = itemView.findViewById(R.id.character_item_name);
            TextView textView = itemView.findViewById(R.id.character_item_line);
        
            // Set up the spinner with an ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, characterNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        
            // Set the pre-selected value for the spinner
            int spinnerPosition = adapter.getPosition(line.getCharacterName());
            spinner.setSelection(spinnerPosition);
        
            // Set up the text view
            textView.setText(lines[line.getStartIndex()]);
        
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
