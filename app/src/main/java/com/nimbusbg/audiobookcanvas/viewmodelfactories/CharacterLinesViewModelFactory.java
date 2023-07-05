package com.nimbusbg.audiobookcanvas.viewmodelfactories;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nimbusbg.audiobookcanvas.viewmodels.CharacterLinesViewModel;


public class CharacterLinesViewModelFactory implements ViewModelProvider.Factory
{
    private Application mApplication;
    private int mTextBlockId;
    
    
    public CharacterLinesViewModelFactory(Application application, int id)
    {
        mApplication = application;
        mTextBlockId = id;
    }
    
    
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass)
    {
        return (T) new CharacterLinesViewModel(mApplication, mTextBlockId);
    }
}