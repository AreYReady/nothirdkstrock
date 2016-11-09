package com.dqwl.optiontrade.mvp.login.presenter;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.dqwl.optiontrade.BuildConfig;
import com.dqwl.optiontrade.bean.BeanUserLoginLogin;
import com.dqwl.optiontrade.bean.IUserLogin;
import com.dqwl.optiontrade.handler.HandlerSend;
import com.dqwl.optiontrade.handler.HandlerWrite;
import com.dqwl.optiontrade.constant.ServerIP;
import com.dqwl.optiontrade.mvp.login.view.ILoginView;
import com.dqwl.optiontrade.util.SSLSOCKET.Decoder;
import com.dqwl.optiontrade.util.SSLSOCKET.Encoder;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLDecoderImp;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLEncodeImp;
import com.dqwl.optiontrade.util.SSLSOCKET.SSLSocketChannel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kaede on 2015/5/18.
 */
public class LoginPresenterCompl implements ILoginPresenter {
	public static final String DISCONNECT_FROM_SERVER = "DISCONNECT_FROM_SERVER";
	public static final String THREAD_READ = "threadRead";
	public static final String SSL_SOCKET = "sslSocket";
	public static final String HANDLER_WRITE = "handler_write";
	ILoginView iLoginView;
	IUserLogin mIUserLogin;
	HandlerThread mHandlerThread;
	private SSLSocketChannel<String> mSSLSocketChannel;
	private HandlerWrite mHandlerWrite;

	public LoginPresenterCompl(ILoginView iLoginView) {
		this.iLoginView = iLoginView;
	}

	@Override
	public void doLogin(final String name, final String passwd) {
//		MinaUtil.connectToServer(name, passwd);
//		new Thread(new SSLSocketClientThread(iLoginView.getContext(),name, passwd)).start();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					NioSslClient nioSslClient = new NioSslClient("TLS", ServerIP.HOST, ServerIP.PORT);
//					if(nioSslClient.connect()){
//						BeanUserLoginLogin userLogin = new BeanUserLoginLogin(Integer.valueOf(name), passwd);
//						String loginStr = new Gson().toJson(userLogin, BeanUserLoginLogin.class);
//						nioSslClient.write(loginStr);
//						nioSslClient.read();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
		mHandlerThread = new HandlerThread("SSL"){
			@Override
			public void run() {
				try {
					Log.i("123", "run: sslTest");
					sslTest(name, passwd);
				} catch (KeyManagementException e) {
					e.printStackTrace();
				}
				super.run();
			}
		};
		mHandlerThread.start();
		Handler handlerRead = new HandlerSend(mHandlerThread.getLooper(),
				iLoginView.getContext(),mHandlerThread, mSSLSocketChannel, mHandlerWrite);
		///		Map<String, Object> map = new HashMap<>();
		EventBus.getDefault().postSticky(handlerRead);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					sslTest(name, passwd);
//				} catch (KeyManagementException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					BeanUserLoginLogin userLogin = new BeanUserLoginLogin(Integer.valueOf(name), passwd);
//						String loginStr = new Gson().toJson(userLogin, BeanUserLoginLogin.class);
//					byte[] tt = SocketUtil.writePureByte(loginStr);
//					MultiplexingTest.connectTest(ServerIP.HOST, ServerIP.PORT, ByteBuffer.wrap(tt));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
	}

	private void sslTest(String name, String passwd) throws KeyManagementException {
		final SocketAddress address = new InetSocketAddress(BuildConfig.API_URL, ServerIP.PORT);
		Encoder<String> encoder = new SSLEncodeImp();
		Decoder<String> decoder = new SSLDecoderImp();
		Log.i("123", "doLogin: Opening channel");
		try {
			mSSLSocketChannel = SSLSocketChannel.open(address, encoder, decoder, 1024*1024, 1024*1024);
			HandlerThread writeThread = new HandlerThread("write");
			writeThread.start();
			mHandlerWrite = new HandlerWrite(writeThread.getLooper(), mSSLSocketChannel);
			Log.i("123", "doLogin: Channel opened, initial handshake done");
///		map.put(SSL_SOCKET, sslSocketChannel);
///		map.put(THREAD_READ, mHandlerThread
			Log.i("123", "doLogin: Sending request");

			BeanUserLoginLogin userLogin = new BeanUserLoginLogin(Integer.valueOf(name), passwd);
			String loginStr = new Gson().toJson(userLogin, BeanUserLoginLogin.class);
			mSSLSocketChannel.send(loginStr);

			Log.i("123", "doLogin: Receiving response");
			mHandlerWrite.sendEmptyMessage(0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		iLoginView = null;
	}

}
