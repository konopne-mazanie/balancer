package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.BR;
import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.databinding.DialogAddBinding;
import com.uhreckysw.balancer.ui.MyArrayAdapter;
import com.uhreckysw.balancer.ui.interfaces.ICategoryDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.PaymentUIElem;

import java.util.Date;
import java.util.regex.Pattern;

public class AddPaymentDialog extends MyDialog implements ICategoryDialog {
    protected PaymentUIElem clickedItem;
    private String itemDateFieldText;
    private String itemPriceFieldText;
    private String itemDescriptionFieldText;

    final protected EditText itemNameField;
    final protected Spinner categoryList;
    final protected ArrayAdapter<String> categoryDataAdapter;

    private AddCategoryDialog addCategoryDialog;

    public void bind() {
        DialogAddBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_add, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }

    public AddPaymentDialog(final Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);
        addCategoryDialog = new AddCategoryDialog(parentActivity, parent, this);

        itemNameField = dialogLayout.findViewById(R.id.item_name);
        categoryList = dialogLayout.findViewById(R.id.item_category);

        categoryDataAdapter = new MyArrayAdapter<String>(parentActivity,
                android.R.layout.simple_spinner_item, db.getAllCategories());
        categoryDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(categoryDataAdapter);

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
        setItemDescriptionFieldText("");
    }

    @Override
    void update() {
        categoryDataAdapter.clear();
        categoryDataAdapter.addAll(db.getAllCategories());
        categoryDataAdapter.notifyDataSetChanged();
        if (categoryDataAdapter.getCount() == 0) categoryDataAdapter.add("");
        categoryList.setSelection(0);
        itemNameField.setText("");
        setItemPriceFieldText("");
        setItemDateFieldText(DateCommon.dateFormatGUI.format(new Date()));
        setItemDescriptionFieldText("");
        resetValidation();
    }

    @Override
    public void setCategory(String category) {
        categoryDataAdapter.clear();
        categoryDataAdapter.addAll(db.getAllCategories());
        categoryDataAdapter.notifyDataSetChanged();
        categoryList.setSelection(categoryDataAdapter.getPosition(category));
    }

    public void setClickedItem(PaymentUIElem clickedItem) {
        this.clickedItem = clickedItem;
        itemNameField.setText(clickedItem.getItem());
        setItemPriceFieldText(clickedItem.getPrice());
        setCategory(clickedItem.payment.category);
        setItemDateFieldText(DateCommon.dateFormatGUI.format(clickedItem.payment.date_of_buy));
        setItemDescriptionFieldText(clickedItem.payment.description);
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
        ((EditText) dialogLayout.findViewById(R.id.item_payed_date)).setError(validator(getItemDateFieldText().isEmpty(), error));
        ((EditText) dialogLayout.findViewById(R.id.item_price)).setError(validator(getItemPriceFieldText().isEmpty(), error));
        ((TextView) categoryList.getSelectedView()).setError(validator(categoryList.getSelectedItem().toString().isEmpty(), error));
        return (!error[0] && (itemNameField.getError() == null));
    }
    void resetValidation() {
        itemNameField.setError(null);
        ((EditText) dialogLayout.findViewById(R.id.item_payed_date)).setError(null);
        ((EditText) dialogLayout.findViewById(R.id.item_price)).setError(null);
    }

    public void onConfirm() {
        if (validate()) {
            try {
                db.createPayment(
                        itemNameField.getText().toString(),
                        ((float) Math.round(Float.parseFloat(getItemPriceFieldText().replace(",", ".")) * 100)) / 100,
                        DateCommon.parseDateGUI(getItemDateFieldText()),
                        categoryList.getSelectedItem().toString(),
                        getItemDescriptionFieldText(),
                        ((clickedItem == null) || (clickedItem.payment.receipt == null)) ? "" : clickedItem.payment.receipt.id
                );
            } catch (Exception e) {
                ((EditText) dialogLayout.findViewById(R.id.item_payed_date)).setError(parentActivity.getString(R.string.required_field));
                return;
            }
            parent.update();
            dialog.cancel();
        }
    }

    public void onAddCategory() {
        addCategoryDialog.update();
        addCategoryDialog.show();
    }

    public void onDatePick() {
        try {
            DateCommon.pickDate(DateCommon.dateFormatGUI.parse(getItemDateFieldText()), (String s) -> setItemDateFieldText(s), parentActivity);
        } catch (Exception e) {}
    }

    @Bindable
    public String getItemDateFieldText() {
        return itemDateFieldText;
    }

    public void setItemDateFieldText(String value) {
        this.itemDateFieldText = value;
        notifyPropertyChanged(BR.itemDateFieldText);
    }

    @Bindable
    public String getItemPriceFieldText() {
        return itemPriceFieldText;
    }

    public void setItemPriceFieldText(String value) {
        this.itemPriceFieldText = value;
        notifyPropertyChanged(BR.itemPriceFieldText);
    }

    @Bindable
    public String getItemDescriptionFieldText() {
        return itemDescriptionFieldText;
    }

    public void setItemDescriptionFieldText(String value) {
        this.itemDescriptionFieldText = value;
        notifyPropertyChanged(BR.itemDescriptionFieldText);
    }
}
