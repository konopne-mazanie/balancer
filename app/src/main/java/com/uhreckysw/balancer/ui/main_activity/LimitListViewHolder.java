package com.uhreckysw.balancer.ui.main_activity;

import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.ActivityMainListRowBinding;
import com.uhreckysw.balancer.databinding.LimitsListRowBinding;


public class LimitListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private LimitListViewAdapter limitListViewAdapter;
    public LimitsListRowBinding binding;

    LimitListViewHolder(LimitListViewAdapter limitListViewAdapter, View itemView, LimitsListRowBinding binding) {
        super(itemView);
        this.limitListViewAdapter = limitListViewAdapter;
        this.binding = binding;
        ImageButton edit_button = itemView.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (limitListViewAdapter.mClickListener != null) limitListViewAdapter.mClickListener.onSettingsButtonClick(view, getAdapterPosition());
            }
        });
        ImageButton delete_button = itemView.findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (limitListViewAdapter.mClickListener != null) limitListViewAdapter.mClickListener.onDeleteButtonClick(view, getAdapterPosition());
            }
        });
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (limitListViewAdapter.mClickListener != null) limitListViewAdapter.mClickListener.onItemClick(view, getAdapterPosition());
    }

}
