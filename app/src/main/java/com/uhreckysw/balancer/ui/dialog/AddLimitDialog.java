package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.BR;
import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.db.Limit;
import com.uhreckysw.balancer.databinding.DialogAddLimitBinding;
import com.uhreckysw.balancer.ui.interfaces.IFiltersDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

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
                if (!Pattern.compile("^[\\w\\s()-]*$").matcher(itemNameField.getText()).find())
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
        resetValidation();
    }

    public void onCancel() {
        dialog.cancel();
    }

    String validator(boolean cond, boolean[] error) {
        if (cond) {
            error[0] = true;
            return parentActivity.getString(R.string.required_field);
        }
        return null;
    }
    boolean validate() {
        boolean[] error = {false};
        itemNameField.setError(validator(itemNameField.getText().toString().isEmpty(), error));
        ((EditText) dialogLayout.findViewById(R.id.item_value)).setError(validator(getItemValue().isEmpty(), error));
        ((EditText) dialogLayout.findViewById(R.id.item_days)).setError(validator(getItemDays().isEmpty(), error));
        ((Button) dialogLayout.findViewById(R.id.item_category)).setError(validator(getCategory().isEmpty(), error));
        return (!error[0] && (itemNameField.getError() == null));
    }
    void resetValidation() {
        itemNameField.setError(null);
        ((EditText) dialogLayout.findViewById(R.id.item_value)).setError(null);
        ((EditText) dialogLayout.findViewById(R.id.item_days)).setError(null);
        ((Button) dialogLayout.findViewById(R.id.item_category)).setError(null);
    }

    public void onConfirm() {
        if (validate()) {
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
        ((Button) dialogLayout.findViewById(R.id.item_category)).setError(validator(getCategory().isEmpty(), new boolean[] {false}));
    }
}
