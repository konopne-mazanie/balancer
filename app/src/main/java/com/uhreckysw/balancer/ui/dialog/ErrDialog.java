package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;

import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.DialogErrBinding;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

public class ErrDialog extends MyDialog {
    public String dialogText;

    public void bind() {
        DialogErrBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_err, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }
    public ErrDialog(Activity parentActivity, IUpdatable parent, String text) {
        super(parentActivity, parent);
        dialogText = text;
    }

    public void onConfirm() {
        dialog.cancel();
    }

    @Override
    void update() {}

}
