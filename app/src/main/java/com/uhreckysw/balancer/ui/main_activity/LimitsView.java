package com.uhreckysw.balancer.ui.main_activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.backend.db.Limit;
import com.uhreckysw.balancer.databinding.LimitsViewBinding;

import com.uhreckysw.balancer.ui.interfaces.ItemClickListenerLimits;

import java.util.ArrayList;
import java.util.Date;

public class LimitsView extends Fragment implements ItemClickListenerLimits {

    public static LimitsView newInstance() {
        return new LimitsView();
    }

    Database db;
    private LimitsViewBinding binding;
    Activity parentActivity;
    LimitListViewAdapter limitsListViewAdapter;
    RecyclerView limitsListView;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        db = Database.getInstance();

        parentActivity = getActivity();
        binding = DataBindingUtil.inflate(
                inflater, R.layout.limits_view, container, false);
        binding.setViewmodel(this);
        view = binding.getRoot();

        // set up the RecyclerView
        limitsListView = view.findViewById(R.id.item_list);
        LinearLayoutManager layout_manager = new LinearLayoutManager(parentActivity);
        limitsListView.setLayoutManager(layout_manager);
        limitsListViewAdapter = new LimitListViewAdapter(parentActivity, new ArrayList<LimitUIElem>());
        limitsListViewAdapter.setClickListener(this);
        limitsListView.setAdapter(limitsListViewAdapter);

        return view;
    }

    public void update() {
        view.setEnabled(false);
        limitsListViewAdapter.clear();
        for ( Limit limit : db.getAllLimits()) limitsListViewAdapter.add(new LimitUIElem(limit));
        limitsListViewAdapter.notifyDataSetChanged();
        limitsListView.smoothScrollToPosition(0);
        view.setEnabled(true);
    }

    @Override
    public void onItemClick(View view, int position) {
        LimitUIElem elem = limitsListViewAdapter.mData.get(position);
        elem.setUnpacked(!elem.getUnpacked());
    }

    @Override
    public void onSettingsButtonClick(View view, int position) {

    }

    @Override
    public void onDeleteButtonClick(View view, int position) {

    }

    public void onAddItem() {
    }

}
