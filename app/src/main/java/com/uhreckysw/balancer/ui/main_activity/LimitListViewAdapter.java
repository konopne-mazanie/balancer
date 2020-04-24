package com.uhreckysw.balancer.ui.main_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.LimitsListRowBinding;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListenerLimits;

import java.util.List;

public class LimitListViewAdapter extends RecyclerView.Adapter<LimitListViewAdapter.LimitListViewHolder> {

    public List<LimitUIElem> mData;
    public LayoutInflater mInflater;
    public ItemClickListenerLimits mClickListener;

    LimitListViewAdapter(Context context, List<LimitUIElem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public LimitListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.limits_list_row, parent, false);
        return new LimitListViewHolder(this, view, DataBindingUtil.bind(view));
    }

    @Override
    public void onBindViewHolder(LimitListViewHolder holder, int position) {
        holder.binding.setViewmodel(mData.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void setClickListener(ItemClickListenerLimits itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public static class LimitListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LimitListViewAdapter limitListViewAdapter;
        public LimitsListRowBinding binding;

        LimitListViewHolder(LimitListViewAdapter limitListViewAdapter, View itemView, LimitsListRowBinding binding) {
            super(itemView);
            this.limitListViewAdapter = limitListViewAdapter;
            this.binding = binding;
            ImageButton edit_button = itemView.findViewById(R.id.edit_button);
            edit_button.setOnClickListener(view -> {
                if (limitListViewAdapter.mClickListener != null) limitListViewAdapter.mClickListener.onSettingsButtonClick(view, getAdapterPosition());
            });
            ImageButton delete_button = itemView.findViewById(R.id.delete_button);
            delete_button.setOnClickListener(view -> {
                if (limitListViewAdapter.mClickListener != null) limitListViewAdapter.mClickListener.onDeleteButtonClick(view, getAdapterPosition());
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (limitListViewAdapter.mClickListener != null) limitListViewAdapter.mClickListener.onItemClick(view, getAdapterPosition());
        }

    }
}