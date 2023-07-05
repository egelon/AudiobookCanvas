package com.nimbusbg.audiobookcanvas.viewmodelfactories;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nimbusbg.audiobookcanvas.viewmodels.ProcessTextFileViewModel;

public class ProcessTextFileViewModelFactory implements ViewModelProvider.Factory
{
    private Application mApplication;
    private int mProjId;
    
    
    public ProcessTextFileViewModelFactory(Application application, int projId)
    {
        mApplication = application;
        mProjId = projId;
    }
    
    
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass)
    {
        return (T) new ProcessTextFileViewModel(mApplication, mProjId);
    }
}
