package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;

import androidx.databinding.BaseObservable;

import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;


public abstract class MyDialog extends BaseObservable {
    protected Database db;

    protected Activity parentActivity;
    protected IUpdatable parent;
    protected AlertDialog dialog;
    protected View dialogLayout;

    MyDialog(Activity parentActivity, IUpdatable parent) {
        db = Database.getInstance();
        this.parent = parent;
        this.parentActivity = parentActivity;
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        bind();
        builder.setView(dialogLayout);
        dialog = builder.create();
    }

    public void show() {
        update();
        dialog.show();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void cancel() {
        dialog.cancel();
    }

    abstract void update();
    abstract void bind();
}
