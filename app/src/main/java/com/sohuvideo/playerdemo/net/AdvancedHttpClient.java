package com.sohuvideo.playerdemo.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

public class AdvancedHttpClient extends DefaultHttpClient {
	private static final String TAG = "AdvancedHttpClient";

	private static final int TIMEOUT = 3; // 3秒

	public AdvancedHttpClient(ClientConnectionManager ccm, HttpParams params) {
		super(ccm, params);
	}

	private AdvancedHttpClient(HttpParams params) {
		super(params);
	}

	/**
	 * Create the default HTTP protocol parameters.
	 */
	public static final HttpParams createDefaultHttpParams() {
		final HttpParams params = new BasicHttpParams();
		// Turn off stale checking. Our connections break
		// all the time anyway,
		// and it's not worth it to pay the penalty of
		// checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192 << 1);

		return params;
	}

	public static AdvancedHttpClient createDefaultClient(
            Context context) {
        HttpParams params = createDefaultHttpParams();
        String[] currentProxy = Connectivity.getInstance(context)
                .getCurrentProxy();
        if (currentProxy != null) { // 加入代理
			String proxy = currentProxy[0];
			int port = Integer.valueOf(currentProxy[1]);
			Log.d("AdvancedHttpClient", "found proxy, proxy: " + proxy + " port:" + port);
			if(!TextUtils.isEmpty(proxy) && port != 0){
				HttpHost host = new HttpHost(proxy, port);
				params.setParameter(ConnRouteParams.DEFAULT_PROXY, host);
			}
        } 
        
        AdvancedHttpClient client = new AdvancedHttpClient(params);
        return client;
    }

	public static AdvancedHttpClient createSSLHttpClient(Context context) {
		HttpParams params = createDefaultHttpParams();
		String[] currentProxy = Connectivity.getInstance(context)
				.getCurrentProxy();
		if (currentProxy != null) { // 加入代理
			String proxy = currentProxy[0];
			int prot = Integer.valueOf(currentProxy[1]);
			HttpHost host = new HttpHost(proxy, prot);
			params.setParameter(ConnRouteParams.DEFAULT_PROXY, host);
		} else {
			params.removeParameter(ConnRouteParams.DEFAULT_PROXY);
		}
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			AdvancedHttpClient client = new AdvancedHttpClient(ccm, params);
			return client;
		} catch (Exception e) {
			return null;
		}

	}

	private static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
