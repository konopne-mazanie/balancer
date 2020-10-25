package com.uhreckysw.balancer.ui.settings_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.databinding.ActivitySettingsBinding;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private static final int CREATE_DOCUMENT_REQUEST_NR = 201;
    private static final int OPEN_DOCUMENT_REQUEST_NR = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_NR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (
                (resultCode != Activity.RESULT_OK)
                || (requestCode < CREATE_DOCUMENT_REQUEST_NR)
                || (requestCode > OPEN_DOCUMENT_REQUEST_NR)
        ) return;

        try {
            if (requestCode == CREATE_DOCUMENT_REQUEST_NR) Database.getInstance().backup(getContentResolver(), data.getData());
            else Database.getInstance().restore(getContentResolver(), data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
