package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;

import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.DialogAskBinding;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.PaymentUIElem;

import java.util.ArrayList;

public final class DeleteItemDialog extends MyDialog {
    public String dialogText;
    private ArrayList<PaymentUIElem> items;

    public void bind() {
        DialogAskBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_ask, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }
    public DeleteItemDialog(Activity parentActivity, IUpdatable parent, ArrayList<PaymentUIElem> items) {
        super(parentActivity, parent);
        this.items = items;
        if (items.size() > 1) dialogText = parentActivity.getString(R.string.delete_msg_rlly) + " " + items.size() + " " + parentActivity.getString(R.string.selected_payments);
        else dialogText = parentActivity.getString(R.string.delete_msg_rlly) + " " + items.get(0).getItem() + "?";
    }

    @Override
    void update() {
        //TODO handle update already created dialog
    }

    public void onCancel() {
        dialog.cancel();
    }

    public void onConfirm() {
        for (PaymentUIElem item : items) db.deletePayment(item.payment.id);
        parent.cancelSelection();
        parent.update();
        dialog.cancel();
    }


}
