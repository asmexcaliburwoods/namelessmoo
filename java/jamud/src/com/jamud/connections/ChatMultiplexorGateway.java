package com.jamud.connections;


import jamud.object.Player;
import jamud.plugin.JamudPlugin;

import java.rmi.RemoteException;

import openwood.chat.impl.ChatConnectorLauncher;
import openwood.chat.impl.ChatMultiplexorLink;

public class ChatMultiplexorGateway extends JamudPlugin{
//	private static final String PARAM_PREFIX_CONNECTOR = "connector.";
	private static final String PARAM_CMPROP = "cmprop";

	public final String getName() {return ChatMultiplexorGateway.class.getSimpleName();}

	public final String getVersion() {return "1.0";}

	public final String getAuthor() {return "egphilippov@googlemail.com";}

	public final String getInfo() {return "Allows for misc. connections.";}

	private ChatMultiplexorGatewayListener listener;

	private int state = STATE_TERMINATED;
	protected ChatMultiplexorLink chatlink;
	private ChatMultiplexorChatConnDispatcher dispatcher;

	public int initializableState() {
		return this.state;
	}



	public ChatMultiplexorGateway() throws RemoteException {
		super();

		this.listener = new ChatMultiplexorGatewayListener() {

			@Override
			public void onError(Object source, Throwable tr) {
				tr.printStackTrace();
//			    try {
//					terminate();
//			    } catch(Exception ee) {
//			    	ee.printStackTrace();
//			    }
			}

			@Override
			public void onNewConnection(Object source, Player p) {}
		};
	}



	public synchronized boolean initialize() {
		System.out.println( "begin: cmgw.initialize()" );
		if(this.state > STATE_TERMINATED) {
			System.out.println( " not yet terminated" );
			System.out.println( "end/nyt: cmgw.initialize()" );
			return false;
		} else {
			this.state = STATE_INITIALIZING;
		}

		try {	    
//			if(thread == null) {
				final String cmproplocation = parameters().getAttribute( PARAM_CMPROP, "must point to ChatConnectorLauncher.properties from openwood googlecode project" );
				System.out.println("cmprop location: "+cmproplocation);
				new Thread("ChatConnectorLauncher.main"){
					@Override
					public void run() {
						try{
							dispatcher=new ChatMultiplexorChatConnDispatcher(listener);
							CCListener ccl=new CCListener(dispatcher);
							ChatMultiplexorGateway.this.chatlink=
								new ChatMultiplexorLink(cmproplocation,ccl);
							dispatcher.setChatLink(chatlink);
//							ChatConnectorLauncher.main(new String[]{cmproplocation});
//							for(Map.Entry<String,ChatConnector> pair:
//								ChatConnectorLauncher.getConnectors().entrySet()){
//								String key=pair.getKey();
//								ChatConnector ccon=pair.getValue();
//								new ChatMultiplexorChatConnDispatcher(key,ccon,listener);
//							}
						}catch(Throwable tr){
							tr.printStackTrace();
							listener.onError(ChatConnectorLauncher.class, tr);
						}
					}
				}.start();

//				thread = new SocketServerThread( this.listener, ip );
//			}
//			thread.start();

		} catch(Exception e) {
			e.printStackTrace();

			this.state = STATE_TERMINATED;
			System.out.println( "end/ex: cmgw.initialize()" );
			return false;
		}

		this.state = STATE_INITIALIZED;
		System.out.println( "end: cm.initialise()" );
		return true;
	}


	public synchronized boolean terminate() {
		System.out.println( "begin: cm.terminate()" );
		if(this.state < STATE_INITIALIZED) {
			System.out.println( " not yet initialized" );
			System.out.println( "end: cm.terminate()" );
			return false;
		} else {
			this.state = STATE_TERMINATING;
		}

//		// shut it down
//		thread.halt();

		this.state = STATE_TERMINATED;
		System.out.println( "end: cm.terminate()" );
		return true;
	}

	public boolean isActive() {
		return state == STATE_INITIALIZED;
	}
}
