package com.example.audiobookcanvas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.audiobookcanvas.databinding.FragmentPreviewTextFileBinding;

import org.json.JSONException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import com.example.audiobookcanvas.RequestQueueSingleton;

public class PreviewTxtFileFragment extends Fragment {

    TextView textFileContentView;
    EditText editProjectName;
    Context appActivityContext;
    Button btnListCharacters, btnCancel;
    ActivityResultLauncher<Intent> filePicker;
    private FragmentPreviewTextFileBinding binding;
    private ArrayList<String> contentChunks;
    private int maxChunkSize = 1800;
    String openai_completions_endpoint = "https://api.openai.com/v1/completions";
    public static final String requestTag = "NamedEntityRecognitionRequest";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentPreviewTextFileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        appActivityContext = this.getActivity();

        btnCancel = view.findViewById(R.id.btnCancelFilePreview);
        btnListCharacters = view.findViewById(R.id.btnInvokeAPI);
        btnListCharacters.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createProjectXML(editProjectName.getText().toString() + ".xml", appActivityContext);
                int testChunk = 150;
                try {
                    getCompletion(contentChunks.get(testChunk), appActivityContext);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        textFileContentView = view.findViewById(R.id.textFileContent);
        textFileContentView.setMovementMethod(new ScrollingMovementMethod());

        editProjectName = view.findViewById(R.id.editProjectName);

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
                    chunkInputFile(this.getActivity(), uri);
                    textFileContentView.setText(contentChunks.get(0));
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

    private void chunkInputFile(Context context, Uri uri) {
        InputStream inputStream = null;
        contentChunks = new ArrayList<String>();
        StringBuilder text = new StringBuilder();
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            String bufferStringContent = outputStream.toString("UTF-8");
            Pattern sentenceBoundaryPattern = Pattern.compile("[.!?]+\\s*");

            for (int i = 0; i < bufferStringContent.length(); i++)
            {
                text.append(bufferStringContent.charAt(i));

                if (text.length() >= maxChunkSize)
                {
                    Matcher matcher = sentenceBoundaryPattern.matcher(text);
                    int lastIndex = 0;

                    while (matcher.find())
                    {
                        if (matcher.start() >= maxChunkSize)
                        {
                            break;
                        }
                        lastIndex = matcher.end();
                    }

                    if (lastIndex > 0)
                    {
                        contentChunks.add(text.substring(0, lastIndex));
                        text.delete(0, lastIndex);
                    }
                }
            }

            if (text.length() > 0)
            {
                contentChunks.add(text.toString());
            }
        }
        catch (IOException ex) {
            Log.e("Error", ex.getMessage());
            Toast.makeText(context, "getBytes error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createProjectXML(String fileName, Context context)
    {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        //File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try
        {
            //   /storage/emulated/0/Android/data/com.example.audiobookcanvas/files/newProject.xml


            FileOutputStream fileOutputStream = new FileOutputStream (new File(path, fileName));


            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag("", "audiobook");

            // Metadata, filename, and other XML elements can be added here

            xmlSerializer.startTag("", "content_blocks");

            for (int i = 0; i < contentChunks.size(); i++)
            {
                String chunk = contentChunks.get(i);

                xmlSerializer.startTag("", "content_block");
                xmlSerializer.attribute("", "size", String.valueOf(chunk.length()));
                xmlSerializer.attribute("", "index", String.valueOf(i));

                xmlSerializer.startTag("", "text");
                xmlSerializer.text(chunk);
                xmlSerializer.endTag("", "text");

                // Processed_text, background_tone, background_music, characters, and corrections can be added here

                xmlSerializer.endTag("", "content_block");
            }

            xmlSerializer.endTag("", "content_blocks");

            // Audio and other XML elements can be added here

            xmlSerializer.endTag("", "audiobook");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();
            Toast.makeText(context, "Project " + fileName + " created", Toast.LENGTH_SHORT).show();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            Log.e("Error", ex.getMessage());
            Toast.makeText(context, "getBytes error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getCompletion(String textChunk, Context context) throws JSONException {
        // setting text on for question on below line.
        // creating a queue for request queue.
        // creating a json object on below line.
        JSONObject requestBody = new JSONObject();
        // adding params to json object.
        requestBody.put("model", "text-davinci-003");
        String prompt = getString(R.string.named_entity_recognition_prompt) + textChunk + "\n\n[Output]";
        requestBody.put("prompt", prompt);
        requestBody.put("temperature", 0);
        requestBody.put("max_tokens", 1500);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0.0);
        requestBody.put("presence_penalty", 0.0);


        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, openai_completions_endpoint, requestBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        textFileContentView.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                textFileContentView.setText("Response Error: " + error.toString());
            }
        }


        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", " application/json");
                params.put("Authorization", "Bearer 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");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        jsonRequest.setTag(requestTag);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonRequest);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        RequestQueueSingleton.getInstance(this.getActivity()).getRequestQueue().cancelAll(requestTag);
        binding = null;
    }
}