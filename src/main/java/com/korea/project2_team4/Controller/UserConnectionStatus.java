package com.korea.project2_team4.Controller;

public class UserConnectionStatus {
    private boolean connected;

    public UserConnectionStatus() {
        this.connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
