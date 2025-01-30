package com.solver.callbutton;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class YoutubeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        // Find the WebView by ID
        WebView webView = findViewById(R.id.webview);

        // Enable JavaScript for the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);  // Enable DOM storage for better performance
        webSettings.setAllowFileAccess(true);
        // Allow access to files (if needed)

        // Set a WebViewClient to prevent opening a browser
        webView.setWebViewClient(new WebViewClient());

        // YouTube embed URL with autoplay and mute parameters
        String videoUrl = "https://www.youtube.com/embed/7PIji8OubXU?autoplay=1&mute=1&modestbranding=1&showinfo=0&controls=0";

        // Construct the HTML to load in the WebView
        String videoHtml = "<html><body>" +
                "<iframe width=\"100%\" height=\"100%\" " +
                "src=\"" + videoUrl + "\" " +
                "frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe>" +
                "</body></html>";

        // Load the HTML content with the YouTube video
        webView.loadData(videoHtml, "text/html", "utf-8");

    }
}