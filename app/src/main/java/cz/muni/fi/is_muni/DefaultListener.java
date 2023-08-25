package cz.muni.fi.is_muni;

import android.graphics.Bitmap;

import im.delight.android.webview.AdvancedWebView;

/**
 * Intentionally empty default AdvancedWebView.Listener interface
 * @author Adam Valt
 */
public interface DefaultListener extends AdvancedWebView.Listener {
    @Override
    default void onPageStarted(String url, Bitmap favicon) { }
    @Override
    default void onPageFinished(String url) { }
    @Override
    default void onPageError(int errorCode, String description, String failingUrl) { }
    @Override
    default void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }
    @Override
    default void onExternalPageRequest(String url) { }
}
