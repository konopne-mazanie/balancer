package com.uhreckysw.balancer.ui.main_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;

import java.util.List;

public class PaymentListViewAdapter extends RecyclerView.Adapter<PaymentListViewHolder> {

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

    PaymentUIElem getItem(int id) {return mData.get(id);}
    void clear() {mData.clear();}
    void add(PaymentUIElem elem) {mData.add(elem);}

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}