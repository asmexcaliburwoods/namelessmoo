package com.jamud.connections;

import jamud.object.Player;

public interface ChatMultiplexorGatewayListener {
   public void onNewConnection(Object source, Player conn);
   public void onError(Object source, Throwable tr);
}
