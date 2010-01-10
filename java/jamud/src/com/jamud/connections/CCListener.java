/**
 * 
 */
package com.jamud.connections;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import openwood.chat.ChatConnector.Listener;

public class CCListener extends RemoteObject implements Listener {
	private final ChatMultiplexorChatConnDispatcher dispatcher;
	
	public CCListener(ChatMultiplexorChatConnDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
	}

	@Override
	public void agentBusy(String connectorId, String networkId, String roomId,
			String senderId) throws RemoteException {
		dispatcher.handleAgentBusy(connectorId, networkId, roomId, senderId);		
	}

	@Override
	public void agentName(String connectorId, String networkId, String roomId,
			String senderId, String nickName, String firstName,
			String lastName, String middleName, String realName)
	throws RemoteException {
		dispatcher.handleAgentName(connectorId, networkId, roomId, senderId,
				nickName, firstName, lastName, middleName, realName);		
	}

	@Override
	public void agentOffline(String connectorId, String networkId,
			String roomId, String senderId) throws RemoteException {
		dispatcher.handleAgentOffline(connectorId, networkId, roomId, senderId);		
	}

	@Override
	public void agentOnline(String connectorId, String networkId,
			String roomId, String senderId) throws RemoteException {
		dispatcher.handleAgentOnline(connectorId, networkId, roomId, senderId);		
	}

	@Override
	public void messageArrived(String connectorId, String networkId,
			String roomId, String senderId, String plainTextMessage)
	throws RemoteException {
		dispatcher.handlePlainTextMessageArrived(connectorId, networkId, roomId, senderId, plainTextMessage);
	}
}