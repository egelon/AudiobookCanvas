package com.example.audiobookcanvas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.audiobookcanvas.databinding.FragmentPreviewTextFileBinding;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class PreviewTxtFileFragment extends Fragment {

    TextView textFileContentView;
    ActivityResultLauncher<Intent> filePicker;
    private FragmentPreviewTextFileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentPreviewTextFileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        textFileContentView = view.findViewById(R.id.textFileContent);
        textFileContentView.setMovementMethod(new ScrollingMovementMethod());
        selectFileFromStorage();
    }

    private void selectFileFromStorage()
    {
        filePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK)
                {
                    Intent intent1 = result.getData();
                    Uri uri = intent1.getData();
                    byte[] byteData = getBytes(this.getActivity(), uri);
                    String bookText = new String(byteData);
                    textFileContentView.setText(bookText);
                }
            });


        try
        {
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            fileIntent.setType("text/*");
            filePicker.launch(fileIntent);
        }
        catch (Exception ex)
        {
            Log.e("Error", ex.getMessage());
            Toast.makeText(this.getActivity(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private String readFile(String input)
    {
        File file = new File(input);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader buffReader = new BufferedReader((new FileReader(file)));
            String line;
            while((line = buffReader.readLine()) != null)
            {
                text.append(line);
                text.append("\n");
            }
            buffReader.close();
        } catch(IOException ex)
        {
            Log.e("Error", ex.getMessage());
            Toast.makeText(this.getActivity(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
        return text.toString();
    }

    byte[] getBytes(Context context, Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage().toString());
            Toast.makeText(context, "getBytes error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}