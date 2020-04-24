package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.widget.TextView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.db.Limit;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.LimitUIElem;

public final class EditLimitDialog extends AddLimitDialog {
    private LimitUIElem clickedItem;

    public EditLimitDialog(Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);
        ((TextView) dialogLayout.findViewById(R.id.dialog_title)).setText(parentActivity.getString(R.string.edit_limit));
    }

    @Override
    public void onConfirm() {
        if (validate()) {
            db.editLimit(new Limit()
                    .setId(clickedItem.limit.id)
                    .setName(itemNameField.getText().toString())
                    .setCategory(getCategory())
                    .setDays(Integer.parseInt(getItemDays()))
                    .setValue(((float) Math.round(Float.parseFloat(getItemValue().replace(",", ".")) * 100)) / 100));

            parent.update();
            dialog.cancel();
        }
    }

    public void setClickedItem(LimitUIElem clickedItem) {
        this.clickedItem = clickedItem;
        setCategory(clickedItem.limit.category);
        setItemDays(String.valueOf(clickedItem.limit.days));
        setItemValue(String.valueOf(clickedItem.limit.value));
        itemNameField.setText(clickedItem.limit.name);
    }

}