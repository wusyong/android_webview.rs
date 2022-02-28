package com.example.w

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.webkit.*
import android.widget.Toast
import androidx.webkit.WebViewAssetLoader

class MainActivity : Activity() {
    private lateinit var webView: WebView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WryWebView(this)

//        val html = "<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "   <head>\n" +
//                "      <meta charset=\"utf-8\">\n" +
//                "      <title>JSTest</title>  \n" +
//                "      <script>\n" +
//                "         function showToast(){\n" +
//                "            window.ipc.postMessage(\"HelloWorld\");\n" +
//                "         }\n" +
//                "      </script>\n" +
//                "   </head>\n" +
//                "   <body>\n" +
//                "      <button type=\"button\" id=\"button1\" onclick=\"showToast()\"></button>\n" +
//                "   </body>\n" +
//                "</html>"
//        val encodedHtml = Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
//        webView.loadData(encodedHtml, "text/html", "base64")

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
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(context))
            .build();
        this.webViewClient = WryClient(scripts, assetLoader);
        this.settings.javaScriptEnabled = true;
        this.addJavascriptInterface(IpcHandler(context), "ipc");

    }
}

class WryClient(private val initializationScripts: Array<String>, private val assetLoader: WebViewAssetLoader) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false;
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        initializationScripts.forEach { view?.evaluateJavascript(it, null) };
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return assetLoader.shouldInterceptRequest(request.url)
    }
}

class IpcHandler(private val context: Context) {
    @JavascriptInterface
    fun postMessage(message: String) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}