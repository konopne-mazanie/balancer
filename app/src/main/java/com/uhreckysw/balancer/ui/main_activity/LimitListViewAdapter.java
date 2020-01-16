package com.uhreckysw.balancer.ui.main_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListenerLimits;

import java.util.List;

public class LimitListViewAdapter extends RecyclerView.Adapter<LimitListViewHolder> {

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
        if(position % 2 == 0) holder.binding.rootView.setBackgroundResource(R.color.colorWhite);
        else holder.binding.rootView.setBackgroundResource(R.color.colorDarkerWhite);

        holder.binding.setViewmodel(mData.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    LimitUIElem getItem(int id) {return mData.get(id);}
    void clear() {mData.clear();}
    void add(LimitUIElem elem) {mData.add(elem);}

    void setClickListener(ItemClickListenerLimits itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}