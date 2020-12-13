package com.uhreckysw.balancer.ui.settings_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.BR;
import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.databinding.ActivitySettingsBinding;
import com.uhreckysw.balancer.ui.dialog.AskDialog;
import com.uhreckysw.balancer.ui.dialog.ErrDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements IUpdatable {

    private static final int CREATE_DOCUMENT_REQUEST_NR = 201;
    private static final int OPEN_DOCUMENT_REQUEST_NR = 202;

    private ConfirmRestoreDialog confirmRestoreDialog;
    public Observable observable = new Observable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        confirmRestoreDialog = new ConfirmRestoreDialog(SettingsActivity.this, SettingsActivity.this);
        ((ActivitySettingsBinding) (DataBindingUtil.setContentView(this, R.layout.activity_settings))).setViewmodel(this);
    }

    public void onBackup() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "db.sqlite");
        startActivityForResult(intent, CREATE_DOCUMENT_REQUEST_NR);
    }

    public void onRestore() {
        confirmRestoreDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (confirmRestoreDialog.isShowing()) confirmRestoreDialog.cancel();
        if (
                (resultCode != Activity.RESULT_OK)
                || (requestCode < CREATE_DOCUMENT_REQUEST_NR)
                || (requestCode > OPEN_DOCUMENT_REQUEST_NR)
        ) return;

        observable.setInProgress(true);
        try {
            if (requestCode == CREATE_DOCUMENT_REQUEST_NR) Database.getInstance().backup(getContentResolver(), data.getData());
            else Database.getInstance().restore(getContentResolver(), data.getData());
        } catch (Exception e) {
            new ErrDialog(SettingsActivity.this, SettingsActivity.this, getString(R.string.fileoperror)).show();
        }
        observable.setInProgress(false);
    }

    public final class ConfirmRestoreDialog extends AskDialog {

        public ConfirmRestoreDialog(Activity parentActivity, IUpdatable parent) {
            super(parentActivity, parent);
            dialogText =  parentActivity.getString(R.string.restore_msg_rlly);
            dialogTitle = parentActivity.getString(R.string.restore);
    }

        @Override
        public void onCancel() {
            dialog.cancel();
        }

        @Override
        public void onConfirm() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_NR);
        }
    }

    @Override
    public void update() { }

    @Override
    public void cancelSelection() { }

    @Override
    public void setFilter(Filter f) { }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return (observable.getInProgress() || super.dispatchTouchEvent(ev));
    }

    @Override
    public void onBackPressed() {
        if (!observable.getInProgress()) super.onBackPressed();
    }

    // ###########################################################################################

    public class Observable extends BaseObservable {
        private boolean inProgress;
        @Bindable
        public boolean getInProgress() {
            return inProgress;
        }

        public void setInProgress (boolean inProgress) {
            this.inProgress = inProgress;
            notifyPropertyChanged(BR.inProgress);
        }

        public Observable() {
            setInProgress(false);
        }
    }

}
