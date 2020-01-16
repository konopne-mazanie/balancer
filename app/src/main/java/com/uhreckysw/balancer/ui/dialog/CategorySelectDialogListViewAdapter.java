package com.uhreckysw.balancer.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;
import java.util.List;

public class CategorySelectDialogListViewAdapter extends RecyclerView.Adapter<CategorySelectDialogListViewHolder> {

    public List<CategorySelectDialogUiElem> mData;
    public LayoutInflater mInflater;
    public ItemClickListener mClickListener;

    CategorySelectDialogListViewAdapter(Context context, List<CategorySelectDialogUiElem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public CategorySelectDialogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.dialog_filter_category_select_list_row, parent, false);
        return new CategorySelectDialogListViewHolder(this, view, DataBindingUtil.bind(view));
    }

    @Override
    public void onBindViewHolder(CategorySelectDialogListViewHolder holder, int position) {
        holder.binding.setViewmodel(mData.get(position));
        holder.binding.executePendingBindings();
    }

    void clear() {mData.clear();}
    void add(CategorySelectDialogUiElem elem) {mData.add(elem);}

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}