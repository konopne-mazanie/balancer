package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.widget.TextView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

public final class EditPaymentDialog extends AddPaymentDialog {
    public EditPaymentDialog(Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);
    }

    @Override
    void update() {
        ((TextView) dialogLayout.findViewById(R.id.dialog_title)).setText(parentActivity.getString(R.string.edit_payment));
    }

    @Override
    public void onConfirm() {
        if ((!itemNameField.getText().toString().isEmpty()) &&
                (!getItemDateFieldText().isEmpty()) &&
                (!getItemPriceFieldText().isEmpty()) &&
                (itemNameField.getError() == null)) {
            try {
            db.editPayment(new Payment()
                    .setItem(itemNameField.getText().toString())
                    .setDate_of_buy(DateCommon.parseDateGUI(getItemDateFieldText()))
                    .setPrice(((float) Math.round(Float.parseFloat(getItemPriceFieldText().replace(",", "."))*100))/100)
                    .setId(0)
                    .setDescription(getItemDescriptionFieldText())
                    .setCategory(categoryList.getSelectedItem().toString())
                    .setId(clickedItem.payment.id));
            } catch (Exception e) {
                return;
            }
            parent.update();
            dialog.cancel();
        }
    }

}
