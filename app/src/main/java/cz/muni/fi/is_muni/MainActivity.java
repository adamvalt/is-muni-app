package cz.muni.fi.is_muni;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;

/**
 * The main activity with the WebView displaying the content.
 * @author Adam Valt
 */
public class MainActivity extends Activity implements DefaultListener {
    private static final String PAGE_URL = BuildConfig.PAGE_URL;
    private AdvancedWebView mWebView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private static final String webViewStateKey = "webViewState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webView);
        if (savedInstanceState != null) {
            mWebView.restoreState(Objects.requireNonNull(savedInstanceState.getBundle(webViewStateKey)));
            return;
        }
        mWebView.setListener(this, this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(false); // ?
        mWebView.setCookiesEnabled(true);
        mWebView.setThirdPartyCookiesEnabled(true);
        mWebView.setWebViewClient(getCustomWebViewClient());
        mWebView.loadUrl(PAGE_URL);

        mySwipeRefreshLayout = this.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {
                    mWebView.reload();
                    mySwipeRefreshLayout.setRefreshing(false);
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.canGoBack()) { finish(); return; }
        mWebView.goBack();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mWebView.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Toast.makeText(getApplicationContext(), "Unexpected error occurred. Reload page again.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        String cookies = CookieManager.getInstance().getCookie(url);
        AdvancedWebView.DownloadManagerRequestConfigurator configuration = request -> {
            request.setMimeType(mimeType);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(suggestedFilename);
        };
        if(AdvancedWebView.handleDownload(this, url, suggestedFilename, configuration)){
            Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        mWebView.saveState(bundle);
        outState.putBundle(webViewStateKey, bundle);
    }
    @NonNull
    private WebViewClient getCustomWebViewClient(){
        return new WebViewClient() {
            // Fixing problem with mailto: and tel: prepended links not opening
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                    return false;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }
        };
    }


}