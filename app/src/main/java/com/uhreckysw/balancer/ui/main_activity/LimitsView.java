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
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.databinding.LimitsViewBinding;
import com.uhreckysw.balancer.ui.dialog.AddLimitDialog;
import com.uhreckysw.balancer.ui.dialog.DeleteLimitDialog;
import com.uhreckysw.balancer.ui.dialog.EditLimitDialog;
import com.uhreckysw.balancer.ui.dialog.ShowLimitDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListenerLimits;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LimitsView extends Fragment implements ItemClickListenerLimits, IUpdatable {

    Database db;
    private LimitsViewBinding binding;
    public Activity parentActivity;
    LimitListViewAdapter limitsListViewAdapter;
    RecyclerView limitsListView;
    View view;

    AddLimitDialog addLimitDialog;
    EditLimitDialog editLimitDialog;
    ShowLimitDialog showLimitDialog;

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

        addLimitDialog = new AddLimitDialog(parentActivity, this);
        editLimitDialog = new EditLimitDialog(parentActivity, this);
        showLimitDialog = new ShowLimitDialog(parentActivity, this);

        update();
        return view;
    }

    public void update() {
        if (view == null) return;
        new Thread(() -> {
            parentActivity.runOnUiThread(() -> view.setEnabled(false));

            limitsListViewAdapter.mData = db.getAllLimits().stream().map(LimitUIElem::new).collect(Collectors.toList());

            parentActivity.runOnUiThread(() -> {
                limitsListViewAdapter.notifyDataSetChanged();
                limitsListView.smoothScrollToPosition(0);
                view.setEnabled(true);
            });
        }).start();
    }

    @Override
    public void onItemClick(View view, int position) {
        showLimitDialog.show();
        showLimitDialog.setClickedItem(limitsListViewAdapter.mData.get(position));
        showLimitDialog.update();
    }

    @Override
    public void onSettingsButtonClick(View view, int position) {
        editLimitDialog.show();
        editLimitDialog.setClickedItem(limitsListViewAdapter.mData.get(position));
    }

    @Override
    public void onDeleteButtonClick(View view, int position) {
        new DeleteLimitDialog(parentActivity, this, limitsListViewAdapter.mData.get(position)).show();
    }

    public void onAddItem() {
        addLimitDialog.show();
    }

     @Override
     public void cancelSelection() {}
     @Override
    public void setFilter(Filter f){}

}
