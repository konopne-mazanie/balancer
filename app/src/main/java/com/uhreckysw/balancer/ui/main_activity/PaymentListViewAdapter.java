package com.uhreckysw.balancer.ui.main_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.ActivityMainListRowBinding;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;

import java.util.List;

public class PaymentListViewAdapter extends RecyclerView.Adapter<PaymentListViewAdapter.PaymentListViewHolder> {

    public List<PaymentUIElem> mData;
    public LayoutInflater mInflater;
    public ItemClickListener mClickListener;

    PaymentListViewAdapter(Context context, List<PaymentUIElem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public PaymentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_main_list_row, parent, false);
        return new PaymentListViewHolder(this, view, DataBindingUtil.bind(view));
    }

    @Override
    public void onBindViewHolder(PaymentListViewHolder holder, int position) {
        if(position % 2 == 0) holder.binding.rootView.setBackgroundResource(R.color.colorWhite);
        else holder.binding.rootView.setBackgroundResource(R.color.colorDarkerWhite);

        holder.binding.setViewmodel(mData.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public static class PaymentListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private PaymentListViewAdapter paymentListViewAdapter;
        public ActivityMainListRowBinding binding;

        PaymentListViewHolder(PaymentListViewAdapter paymentListViewAdapter, View itemView, ActivityMainListRowBinding binding) {
            super(itemView);
            this.paymentListViewAdapter = paymentListViewAdapter;
            this.binding = binding;
            ImageButton edit_button = itemView.findViewById(R.id.edit_button);
            edit_button.setOnClickListener(view -> {
                if (paymentListViewAdapter.mClickListener != null) paymentListViewAdapter.mClickListener.onSettingsButtonClick(view, getAdapterPosition());
            });
            ImageButton copy_button = itemView.findViewById(R.id.copy_button);
            copy_button.setOnClickListener(view -> {
                if (paymentListViewAdapter.mClickListener != null) paymentListViewAdapter.mClickListener.onCopyButtonClick(view, getAdapterPosition());
            });
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (paymentListViewAdapter.mClickListener != null) paymentListViewAdapter.mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (paymentListViewAdapter.mClickListener != null) paymentListViewAdapter.mClickListener.onItemLongClick(view, getAdapterPosition());
            else return false;
            return true;
        }

    }
}