package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.LimitUIElem;

public final class DeleteLimitDialog extends AskDialog {
    private LimitUIElem item;


    public DeleteLimitDialog(Activity parentActivity, IUpdatable parent, LimitUIElem item) {
        super(parentActivity, parent);
        this.item = item;
        dialogText = parentActivity.getString(R.string.delete_msg_rlly) + " " + item.getName() + "?";
        dialogTitle = parentActivity.getString(R.string.delete_limit);
    }

    @Override
    public void onCancel() {
        dialog.cancel();
    }

    @Override
    public void onConfirm() {
        db.deleteLimit(item.limit.id);
        parent.update();
        dialog.cancel();
    }

}
