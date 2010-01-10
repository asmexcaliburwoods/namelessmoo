package com.jamud.connections;

import jamud.Jamud;
import jamud.object.Player;
import jamud.object.PlayerManager;
import jamud.object.PlayerMask;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.StringTokenizer;

import openwood.chat.ChatConnector;
import openwood.chat.impl.ChatMultiplexorLink;

public class ChatMultiplexorChatConnDispatcher {
	private final ChatMultiplexorGatewayListener cmglistener;
	private volatile ChatMultiplexorLink chatlink0;

	class CMPlayerSubject{
		private final String connectorId;
		private final String networkId;
		private final String roomId;
		private final String playersubjectId;
		public CMPlayerSubject(String connectorId, String networkId,
				String roomId, String playersubjectId) {
			super();
			this.connectorId = connectorId;
			this.networkId = networkId;
			this.roomId = roomId;
			this.playersubjectId = playersubjectId;
		}
		public String getConnectorId() {
			return connectorId;
		}
		public String getNetworkId() {
			return networkId;
		}
		public String getRoomId() {
			return roomId;
		}
		public String getPlayersubjectId() {
			return playersubjectId;
		}
	}
	public ChatMultiplexorChatConnDispatcher(ChatMultiplexorGatewayListener listener) throws RemoteException {
		this.cmglistener=listener;

		//		chatConnector.
		//		
		//		chatConnector.kernelStarted(new Listener() {
		//			@Override
		//			public void messageArrived(String connectorId, String networkId,
		//					String roomId, String senderId, String plainTextMessage)
		//					throws RemoteException {
		//				handlePlainTextMessageArrived(connectorId,networkId,roomId,senderId,plainTextMessage);
		//			}
		//			
		//			@Override
		//			public void agentOnline(String connectorId, String networkId,
		//					String roomId, String senderId) throws RemoteException {
		//				handleAgentOnline(connectorId,networkId,roomId,senderId);
		//			}
		//			
		//			@Override
		//			public void agentOffline(String connectorId, String networkId,
		//					String roomId, String senderId) throws RemoteException {
		//				handleAgentOffline(connectorId,networkId,roomId,senderId);
		//			}
		//			
		//			@Override
		//			public void agentName(String connectorId, String networkId, String roomId,
		//					String senderId, String nickName, String firstName,
		//					String lastName, String middleName, String realName)
		//					throws RemoteException {
		//				handleAgentName(connectorId, networkId, roomId,
		//						senderId, nickName, firstName,
		//						lastName, middleName, realName);
		//			}
		//			
		//			@Override
		//			public void agentBusy(String connectorId, String networkId, String roomId,
		//					String senderId) throws RemoteException {
		//				handleAgentBusy(connectorId,networkId,roomId,senderId);
		//			}
		//		});
	}

	void handleAgentName(String connectorId, String networkId,
			String roomId, String senderId, String nickName, String firstName,
			String lastName, String middleName, String realName) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId+": nick "+nickName+
		" first/m/last name "+firstName+" "+middleName+" "+lastName+" realname "+realName;
		System.out.println("handleAgentName: "+id);
	}

	void handleAgentOnline(final String connectorId, final String networkId,
			final String roomId, final String senderId) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		System.out.println("handleAgentOnline "+id);
//		Player p=getPlayer(connectorId, networkId, roomId, senderId);
//		cmglistener.onNewConnection(id, p);
	}

	void handleAgentOffline(String connectorId, String networkId,
			String roomId, String senderId) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		System.out.println("handleAgentOffline "+id);
		//getPlayer(connectorId, networkId, roomId, senderId).terminate();
	}

	void handleAgentBusy(String connectorId, String networkId,
			String roomId, String senderId) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		System.out.println("handleAgentBusy "+id);
	}

	void handlePlainTextMessageArrived(final String connectorId,
			final String networkId, final String roomId, final String senderId,
			final String plainTextMessage) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		System.out.println("handlePlainTextMessageArrived "+id+": "+plainTextMessage);
		new Thread("handlePlainTextMessageArrived"){
			public void run(){
				try{
					//		CMPlayerSubject...
					PlayerMask pm=getPlayer(connectorId, networkId, roomId, senderId);
					if(!(pm instanceof Player)){
						new UnsupportedOperationException("not (playermask instaceof Player): "+id).printStackTrace();
						return;
					}
					Player p=(Player) pm;
					StringTokenizer st=new StringTokenizer(plainTextMessage,"\r\n");
					while(st.hasMoreTokens()){
						String tok=st.nextToken().trim();
						if(tok.length()==0)continue;
						boolean ad_to=false;
						if(tok.toLowerCase().startsWith("ad")){
							ad_to=true;
							StringTokenizer adst=new StringTokenizer(tok," \t");
							if(!adst.hasMoreTokens())ad_to=false;
							if(ad_to)adst.nextToken().toLowerCase().trim();
							String to=null;
							if(!adst.hasMoreTokens())ad_to=false;
							if(ad_to)to=adst.nextToken().toLowerCase().trim();
							if(!("to".equals(to)))ad_to=false;
							String connectorid=null;
							if(!adst.hasMoreTokens())ad_to=false;
							if(ad_to)connectorid=adst.nextToken().toLowerCase().trim();
							if(connectorid==null)ad_to=false;
							String loginid=null;
							if(!adst.hasMoreTokens())ad_to=false;
							if(ad_to)loginid=adst.nextToken().toLowerCase().trim();
							if(loginid==null)ad_to=false;
							final String loginid_=loginid;
							final String connectorid_=connectorid;
							if(ad_to){
								new Thread("ad_to from "+connectorId+"/"+senderId){
									public void run(){
										try{
											waitForChatLink_get().sendMessage(
													connectorid_, networkId, roomId, loginid_, 
													senderId+"@"+connectorid_+" has just advertised namelessmoo to you.");
											handlePlainTextMessageArrived(connectorid_,networkId, roomId, loginid_,"0");
										}catch(Throwable tr){
											tr.printStackTrace();
										}
									}
								}.start();
								continue;
							}
						}
						//ad to icq 12345678
						if(p.initializableState()!=p.STATE_INITIALIZED&&p.initializableState()!=p.STATE_INITIALIZING){
							if(!p.initialize()){
								p.terminate();
								return;
							}
						}
						p.enqueueCommand(tok); //p.enact(tok);
					}
					//new Thread(""){public void run(){try{
				}catch(Throwable tr){
					tr.printStackTrace();
				}
			}
		}.start();
	}

	protected PlayerMask getPlayer(String connectorId, String networkId,
			String roomId, String senderId,ChatMultiplexorGatewayConnection conn) {
		return Jamud.currentInstance().playerManager().getOrCreatePlayerMask(networkId+"/"+senderId,conn);		
	}

	protected Player getPlayer(String connectorId, String networkId,
			String roomId, String senderId) {
		String name=connectorId+" "+senderId;
		PlayerManager mgr=Jamud.currentInstance().playerManager();
		PlayerMask pm=mgr.getPlayerMask(name);
		if(pm!=null)return (Player) pm;
		return (Player) mgr.getOrCreatePlayerMask(name,createConnection(connectorId, networkId, roomId, senderId));		
	}

	private ChatMultiplexorGatewayConnection createConnection(final String connectorId, final String networkId, final String roomId, final String senderId){
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		ChatMultiplexorGatewayConnectionImpl.Emitter emitter=new ChatMultiplexorGatewayConnectionImpl.Emitter(){
			@Override
			public void emit(String text) {
				try {
					waitForChatLink_get().sendMessage(connectorId, networkId, roomId, senderId, text);
				} catch (Exception e) {
					cmglistener.onError(id, e);
				}
			}
		};
		return new ChatMultiplexorGatewayConnectionImpl(emitter);
	}

	private synchronized ChatMultiplexorLink waitForChatLink_get()throws InterruptedException{
		while(true){
			if(chatlink0!=null)return chatlink0;
			wait();
		}
	}

	public void setChatLink(ChatMultiplexorLink chatlink0) {
		if(chatlink0==null)throw new NullPointerException();
//		Map<String,ChatConnector> connectorId2connector=chatlink0.getConnectorId2ConnectorMap();
//		for(Map.Entry<String,ChatConnector> entry : connectorId2connector.entrySet()){
//			String id=entry.getKey();
//			ChatConnector cc=entry.getValue();
//			cc.addListe
//		}
		synchronized(this){
			this.chatlink0 = chatlink0;
			notifyAll();
		}
	}
}
