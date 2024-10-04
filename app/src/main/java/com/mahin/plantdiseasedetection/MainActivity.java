package com.mahin.plantdiseasedetection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    WebView myWeb;
    private ValueCallback<Uri[]> filePathCallback;
    ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize WebView
        myWeb = findViewById(R.id.myDiseaseDetection);
        WebSettings settings = myWeb.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        myWeb.setWebViewClient(new WebViewClient());

        // Set WebChromeClient to handle file inputs
        myWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (MainActivity.this.filePathCallback != null) {
                    MainActivity.this.filePathCallback.onReceiveValue(null);
                }
                MainActivity.this.filePathCallback = filePathCallback;

                // Launch image picker
                openImagePicker();
                return true;
            }
        });

        myWeb.loadUrl("https://mahinpdd.streamlit.app/");

        // Set up ActivityResultLauncher for picking images
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (filePathCallback != null) {
                        Uri[] results = null;
                        if (result.getResultCode() == RESULT_OK) {
                            if (result.getData() != null) {
                                Uri data = result.getData().getData();
                                if (data != null) {
                                    results = new Uri[]{data};
                                }
                            }
                        }
                        filePathCallback.onReceiveValue(results);
                        filePathCallback = null;
                    }
                }
        );
    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }
}
