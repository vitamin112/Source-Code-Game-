package io;

public interface ISessionHandler {

    public abstract void processMessage(Session conn, Message message);

    public abstract void onConnectionFail(Session conn);

    public abstract void onDisconnected(Session conn);

    public abstract void onConnectOK(Session conn);
}
