package com.example.minissltest;


import java.util.Formatter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


import android.util.Log;



public class MinaClientHandler extends IoHandlerAdapter {

	private static final String TAG = "MinaClientHandler";

	public void sessionCreated(IoSession session) throws Exception {
		Log.v(TAG,"[NIO Client]>> sessionCreated");
		session.write("android client create");
	}

	public void sessionOpened(IoSession session) throws Exception {
		Log.v(TAG,"[NIO Client]>> sessionOpened");
		session.write("android client open");
	}

	public void sessionClosed(IoSession session) throws Exception {
		Log.v(TAG,"[NIO Client]>> sessionClosed");
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		Log.v(TAG,"[NIO Client]>> sessionIdle");
	}

	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		Log.v(TAG,"[NIO Client]>> exceptionCaught :");
		cause.printStackTrace();
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		Log.v(TAG,"[NIO Client]>> messageReceived");
		Log.v(TAG,"[NIO Client Received]>>{}"+(String) message);
		session.write("i got u");
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		Log.v(TAG,"[NIO Client]>> messageSent");
		Log.v(TAG,"[NIO Client messageSent]>> "+(String) message);
	}
}