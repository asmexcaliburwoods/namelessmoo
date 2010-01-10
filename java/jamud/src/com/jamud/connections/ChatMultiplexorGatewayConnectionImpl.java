package com.jamud.connections;

public class ChatMultiplexorGatewayConnectionImpl extends AbstractConnection
		implements ChatMultiplexorGatewayConnection {
	
	public static interface Emitter{
		void emit(String text);
	}

	private Emitter emitter;
	
	public ChatMultiplexorGatewayConnectionImpl(Emitter emitter){
		this.emitter=emitter;
	}

	@Override
	public void print(String text) {
		emitter.emit(text);
	}

}
