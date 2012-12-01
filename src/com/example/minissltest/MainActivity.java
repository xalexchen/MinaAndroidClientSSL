package com.example.minissltest;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
	public static final String TAG = "SSL";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MinaClientThread().start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private class MinaClientThread extends Thread{
    	
    	private static final String CLIENT_KET_PASSWORD = "123456";//私钥密码
    	private static final String CLIENT_TRUST_PASSWORD = "123456";//信任证书密码
    	private static final String CLIENT_AGREEMENT = "TLS";//使用协议
    	private static final String CLIENT_KEY_MANAGER = "X509";//密钥管理器
    	private static final String CLIENT_TRUST_MANAGER = "X509";//
    	private static final String CLIENT_KEY_KEYSTORE = "BKS";//密库，这里用的是BouncyCastle密库
    	private static final String CLIENT_TRUST_KEYSTORE = "BKS";//
		@Override
    	public void run()
    	{
			// 创建客户端连接器
			IoConnector connector = new NioSocketConnector();

			// 设置加密过滤器
			SslFilter connectorTLSFilter;
			try {
//				InputStream in = getResources().getAssets().open("newclient.bks");
//				InputStream trust = getResources().getAssets().open("newclient.bks");
//				BogusSslContextFactory bc = new BogusSslContextFactory(in,trust);
	    		//取得SSL的SSLContext实例
				SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
				//取得KeyManagerFactory和TrustManagerFactory的X509密钥管理器实例
				KeyManagerFactory keyManager = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);
				TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
				//取得BKS密库实例
				KeyStore kks= KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
				KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
				//加客户端载证书和私钥,通过读取资源文件的方式读取密钥和信任证书
				kks.load(getBaseContext()
						.getResources()
						.openRawResource(R.raw.bksclient),CLIENT_KET_PASSWORD.toCharArray());
				tks.load(getBaseContext()
						.getResources()
						.openRawResource(R.raw.bksclient),CLIENT_TRUST_PASSWORD.toCharArray());
				//初始化密钥管理器
				keyManager.init(kks,CLIENT_KET_PASSWORD.toCharArray());
				trustManager.init(tks);
				//初始化SSLContext
				sslContext.init(keyManager.getKeyManagers(),trustManager.getTrustManagers(),null);
				//生成SSLSocket
//				Client_sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(SERVER_IP,4123);
				
				connectorTLSFilter = new SslFilter(sslContext);
				// 设置为客户端模式
				connectorTLSFilter.setUseClientMode(true);
				connector.getFilterChain().addLast("SSL", connectorTLSFilter);

				// 设置事件处理器
				connector.setHandler(new MinaClientHandler());

				// 设置编码过滤器和按行读取数据模式
				connector.getFilterChain().addLast("codec",
						new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

				// 创建连接
				ConnectFuture future = connector.connect(new InetSocketAddress("192.168.0.175", 4123));
				// 等待连接创建完成
				future.awaitUninterruptibly();
				Log.v("ee","handshake sucess");
				// 获取连接会话
				IoSession session = future.getSession();
				// 发送信息
				session.write("did i save ?");
				Log.v("ee","sending");
				// 等待连接断开
				session.getCloseFuture().awaitUninterruptibly();
				connector.dispose();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    }    
}
