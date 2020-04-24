package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.PaymentUIElem;

import java.util.List;

public final class DeleteItemDialog extends AskDialog {
    private List<PaymentUIElem> items;

    public DeleteItemDialog(Activity parentActivity, IUpdatable parent, List<PaymentUIElem> items) {
        super(parentActivity, parent);
        this.items = items;
        if (items.size() > 1) dialogText = parentActivity.getString(R.string.delete_msg_rlly) + " " + items.size() + " " + parentActivity.getString(R.string.selected_payments);
        else dialogText = parentActivity.getString(R.string.delete_msg_rlly) + " " + items.get(0).getItem() + "?";
        dialogTitle = parentActivity.getString(R.string.delete_payment);
    }

    @Override
    public void onCancel() {
        dialog.cancel();
    }

    @Override
    public void onConfirm() {
        for (PaymentUIElem item : items) db.deletePayment(item.payment.id);
        parent.cancelSelection();
        parent.update();
        dialog.cancel();
    }

}
