package de.sonothar.tweet.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.sonothar.tweet.R;

public class TwitterWebview extends Activity {

	private Intent mIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mIntent = getIntent();
		String url = (String) mIntent.getExtras().get("URL");
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains(getResources().getString(
						R.string.twitter_callback))) {
					Uri uri = Uri.parse(url);
					String oauthVerifier = uri
							.getQueryParameter("oauth_verifier");
					mIntent.putExtra("oauth_verifier", oauthVerifier);
					setResult(RESULT_OK, mIntent);
					finish();
					return true;
				}
				return false;
			}
		});
		webView.loadUrl(url);
	}
}
