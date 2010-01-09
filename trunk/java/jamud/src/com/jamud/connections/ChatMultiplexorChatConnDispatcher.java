package com.jamud.connections;

import jamud.Jamud;
import jamud.object.Player;
import jamud.object.PlayerManager;
import jamud.object.PlayerMask;

import java.rmi.RemoteException;
import java.util.StringTokenizer;

import openwood.chat.ChatConnector;
import openwood.chat.ChatConnector.Listener;

public class ChatMultiplexorChatConnDispatcher {
	@SuppressWarnings("unused")
	private final String chatConnectorId;
	private final ChatConnector chatConnector;
	private final ChatMultiplexorGatewayListener cmglistener;

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
	public ChatMultiplexorChatConnDispatcher(String chatConnectorId, ChatConnector ccon,
			ChatMultiplexorGatewayListener listener) throws RemoteException {
		this.chatConnectorId=chatConnectorId;
		this.chatConnector=ccon;
		this.cmglistener=listener;
		
//		chatConnector.
//		
		chatConnector.kernelStarted(new Listener() {
			@Override
			public void messageArrived(String connectorId, String networkId,
					String roomId, String senderId, String plainTextMessage)
					throws RemoteException {
				handlePlainTextMessageArrived(connectorId,networkId,roomId,senderId,plainTextMessage);
			}
			
			@Override
			public void agentOnline(String connectorId, String networkId,
					String roomId, String senderId) throws RemoteException {
				handleAgentOnline(connectorId,networkId,roomId,senderId);
			}
			
			@Override
			public void agentOffline(String connectorId, String networkId,
					String roomId, String senderId) throws RemoteException {
				handleAgentOffline(connectorId,networkId,roomId,senderId);
			}
			
			@Override
			public void agentName(String connectorId, String networkId, String roomId,
					String senderId, String nickName, String firstName,
					String lastName, String middleName, String realName)
					throws RemoteException {
				handleAgentName(connectorId, networkId, roomId,
						senderId, nickName, firstName,
						lastName, middleName, realName);
			}
			
			@Override
			public void agentBusy(String connectorId, String networkId, String roomId,
					String senderId) throws RemoteException {
				handleAgentBusy(connectorId,networkId,roomId,senderId);
			}
		});
	}

	protected void handleAgentName(String connectorId, String networkId,
			String roomId, String senderId, String nickName, String firstName,
			String lastName, String middleName, String realName) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId+": nick "+nickName+
		" first/m/last name "+firstName+" "+middleName+" "+lastName+" realname "+realName;
	}

	protected void handleAgentOnline(final String connectorId, final String networkId,
			final String roomId, final String senderId) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		cmglistener.onNewConnection(id, getPlayer(connectorId, networkId, roomId, senderId));
	}

	protected void handleAgentOffline(String connectorId, String networkId,
			String roomId, String senderId) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		System.out.println("handleAgentOffline "+id);
		getPlayer(connectorId, networkId, roomId, senderId).terminate();
	}

	protected void handleAgentBusy(String connectorId, String networkId,
			String roomId, String senderId) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		System.out.println("handleAgentBusy "+id);
	}

	protected void handlePlainTextMessageArrived(String connectorId,
			String networkId, String roomId, String senderId,
			String plainTextMessage) {
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
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
			p.enqueueCommand(tok);//enact?
		}
	}

	protected PlayerMask getPlayer(String connectorId, String networkId,
			String roomId, String senderId,ChatMultiplexorGatewayConnection conn) {
		return Jamud.currentInstance().playerManager().getOrCreatePlayerMask(networkId+"/"+senderId,conn);		
	}
	
	protected Player getPlayer(String connectorId, String networkId,
			String roomId, String senderId) {
		String name=networkId+"/"+senderId;
		PlayerManager mgr=Jamud.currentInstance().playerManager();
		PlayerMask pm=mgr.getPlayerMask(name);
		if(pm!=null)return (Player) pm;
		return (Player) mgr.getOrCreatePlayerMask(name,createConnection(connectorId, networkId, roomId, senderId));		
	}
	
	private ChatMultiplexorGatewayConnection createConnection(String connectorId, final String networkId, final String roomId, final String senderId){
		final String id=connectorId+"/"+networkId+"/"+roomId+"/"+senderId;
		ChatMultiplexorGatewayConnection cmgc=new ChatMultiplexorGatewayConnection() {
			private Player player; 
			private final long connectTime=System.currentTimeMillis();
			@Override
			public void setPlayer(Player player) {
				this.player=player;				
			}

			@Override
			public void print(String text) {
				try {
					chatConnector.sendMessage(networkId, roomId, senderId, text);
				} catch (RemoteException e) {
					cmglistener.onError(id, e);
				}
			}

			@Override
			public Player getPlayer() {return player;}

			@Override
			public long connectedAt() {return connectTime;}
		};
		return cmgc;
	}
}
