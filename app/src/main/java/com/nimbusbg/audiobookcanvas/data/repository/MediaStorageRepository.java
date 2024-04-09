package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaStorageRepository
{
    Context context;
    
    public MediaStorageRepository(Application application)
    {
        this.context = application.getApplicationContext();
    }
    
    public File getAppDirectory()
    {
        return context.getExternalFilesDir(null);
    }
    
    public File makeFolderInAppStorage(String childFolder)
    {
        String newFolderPath = getAppDirectory().getAbsolutePath() + File.separator + childFolder;
        // Create the temporary folder if it doesn't exist
        File newFolder = new File(newFolderPath);
        if (!newFolder.exists())
        {
            newFolder.mkdirs();
        }
        return newFolder;
    }
    
    public File createChild(String root, String child)
    {
        File newChild = new File(root, child);
        if (!newChild.getParentFile().exists())
        {
            newChild.getParentFile().mkdirs(); // Make sure the directory exists
        }
        return newChild;
    }
    
    public File getAudioFile(String folderName, String fileName)
    {
        File folder = new File(folderName);
        if (!folder.exists())
        {
            Log.v("MediaStorageRepository", "Couldn't find folder " + folderName);
            return null;
        }
        return new File(folder, fileName);
    }
    
    // Helper method to extract the number from the file name
    private int extractNumber(String fileName)
    {
        String numberString = fileName.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberString);
    }
    
    public List<File> getMatchingFilesForId(File tmpFolder, int id, String fileType)
    {
        // List to store the matching WAV files
        List<File> matchingFiles = new ArrayList<>();
        
        // Find all WAV files that contain the ID in their names
        File[] allFiles = tmpFolder.listFiles();
        if (allFiles != null)
        {
            for (File file : allFiles)
            {
                if (file.isFile() && file.getName().contains(String.valueOf(id)) && file.getName().endsWith(fileType))
                {
                    matchingFiles.add(file);
                }
            }
        }
        
        // Sort the matching files based on the number in their names
        Collections.sort(matchingFiles, new Comparator<File>()
        {
            @Override
            public int compare(File file1, File file2)
            {
                int number1 = extractNumber(file1.getName());
                int number2 = extractNumber(file2.getName());
                return Integer.compare(number1, number2);
            }
        });
        
        return matchingFiles;
    }
    
    public void deleteFiles(List<File> files)
    {
        for (File file : files)
        {
            if (file.delete())
            {
                Log.d("TTS_REPOSITORY", "Deleted: " + file.getPath());
            }
            else
            {
                Log.e("TTS_REPOSITORY", "Failed to delete: " + file.getPath());
            }
        }
    }
    
    public void moveFileToMusicDirectory(File outputFile, String relativePath) throws IOException
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, outputFile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/x-wav");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + File.separator + relativePath);
        
        Uri uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null)
        {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                 FileInputStream inputStream = new FileInputStream(outputFile))
            {
                if (outputStream != null)
                {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0)
                    {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();
                }
            }
            
            // Verify if you want to delete the original file after moving it
            if (outputFile.delete())
            {
                Log.d("MediaStorageRepository", "Original output file deleted.");
            }
        }
        else
        {
            IOException ex = new IOException("Failed to insert MediaStore entry for the output file.");
            Log.e("MediaStorageRepository", "Error: " + ex.getMessage());
            throw ex;
        }
    }
    
    public File copyResourceToFile(int resourceId, String fileName)
    {
        Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(resourceId);
        File outputFile = new File(context.getFilesDir(), fileName);
        
        try (OutputStream outputStream = new FileOutputStream(outputFile))
        {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0)
            {
                outputStream.write(buffer, 0, length);
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e("MediaStorageRepository", "Error: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e("MediaStorageRepository", "Error: " + e.getMessage());
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                Log.e("MediaStorageRepository", "Error: " + e.getMessage());
            }
        }
        
        return outputFile;
    }
}
