package com.uhreckysw.balancer.ui.scan_activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.Receipt.ReceiptAPI;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.databinding.ActivityScanBinding;
import com.uhreckysw.balancer.ui.dialog.ErrDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

import org.json.JSONException;

public class ScanActivity extends AppCompatActivity implements IUpdatable {
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private String lastScanned = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityScanBinding) (DataBindingUtil.setContentView(this, R.layout.activity_scan))).setViewmodel(this);
        surfaceView = findViewById(R.id.surfaceView);
    }

    private void initialiseDetectorsAndSources() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920,1080)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {startCamera();}

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { cameraSource.stop(); }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    String receiptId = barcodes.valueAt(0).displayValue;
                    if (receiptId.equals(lastScanned)) return;
                    lastScanned = receiptId;
                    ScanActivity.this.runOnUiThread(() -> {
                        surfaceView.setForeground(ContextCompat.getDrawable(ScanActivity.this, R.drawable.scan_surface_foreg_found));
                        cameraSource.stop();
                    });
                    try {
                        ReceiptAPI.createPayment(ReceiptAPI.post(receiptId));
                        ScanActivity.this.runOnUiThread(() -> {
                            setResult(1, new Intent().putExtra("receiptId", receiptId));
                            finish();
                        });
                    } catch (Exception exception) {
                        ScanActivity.this.runOnUiThread(() -> {
                            if (exception instanceof IOException)
                                new ErrDialog(ScanActivity.this, ScanActivity.this, getString(R.string.internet_error)).show();
                            else if (exception instanceof JSONException)
                                new ErrDialog(ScanActivity.this, ScanActivity.this, getString(R.string.qr_format_error)).show();
                            surfaceView.setForeground(ContextCompat.getDrawable(ScanActivity.this, R.drawable.scan_surface_foreg));
                            startCamera();
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (
                (requestCode != 201)
                || (grantResults.length == 0)
                || (grantResults[0] != PackageManager.PERMISSION_GRANTED)
        ) return;
        initialiseDetectorsAndSources();
        startCamera();
    }

    private void startCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, 201);
                return;
            }
            cameraSource.start(surfaceView.getHolder());
        } catch (IOException e) {
            new ErrDialog(ScanActivity.this, ScanActivity.this, getString(R.string.camera_error)).show();
        }
    }

    @Override
    public void update() { }

    @Override
    public void cancelSelection() { }

    @Override
    public void setFilter(Filter f) { }
}
