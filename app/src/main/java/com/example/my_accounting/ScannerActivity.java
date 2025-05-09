package com.example.my_accounting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.SupabaseHandler;
import Model.Goods;

public class ScannerActivity extends BaseActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private SurfaceView cameraPreview;
    private CameraSource cameraSource;
    private TextView barcodeResult;
    private boolean returnBarcode = false;

    TextView title_good;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        setMainTitle(SCANNER);
        setWarehouseTitle();

        cameraPreview = findViewById(R.id.camera_preview);
        barcodeResult = findViewById(R.id.barcode_result);

        title_good = findViewById(R.id.title_good);
        Button more_info = findViewById(R.id.button_more_info);
        more_info.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null && "getBarcode".equals(intent.getStringExtra("action"))) {
            more_info.setVisibility(View.GONE);
            title_good.setVisibility(View.GONE);
            returnBarcode = true;
        }

        startCamera();
    }

    private void startCamera() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (ContextCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeResult.post(() -> {
                        String barcodeValue = barcodes.valueAt(0).displayValue;
                        if (returnBarcode) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("barcode", barcodeValue);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            // Обычное поведение
                            barcodeResult.setText(barcodeValue);
                            getGood(barcodeValue, new GoodsCallback() {
                                @Override
                                public void onGoodReceived(Goods good) {
                                    if (good != null) {
                                        title_good.setText(good.getTitle());
                                    } else {
                                        title_good.setText("Товара нет в базе");
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public interface GoodsCallback {
        void onGoodReceived(Goods good);
    }
    public void getGood(String scannedBarcode, GoodsCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Goods good;
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                good = supabaseHandler.getGoodByBarcode(this, scannedBarcode);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            handler.post(() -> callback.onGoodReceived(good));
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ScanActivity", "Camera permission not granted");
            }
        }
    }
}

