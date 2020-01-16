package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.database.sqlite.SQLiteConstraintException;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.DialogCategoryAddBinding;
import com.uhreckysw.balancer.ui.interfaces.ICategoryDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

import java.util.regex.Pattern;

public final class AddCategoryDialog extends MyDialog {
    final private EditText categoryName;
    final private MyDialog parentDialog;

    public void bind() {
        DialogCategoryAddBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_category_add, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }

    public AddCategoryDialog(final Activity parentActivity, IUpdatable parent, MyDialog parentDialog) {
        super(parentActivity, parent);

        this.parentDialog = parentDialog;
        categoryName = dialogLayout.findViewById(R.id.category_name);

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
                if (!Pattern.compile("^[\\w|\\s]*$").matcher(categoryName.getText()).find())
                    categoryName.setError(parentActivity.getString(R.string.err_wrong_item_name));
            }
        };
        categoryName.addTextChangedListener(afterTextChangedListener);
    }

    @Override
    void update() {
        categoryName.setText("");
    }

    public void onCancel() {
        dialog.cancel();
    }

    public void onConfirm() {
        if (!(categoryName.getText().toString().isEmpty() || categoryName.getError() != null)) {
            try {
                db.createCategory(categoryName.getText().toString());
                ((ICategoryDialog) parentDialog).setCategory(categoryName.getText().toString());
                dialog.cancel();
            } catch (SQLiteConstraintException e) {
                categoryName.setError(parentActivity.getString(R.string.cat_exists));
            }
        }
    }

}
