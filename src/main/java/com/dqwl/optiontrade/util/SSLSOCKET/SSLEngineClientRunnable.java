package com.dqwl.optiontrade.util.SSLSOCKET;

import android.util.Log;

import com.dqwl.optiontrade.BuildConfig;
import com.dqwl.optiontrade.constant.ServerIP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author xjunda
 * @date 2016-09-01
 */
public class SSLEngineClientRunnable {
    private SSLEngine sslEngine;
    public void EngineInit() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        try {


            //取得SSL的SSLContext实例
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,trustAllCerts,new java.security.SecureRandom());
            sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(true);

        } catch(NoSuchAlgorithmException e) {
        } catch(KeyManagementException e) {
        }
        SSLSession session = sslEngine.getSession();
        int appBufferMax = session.getApplicationBufferSize();
        int netBufferMax = session.getPacketBufferSize();
        Log.i("123", "EngineInit: appBufferMax:"+appBufferMax + "netBufferMax:"+netBufferMax);
        ByteBuffer mTunSSRAppData   = ByteBuffer.allocate(appBufferMax);
        ByteBuffer mTunSSRWrapData   = ByteBuffer.allocate(netBufferMax);
        ByteBuffer  peerAppData = ByteBuffer.allocate(appBufferMax);
        ByteBuffer peerNetData = ByteBuffer.allocate(netBufferMax);
    }

    public void test() throws IOException {
        // Create a nonblocking socket channel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(BuildConfig.API_URL, ServerIP.PORT));

// Complete connection
        while (!socketChannel.finishConnect()) {
            // do something until connect completed
        }

// Create byte buffers to use for holding application and encoded data
        SSLSession session = sslEngine.getSession();
        ByteBuffer myAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
        ByteBuffer myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        ByteBuffer peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
        ByteBuffer peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());

// Do initial handshake
//        doHandshake(socketChannel, sslEngine, myNetData, peerNetData);

        myAppData.put("hello".getBytes());
        myAppData.flip();

        while (myAppData.hasRemaining()) {
            // Generate SSL/TLS encoded data (handshake or application data)
            SSLEngineResult res = sslEngine.wrap(myAppData, myNetData);

            // Process status of call
            if (res.getStatus() == SSLEngineResult.Status.OK) {
                myAppData.compact();

                // Send SSL/TLS encoded data to peer
                while(myNetData.hasRemaining()) {
                    int num = socketChannel.write(myNetData);
                    if (num == 0) {
                        // no bytes written; try again later
                    }
                }
            }

            // Handle other status:  BUFFER_OVERFLOW, CLOSED
        }

        // Read SSL/TLS encoded data from peer
        int num = socketChannel.read(peerNetData);
        if (num == -1) {
            // The channel has reached end-of-stream
        } else if (num == 0) {
            // No bytes read; try again ...
        } else {
            // Process incoming data
            peerNetData.flip();
            SSLEngineResult   res = sslEngine.unwrap(peerNetData, peerAppData);

            if (res.getStatus() == SSLEngineResult.Status.OK) {
                peerNetData.compact();

                if (peerAppData.hasRemaining()) {
                    // Use peerAppData
                }
            }
            // Handle other status:  BUFFER_OVERFLOW, BUFFER_UNDERFLOW, CLOSED
        }
    }
}
