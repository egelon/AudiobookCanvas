package com.nimbusbg.audiobookcanvas.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.databinding.ProjectSetupFragmentBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectSetupFragment extends Fragment{
    EditText projectName;
    EditText audiobookName;
    EditText bookName;
    EditText authorName;
    EditText projectDescription;
    EditText xmlFileName;
    Button selectTxtFileBtn;
    Button exportAsXMLBtn;
    Button processChunkBtn;
    TextView textFileName;
    TextView lastEditedOn;
    TextView lastProcessedTxtBlock;
    TextView percentProcessed;

    ActivityResultLauncher<Intent> filePickerLauncher;




    Context appActivityContext;
    Button btnListCharacters, btnCancel;
    ActivityResultLauncher<Intent> filePicker;
    private ProjectSetupFragmentBinding binding;
    private ArrayList<String> contentChunks;
    private int maxChunkSize = 1400;
    String openai_completions_endpoint = "https://api.openai.com/v1/completions";
    public static final String requestTag = "NamedEntityRecognitionRequest";

    private Uri textFileURI;
    private Uri projectFileURI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = ProjectSetupFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    onTextFileSelected(result);
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.save_project_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int optionItemID = item.getItemId();
        switch (optionItemID) {
            case R.id.action_save_project:
                return isProjectSavedSuccessfully();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isProjectSavedSuccessfully()
    {
        String projectNameStr = projectName.getText().toString();
        String audiobookNameStr = audiobookName.getText().toString();
        String bookNameStr = bookName.getText().toString();
        String authorNameStr = authorName.getText().toString();
        String projectDescriptionStr = projectDescription.getText().toString();
        String xmlFileNameStr = xmlFileName.getText().toString();
        String textFileNameStr = textFileName.getText().toString();

        if(textFileNameStr.trim().isEmpty())
        {
            Toast.makeText(this.getActivity(), "No text file selected", Toast.LENGTH_SHORT).show();
            //return false;
        }

        Toast.makeText(this.getActivity(), "Project Saved", Toast.LENGTH_SHORT).show();

        return true;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        appActivityContext = this.getActivity();

        projectName = binding.projName;
        audiobookName = binding.audiobookName;
        bookName = binding.bookName;
        authorName = binding.authorName;
        projectDescription = binding.descriptionText;
        xmlFileName = binding.xmlFile;

        selectTxtFileBtn = binding.textFileBtn;
        selectTxtFileBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onSelectTxtFileClicked(view);
            }});

        exportAsXMLBtn = binding.exportAsXMLBtn;
        exportAsXMLBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onExportAsXMLClicked(view);
            }});

        processChunkBtn = binding.processTxtBlockBtn;
        processChunkBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onProcessChunkClicked(view);
            }});

        textFileName = binding.textFilePath;
        lastEditedOn = binding.lastEditedOn;
        lastProcessedTxtBlock = binding.lastProcessedBlock;
        percentProcessed = binding.percentCompleted;

        setProjectValues();



        //this.getActivity().setTitle(R.string.edit_project_title);


        //setup the fragment result listener


        /*
        btnCancel = view.findViewById(R.id.btnCancelFilePreview);
        btnListCharacters = view.findViewById(R.id.btnInvokeAPI);
        btnListCharacters.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                chunkInputFile(appActivityContext, textFileURI);
                createProjectXML(editProjectName.getText().toString() + ".xml", appActivityContext);
                int testChunk = 11;
                try {
                    getCompletion(contentChunks.get(testChunk), appActivityContext);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

         */

        //textFileContentView = view.findViewById(R.id.textFileContent);
        //textFileContentView.setMovementMethod(new ScrollingMovementMethod());

        //editProjectName = view.findViewById(R.id.editProjectName);

        //String textFilePath = com.nimbusbg.audiobookcanvas.views.PreviewTxtFileFragmentArgs.fromBundle(getArguments()).getTxtFilePath();
        //String projectFilePath = PreviewTxtFileFragmentArgs.fromBundle(getArguments()).getProjFilePath();
        //textFileURI = Uri.parse(textFilePath);
        //projectFileURI = Uri.parse(projectFilePath);

        //textFileContentView.setText("Slected file: " + textFileURI.toString());

    }

    private void setProjectValues()
    {
        ProjectWithMetadata selectedProject = (ProjectWithMetadata) getArguments().getSerializable("projectWithMetadata");

        projectName.setText(selectedProject.project.getProjectName());
        audiobookName.setText(selectedProject.project.getProjectName());
        bookName.setText(selectedProject.audiobookData.getBookTitle());
        authorName.setText(selectedProject.audiobookData.getAuthor());
        projectDescription.setText(selectedProject.audiobookData.getDescription());
        xmlFileName.setText(selectedProject.project.getOutputXMLFilePath());
        textFileName.setText(selectedProject.project.getInputFilePath());

        Date lastModifiedDate = selectedProject.project.getLastModified();
        lastEditedOn.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(lastModifiedDate));

        int lastBlockID = selectedProject.project.getLastProcessedBlockId();
        lastProcessedTxtBlock.setText(String.valueOf(lastBlockID));

        if(lastBlockID == 0)
        {
            processChunkBtn.setText(R.string.start_processing_btn_label);
        }
        else
        {
            processChunkBtn.setText(R.string.continue_processing_btn_label);
        }

        if(selectedProject.project.getCompleted())
        {
            percentProcessed.setText("100%");
        }
        else
        {
            calculateBookCompletion(lastBlockID);
        }

    }

    private void calculateBookCompletion(int lastBlockID)
    {
        if(lastBlockID == 0)
        {
            percentProcessed.setText("0%");
        }
        else
        {
            //TODO: get the max chunks and calculate it
            percentProcessed.setText("45%");
        }

    }

    public void onSelectTxtFileClicked(View view)
    {
        //TODO: THIS MUST BE IN A REPOSITORY!
        try
        {
            //start picking a file
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            fileIntent.setType("text/plain");
            filePickerLauncher.launch(fileIntent);
        }
        catch (Exception ex)
        {
            Log.e("Error", ex.getMessage());
            Toast.makeText(this.getActivity(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onExportAsXMLClicked(View view)
    {
        Toast.makeText(this.getActivity(),"Export as XML", Toast.LENGTH_LONG).show();
    }

    public void onProcessChunkClicked(View view)
    {
        Toast.makeText(this.getActivity(),"Process chunk", Toast.LENGTH_LONG).show();
    }

    private void onTextFileSelected(ActivityResult result)
    {
        //TODO: THIS MUST BE IN A REPOSITORY!
        if (result.getResultCode() == Activity.RESULT_OK)
        {

            Intent selectFileIntent = result.getData();
            Uri uri = selectFileIntent.getData();
            Toast.makeText(this.getActivity(), "File: " + uri.getPath().toString(), Toast.LENGTH_SHORT).show();

            textFileName.setText(uri.toString());
            //String projFilePath = "newProj.xml";
            //WelcomeFragmentDirections.ActionTextFileSelected action = WelcomeFragmentDirections.actionTextFileSelected(txtFilePath, projFilePath);
            //navController.navigate(action);
        }
        else if(result.getResultCode() == Activity.CONTEXT_RESTRICTED)
        {
            Toast.makeText(this.getActivity(), R.string.no_permission_to_open_txt_file, Toast.LENGTH_LONG).show();
        }
        else
        {
            //user canceled file selection
        }
    }

    //TODO: THIS NEEDS TO BE IN THE REPOSITORY!!!!
    private int getNumberOfChunks(Context context, Uri uri)
    {
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

        return contentChunks.size();
    }

    /*


    private void createProjectXML(String fileName, Context context)
    {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        //File path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try
        {
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

     */

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        //RequestQueueSingleton.getInstance(this.getActivity()).getRequestQueue().cancelAll(requestTag);
        binding = null;
    }


}