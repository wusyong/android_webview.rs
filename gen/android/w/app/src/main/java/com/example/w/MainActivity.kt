package com.example.w

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView

class MainActivity : Activity() {
    private lateinit var webView: WebView;

    companion object {
        init {
            System.loadLibrary("w")
        }
    }

    private external fun create(webView: WebView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)
        create(webView)
    }
}