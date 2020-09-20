package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

public final class EditPaymentDialog extends AddPaymentDialog {
    public EditPaymentDialog(Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);
        ((TextView) dialogLayout.findViewById(R.id.dialog_title)).setText(parentActivity.getString(R.string.edit_payment));
    }

    @Override
    public void onConfirm() {
        if (validate()) {
            try {
            db.editPayment(new Payment()
                    .setItem(itemNameField.getText().toString())
                    .setDate_of_buy(DateCommon.parseDateGUI(getItemDateFieldText()))
                    .setPrice(((float) Math.round(Float.parseFloat(getItemPriceFieldText().replace(",", "."))*100))/100)
                    .setDescription(getItemDescriptionFieldText())
                    .setCategory(categoryList.getSelectedItem().toString())
                    .setId(clickedItem.payment.id)
                    .setReceipt(clickedItem.payment.receipt));
            } catch (Exception e) {
                ((EditText) dialogLayout.findViewById(R.id.item_payed_date)).setError(parentActivity.getString(R.string.required_field));
                return;
            }
            parent.update();
            dialog.cancel();
        }
    }

}
