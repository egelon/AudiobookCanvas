package com.nimbusbg.audiobookcanvas.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;

import java.util.ArrayList;
import java.util.List;

public class TextBlockAdapter extends RecyclerView.Adapter<TextBlockAdapter.TextBlockHolder>
{
    private List<TextBlock> textBlocks = new ArrayList<TextBlock>();
    private TextBlockAdapter.OnTextBlockClickListener listener;
    
    @NonNull
    @Override
    public TextBlockAdapter.TextBlockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //This is where we inflate the layout (where we set how the rows are going to look like)
        //we use our project_item.xml for this
        View textBlockItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_block_item, parent, false);
        
        return new TextBlockAdapter.TextBlockHolder(textBlockItemView);
    }
    
    public interface OnTextBlockClickListener
    {
        void onTextBlockClicked(TextBlock textBlock);
    }
    
    public void setOnTextBlockClickListener(TextBlockAdapter.OnTextBlockClickListener listener)
    {
        this.listener = listener;
    }
    
    @Override
    public void onBindViewHolder(@NonNull TextBlockAdapter.TextBlockHolder holder, int position)
    {
        //here we set the values to each recycler view item
    
        TextBlock currentTextBlock = textBlocks.get(position);
        holder.textBlockId.setText(String.valueOf(currentTextBlock.getId()));
        holder.textBlockSnippet.setText(currentTextBlock.getText());
        
        int completedColor;
        if(!currentTextBlock.isProcessedByAPI())
        {
            //we are still waiting for the APi response
            completedColor = holder.itemView.getResources().getColor(R.color.textblock_waiting_api);
        }
        else
        {
            //we have received an API response for this text block at some point in the past. CHeck its status
            if(currentTextBlock.isReviewed())
            {
                //User has reviewed this text block
                completedColor = holder.itemView.getResources().getColor(R.color.textblock_done);
            }
            else
            {
                //User has not reviewed this text block yet
                completedColor = holder.itemView.getResources().getColor(R.color.textblock_not_reviewed);
            }
        }
        holder.completedTelltale.setColorFilter(completedColor);
    }
    
    @Override
    public int getItemCount()
    {
        //how many items do we want to display
        return textBlocks.size();
    }
    
    public void setTextBlocks(ProjectWithTextBlocks textBlockData)
    {
        //update our items when the LiveData has updated
        this.textBlocks = textBlockData.textBlocks;
        notifyDataSetChanged();
    }
    
    class TextBlockHolder extends RecyclerView.ViewHolder
    {
        private final TextView textBlockId;
        private final TextView textBlockSnippet;
        private final ImageView completedTelltale;
        
        public TextBlockHolder(@NonNull View itemView)
        {
            //binds views from our item layout xml to variables, so we can use them in onBindViewHolder
            super(itemView);
            textBlockId = itemView.findViewById(R.id.text_block_id);
            textBlockSnippet = itemView.findViewById(R.id.text_block_snippet);
            completedTelltale = itemView.findViewById(R.id.text_block_completed_telltale);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if(listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTextBlockClicked(textBlocks.get(position));
                }
            });
        }
    }
}
