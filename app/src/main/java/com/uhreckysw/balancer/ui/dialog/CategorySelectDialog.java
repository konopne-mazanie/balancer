package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.DialogFilterCategorySelectBinding;
import com.uhreckysw.balancer.ui.interfaces.IFiltersDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategorySelectDialog extends MyDialog implements ItemClickListener {
    private CategorySelectDialogListViewAdapter categorySelectDialogListViewAdapter;
    private IFiltersDialog filtersDialog;

    @Override
    public void bind() {
        DialogFilterCategorySelectBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_filter_category_select, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }

    public CategorySelectDialog(Activity parentActivity, IUpdatable parent, IFiltersDialog filtersDialog) {
        super(parentActivity, parent);
        this.filtersDialog = filtersDialog;
        RecyclerView categoryListView = dialogLayout.findViewById(R.id.item_list);
        categoryListView.setLayoutManager(new LinearLayoutManager(parentActivity));
        categorySelectDialogListViewAdapter = new CategorySelectDialogListViewAdapter(parentActivity, new ArrayList<CategorySelectDialogUiElem>());
        categorySelectDialogListViewAdapter.setClickListener(this);
        categoryListView.setAdapter(categorySelectDialogListViewAdapter);
    }


    public void onSelectAll() {
        for (CategorySelectDialogUiElem elem : categorySelectDialogListViewAdapter.mData) elem.setChecked(true);
    }

    public void onConfirm() {
        String category = "";
        for (CategorySelectDialogUiElem elem : categorySelectDialogListViewAdapter.mData) {
            if (elem.getChecked()) category+= "'" + elem.getName() + "'" + ", ";
        }
        if (!category.isEmpty()) category = category.substring(0, category.length() - 2);
        filtersDialog.setCategory(category);
        dialog.cancel();
    }

    public void onClear() {
        for (CategorySelectDialogUiElem elem : categorySelectDialogListViewAdapter.mData) elem.setChecked(false);
    }

    @Override
    void update() {
        List<String> currentCategory = Arrays.asList(filtersDialog.getCategory().replace("'", "")
                                                                        .replace(" ", "")
                                                                        .split(","));
        categorySelectDialogListViewAdapter.mData.clear();
        for (String category : currentCategory) {
            if (category.isEmpty()) continue;
            CategorySelectDialogUiElem newElem = new CategorySelectDialogUiElem(category);
            categorySelectDialogListViewAdapter.mData.add(newElem);
            newElem.setChecked(true);
        }
        for (String category : db.getAllCategories()) {
            if (!currentCategory.contains(category)) categorySelectDialogListViewAdapter.mData.add(new CategorySelectDialogUiElem(category));
        }
        categorySelectDialogListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        CategorySelectDialogUiElem elem = categorySelectDialogListViewAdapter.mData.get(position);
        elem.setChecked(!elem.getChecked());
    }

    @Override
    public void onItemLongClick(View view, int position) { }

    @Override
    public void onSettingsButtonClick(View view, int position) {}

    @Override
    public void onCopyButtonClick(View view, int position) {}
}
