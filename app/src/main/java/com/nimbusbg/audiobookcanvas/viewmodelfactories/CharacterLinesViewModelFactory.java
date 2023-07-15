package com.nimbusbg.audiobookcanvas.viewmodelfactories;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nimbusbg.audiobookcanvas.viewmodels.CharacterLinesViewModel;


public class CharacterLinesViewModelFactory implements ViewModelProvider.Factory
{
    private Application mApplication;
    private int mTextBlockId;
    private int mProjectId;
    
    
    public CharacterLinesViewModelFactory(Application application, int textblockId, int projectId)
    {
        mApplication = application;
        mTextBlockId = textblockId;
        mProjectId = projectId;
    }
    
    
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass)
    {
        return (T) new CharacterLinesViewModel(mApplication, mTextBlockId, mProjectId);
    }
}