package com.jamud.connections;


public abstract interface SocketServerListener {


   public abstract void serverAccept(SocketServerThread source,
				     java.net.Socket socket);


   public abstract void serverError(SocketServerThread source,
				    Exception e);


}
