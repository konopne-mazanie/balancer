package com.uhreckysw.balancer.ui.dialog;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.uhreckysw.balancer.databinding.DialogFilterCategorySelectListRowBinding;


public class CategorySelectDialogListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public DialogFilterCategorySelectListRowBinding binding;
    private CategorySelectDialogListViewAdapter categorySelectDialogListViewAdapter;

    CategorySelectDialogListViewHolder(CategorySelectDialogListViewAdapter categorySelectDialogListViewAdapter, View itemView, DialogFilterCategorySelectListRowBinding binding) {
        super(itemView);
        this.categorySelectDialogListViewAdapter = categorySelectDialogListViewAdapter;
        this.binding = binding;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (categorySelectDialogListViewAdapter.mClickListener != null) categorySelectDialogListViewAdapter.mClickListener.onItemClick(view, getAdapterPosition());
    }

}
