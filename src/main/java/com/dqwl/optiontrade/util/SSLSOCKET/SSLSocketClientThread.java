package com.dqwl.optiontrade.util.SSLSOCKET;

import android.content.Context;

import com.dqwl.optiontrade.BuildConfig;
import com.dqwl.optiontrade.bean.BeanUserLoginLogin;
import com.dqwl.optiontrade.constant.ServerIP;
import com.dqwl.optiontrade.util.SocketUtil;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLSocketClientThread implements Runnable {
    private Context mContext;
    private String TAG ="123";
//    private static final String CLIENT_KET_PASSWORD = "123456";//私钥密码
//    private static final String CLIENT_TRUST_PASSWORD = "123456";//信任证书密码
    private static final String CLIENT_AGREEMENT = "TLS";//使用协议
//    private static final String CLIENT_AGREEMENT = "SSL";//使用协议
    private static final String CLIENT_KEY_MANAGER = "X509";//密钥管理器
    private static final String CLIENT_TRUST_MANAGER = "X509";//
    private static final String CLIENT_KEY_KEYSTORE = "BKS";//密库，这里用的是BouncyCastle密库
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";//
    private SSLSocket sslSocket;
    private String userName, password;

    public SSLSocketClientThread(Context context, String userName, String password) {
        mContext = context;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void run() {
        trustAllHosts();
    }

    public void trustAllHosts() {
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

        // Install the all-trusting trust manager
        DataOutputStream outputStream = null;
        try {
            SSLContext sc = SSLContext.getInstance(CLIENT_AGREEMENT);
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocket = (SSLSocket) sc.getSocketFactory().createSocket(BuildConfig.API_URL, ServerIP.PORT);
            sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                @Override
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    new ReceiveThread(sslSocket).start();
                }
            });
            BeanUserLoginLogin userLogin = new BeanUserLoginLogin(Integer.valueOf(userName), password);
            String loginStr = new Gson().toJson(userLogin, BeanUserLoginLogin.class);
            outputStream = new DataOutputStream(sslSocket.getOutputStream());
            outputStream.write(SocketUtil.writePureByte(loginStr));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}