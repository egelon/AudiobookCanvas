package com.nimbusbg.audiobookcanvas.views;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectHolder>
{
    private List<ProjectWithMetadata> projects = new ArrayList<ProjectWithMetadata>();
    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //This is where we inflate the layout (where we set how the rows are going to look like)
        //we use our project_item.xml for this
        View projectView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_item, parent, false);

        return new ProjectHolder(projectView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectHolder holder, int position)
    {
        //here we set the values to each recycler view item
        ProjectWithMetadata currentProject = projects.get(position);
        holder.projId.setText(String.valueOf(currentProject.project.getId()));
        holder.projName.setText(currentProject.project.getProjectName());
        boolean isCompleted = currentProject.project.getCompleted();
        holder.completedText.setText("Completed: " + String.valueOf(isCompleted));
        int completedColor;
        if(isCompleted)
        {
            //User completed this project
            completedColor = Color.parseColor("#00FF00");
        }
        else
        {
            if(currentProject.project.getLastProcessedBlockId() == 0)
            {
                //User has created, but hasn't started this project yet
                completedColor = Color.parseColor("#0000FF");
            }
            else
            {
                //User has started this project
                completedColor = Color.parseColor("#FF0000");
            }
        }
        holder.completedTelltale.setColorFilter(completedColor);
    }

    @Override
    public int getItemCount()
    {
        //how many items do we want to display
        return projects.size();
    }

    public void setProjects(List<ProjectWithMetadata> projects)
    {
        //update our items when the LiveData has updated
        this.projects = projects;
        notifyDataSetChanged();
    }

    class ProjectHolder extends RecyclerView.ViewHolder
    {
        private TextView projId;
        private TextView projName;
        private TextView completedText;
        private ImageView completedTelltale;

        public ProjectHolder(@NonNull View itemView)
        {
            //binds views from our item layout xml to variables, so we can use them in onBindViewHolder
            super(itemView);
            projId = itemView.findViewById(R.id.project_item_proj_id);
            projName = itemView.findViewById(R.id.project_item_proj_name);
            completedText = itemView.findViewById(R.id.project_item_proj_completed);
            completedTelltale = itemView.findViewById(R.id.project_item_proj_completed_telltale);
        }
    }
}
