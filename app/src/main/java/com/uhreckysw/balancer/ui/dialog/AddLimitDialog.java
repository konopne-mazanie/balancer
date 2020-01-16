package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Limit;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.databinding.DialogAddBinding;
import com.uhreckysw.balancer.databinding.DialogAddLimitBinding;
import com.uhreckysw.balancer.ui.MyArrayAdapter;
import com.uhreckysw.balancer.ui.interfaces.ICategoryDialog;
import com.uhreckysw.balancer.ui.interfaces.IFiltersDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.PaymentUIElem;

import java.util.Date;
import java.util.regex.Pattern;

public class AddLimitDialog extends MyDialog implements IFiltersDialog {
    private String itemValue;
    private String itemDays;
    private String category;

    final protected EditText itemNameField;

    private CategorySelectDialog categorySelectDialog;


    public void bind() {
        DialogAddLimitBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_add_limit, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }

    public AddLimitDialog(final Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);

        itemNameField = dialogLayout.findViewById(R.id.item_name);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                final Activity par = parentActivity;
                if (!Pattern.compile("^[\\w|\\s]*$").matcher(itemNameField.getText()).find())
                    itemNameField.setError(parentActivity.getString(R.string.err_wrong_item_name));
            }
        };
        itemNameField.addTextChangedListener(afterTextChangedListener);
        categorySelectDialog = new CategorySelectDialog(parentActivity, parent, this);
    }

    @Override
    void update() {
        itemNameField.setText("");
        setCategory("");
        setItemDays("");
        setItemValue("");
    }

    public void onCancel() {
        dialog.cancel();
    }

    public void onConfirm() {
        if ((!itemNameField.getText().toString().isEmpty()) &&
                (!getCategory().isEmpty()) &&
                (!getItemValue().isEmpty()) &&
                (!getItemDays().isEmpty()) &&
                (!itemNameField.getText().toString().isEmpty()) &&
                (itemNameField.getError() == null)) {
            db.createLimit(new Limit()
                    .setName(itemNameField.getText().toString())
                    .setCategory(getCategory())
                    .setDays(Integer.parseInt(getItemDays()))
                    .setValue(((float) Math.round(Float.parseFloat(getItemValue().replace(",", ".")) * 100)) / 100));

            parent.update();
            dialog.cancel();
        }
    }

    public void onSelectCategory() {
        categorySelectDialog.show();
    }

    @Bindable
    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String value) {
        this.itemValue = value;
        notifyPropertyChanged(BR.itemValue);
    }

    @Bindable
    public String getItemDays() {
        return itemDays;
    }

    public void setItemDays(String value) {
        this.itemDays = value;
        notifyPropertyChanged(BR.itemDays);
    }

    @Bindable
    public String getCategory() {
        return category;
    }

    public void setCategory(String value) {
        this.category = value;
        notifyPropertyChanged(BR.category);
    }
}
