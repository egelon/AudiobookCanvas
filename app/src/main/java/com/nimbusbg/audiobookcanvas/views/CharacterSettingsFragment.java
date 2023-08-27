package com.nimbusbg.audiobookcanvas.views;

import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.databinding.CharacterSettingsFragmentBinding;
import com.nimbusbg.audiobookcanvas.viewmodelfactories.CharacterSettingsViewModelFactory;
import com.nimbusbg.audiobookcanvas.viewmodels.CharacterSettingsViewModel;

import java.util.ArrayList;
import java.util.List;

public class CharacterSettingsFragment extends Fragment
{
    private CharacterSettingsFragmentBinding binding;
    private int projectId;
    
    private CharacterSettingsViewModel characterSettingsViewModel;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = CharacterSettingsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null)
        {
            projectId = getArguments().getInt("projectID");
        }
        else
        {
            projectId = -1;
            Toast.makeText(requireActivity(), "Missing textblockID!", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        characterSettingsViewModel = new ViewModelProvider(NavHostFragment.findNavController(this).getViewModelStoreOwner(R.id.nav_graph), new CharacterSettingsViewModelFactory(requireActivity().getApplication(), projectId)).get(CharacterSettingsViewModel.class);
        characterSettingsViewModel.getAllCharacters().observe(getViewLifecycleOwner(), new Observer<List<StoryCharacter>>()
        {
            @Override
            public void onChanged(List<StoryCharacter> storyCharacters)
            {
                if(storyCharacters != null)
                {
                    characterSettingsViewModel.waitForTTS(new TtsInitListener()
                    {
                        @Override
                        public void OnInitSuccess()
                        {
                            loadCharacters(storyCharacters);
                        }
        
                        @Override
                        public void OnInitFailure()
                        {
                            Toast.makeText(requireActivity(), "Couldn't initialise TTS", Toast.LENGTH_LONG).show();
                        }
                    });
                    
                }
            }
        });
    }

    private void loadCharacters(List<StoryCharacter> storyCharacters)
    {
        ArrayList<String> allVoices = characterSettingsViewModel.getExtendedEnglishVoices();
    
        binding.characterSettingsLayout.removeAllViews();
        for (int i = 0; i < storyCharacters.size(); i++)
        {
            String currentVoice = storyCharacters.get(i).getVoice();
            View itemView = getLayoutInflater().inflate(R.layout.character_settings_item, binding.characterSettingsLayout, false);
            Spinner voicePicker = itemView.findViewById(R.id.characterSettingsVoice);
            TextView characterName = itemView.findViewById(R.id.characterSettingsName);
            Button playVoiceSampleButton = itemView.findViewById(R.id.playSampleVoiceBtn);
    
            // Attach all voices to the spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allVoices);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            voicePicker.setAdapter(adapter);
            voicePicker.setTag(i);
            voicePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                boolean isSpinnerInitialSelected = true;
    
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (isSpinnerInitialSelected)
                    {
                        isSpinnerInitialSelected = false;
                    } else
                    {
                        String selectedVoice = (String) parent.getItemAtPosition(position);
                        int selectedCharacterIndex = (int) parent.getTag();
                        StoryCharacter selectedCharacter = storyCharacters.get(selectedCharacterIndex);
                        // Here you can call your function using the selected item
                        characterSettingsViewModel.updateCharacterVoice(selectedCharacter.getName(), selectedVoice);
                    }
                }
    
                @Override
                public void onNothingSelected(AdapterView<?> adapterView)
                {
    
                }
            });
    
            // Set the pre-selected value for the spinner
            int currentVoiceId = adapter.getPosition(currentVoice);
            voicePicker.setSelection(currentVoiceId);
            int backgroundColor = itemView.getResources().getColor(R.color.voice_general_background);;
            if(storyCharacters.get(i).getGender().equals("male"))
            {
                backgroundColor = itemView.getResources().getColor(R.color.voice_male_background);
                
            }
            else if(storyCharacters.get(i).getGender().equals("female"))
            {
                backgroundColor = itemView.getResources().getColor(R.color.voice_female_background);
            }
            characterName.setBackgroundColor(backgroundColor);
    
            // Set up the text view
            characterName.setText(storyCharacters.get(i).getName());
    
            playVoiceSampleButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    characterSettingsViewModel.playVoiceSampleForVoice(currentVoice);
                }
            });
    
            // Add the view to the linear layout
            binding.characterSettingsLayout.addView(itemView);
        }
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
        characterSettingsViewModel.destroyTTS();
    }
}