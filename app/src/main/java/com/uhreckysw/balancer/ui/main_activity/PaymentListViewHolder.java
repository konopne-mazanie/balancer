package com.uhreckysw.balancer.ui.main_activity;

import android.view.View;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.ActivityMainListRowBinding;


public class PaymentListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private PaymentListViewAdapter paymentListViewAdapter;
    public ActivityMainListRowBinding binding;

    PaymentListViewHolder(PaymentListViewAdapter paymentListViewAdapter, View itemView, ActivityMainListRowBinding binding) {
        super(itemView);
        this.paymentListViewAdapter = paymentListViewAdapter;
        this.binding = binding;
        ImageButton edit_button = itemView.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentListViewAdapter.mClickListener != null) paymentListViewAdapter.mClickListener.onSettingsButtonClick(view, getAdapterPosition());
            }
        });
        ImageButton copy_button = itemView.findViewById(R.id.copy_button);
        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentListViewAdapter.mClickListener != null) paymentListViewAdapter.mClickListener.onCopyButtonClick(view, getAdapterPosition());
            }
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
