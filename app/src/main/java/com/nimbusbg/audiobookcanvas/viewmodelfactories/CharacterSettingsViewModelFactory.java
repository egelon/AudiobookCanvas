package com.nimbusbg.audiobookcanvas.viewmodelfactories;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nimbusbg.audiobookcanvas.viewmodels.CharacterSettingsViewModel;

public class CharacterSettingsViewModelFactory implements ViewModelProvider.Factory
{
    private Application mApplication;
    private int mProjectId;
    
    public CharacterSettingsViewModelFactory(Application application, int projectId)
    {
        mApplication = application;
        mProjectId = projectId;
    }
    
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass)
    {
        return (T) new CharacterSettingsViewModel(mApplication, mProjectId);
    }
}
