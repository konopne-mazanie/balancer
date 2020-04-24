package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.DialogCategoryBinding;
import com.uhreckysw.balancer.ui.MyArrayAdapter;
import com.uhreckysw.balancer.ui.interfaces.ICategoryDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.PaymentUIElem;

import java.util.List;

public final class ChangeCategoryDialog extends MyDialog implements ICategoryDialog {
    private List<PaymentUIElem> items;
    final private Spinner categoryList;
    final private ArrayAdapter<String> categoryDataAdapter;
    private AddCategoryDialog addCategoryDialog;

    public void bind() {
        DialogCategoryBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_category, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }

    public ChangeCategoryDialog(Activity parentActivity, IUpdatable parent, List<PaymentUIElem> items) {
        super(parentActivity, parent);

        this.items = items;
        addCategoryDialog = new AddCategoryDialog(parentActivity, parent, this);
        categoryList = dialogLayout.findViewById(R.id.item_category);

        categoryDataAdapter = new MyArrayAdapter<String>(parentActivity,
                android.R.layout.simple_spinner_item, db.getAllCategories());
        categoryDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(categoryDataAdapter);
    }

    @Override
    void update() {
        //TODO handle update already created dialog
    }

    @Override
    public void setCategory(String category) {
        categoryDataAdapter.clear();
        categoryDataAdapter.addAll(db.getAllCategories());
        categoryDataAdapter.notifyDataSetChanged();
        categoryList.setSelection(categoryDataAdapter.getPosition(category));
    }

    public void onAddCategory() {
        addCategoryDialog.update();
        addCategoryDialog.show();
    }

    public void onCancel() {
        dialog.cancel();
    }

    public void onConfirm() {
        for (PaymentUIElem item : items) {
            item.payment.category = categoryList.getSelectedItem().toString();
            db.editPayment(item.payment);
        }
        parent.cancelSelection();
        parent.update();
        dialog.cancel();
    }

}
