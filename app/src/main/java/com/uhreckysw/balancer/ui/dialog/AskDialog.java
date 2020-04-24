package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;

import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.DialogAskBinding;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

public abstract class AskDialog extends MyDialog {
    public String dialogText;
    public String dialogTitle;

    public void bind() {
        DialogAskBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_ask, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }
    public AskDialog(Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);
    }

    @Override
    void update() {}

    public abstract void onCancel();

    public abstract void onConfirm();

}
