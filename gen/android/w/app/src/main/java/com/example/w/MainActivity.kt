package com.example.w

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WryWebView(this)


        val html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "   <head>\n" +
                "      <meta charset=\"utf-8\">\n" +
                "      <title>JSTest</title>  \n" +
                "      <script>\n" +
                "         function showToast(){\n" +
                "            window.ipc.postMessage(\"HelloWorld\");\n" +
                "         }\n" +
                "      </script>\n" +
                "   </head>\n" +
                "   <body>\n" +
                "      <button type=\"button\" id=\"button1\" onclick=\"showToast()\"></button>\n" +
                "   </body>\n" +
                "</html>"
        val encodedHtml = Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
        webView.loadData(encodedHtml, "text/html", "base64")

        setContentView(webView)
    }
}

@SuppressLint("SetJavaScriptEnabled")
class WryWebView(context: Context) : WebView(context) {
    companion object {
        init {
            System.loadLibrary("w")
        }
    }

    private external fun create(webView: WebView): Array<String>

    init {
        val scripts = create(this);
        this.webViewClient = WryClient(scripts);
        this.settings.javaScriptEnabled = true;
        this.addJavascriptInterface(IpcHandler(context), "ipc");

    }
}

class WryClient(private val initializationScripts: Array<String>) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false;
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        initializationScripts.forEach { view?.evaluateJavascript(it, null) };
    }
}

class IpcHandler(private val context: Context) {
    @JavascriptInterface
    fun postMessage(message: String) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}