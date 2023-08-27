package com.nimbusbg.audiobookcanvas.viewmodelfactories;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nimbusbg.audiobookcanvas.viewmodels.TextChunkingViewModel;

public class TextChunkingViewModelFactory implements ViewModelProvider.Factory
{
    private Application mApplication;
    private int mProjId;
    private String mTextFileUri;
    
    public TextChunkingViewModelFactory(Application application, int projId, String textFileUri)
    {
        mApplication = application;
        mProjId = projId;
        mTextFileUri = textFileUri;
    }
    
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass)
    {
        return (T) new TextChunkingViewModel(mApplication, mProjId, mTextFileUri);
    }
}