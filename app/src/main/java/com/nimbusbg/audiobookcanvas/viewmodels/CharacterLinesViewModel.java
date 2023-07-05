package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;

import java.util.List;

public class CharacterLinesViewModel extends AndroidViewModel
{
    private final AudiobookRepository repository;
    private LiveData<TextBlockWithData> currentTextBlockWithData;
    private LiveData<List<StoryCharacter>> allCharacters;
    
    public CharacterLinesViewModel(@NonNull Application application, int textblockId)
    {
        super(application);
        repository = new AudiobookRepository(application);
    
        currentTextBlockWithData = repository.getTextBlockWithDataByTextBlockId(textblockId);
        allCharacters = repository.getAllCharacters();
    }
    
    public LiveData<List<CharacterLine>> getCharacterLinesByTextBlockId(int id)
    {
        return repository.getCharacterLinesByTextBlockId(id);
    }
    
    public LiveData<List<StoryCharacter>> getAllCharacters()
    {
        return allCharacters;
    }
    
    public LiveData<TextBlockWithData> getTextBlockWithData()
    {
        return currentTextBlockWithData;
    }
}
