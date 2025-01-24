package com.wallboard.wallboard.pbx.adapters;

import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.event.ManagerEvent;

public class Issabel {

    private final ManagerConnection managerConnection;
    private final EventListener eventListener;

    @FunctionalInterface
    public interface EventListener {
        void onEvent(ManagerEvent event);
    }

    public Issabel(String host, int port, String username, String password, EventListener eventListener) {
        ManagerConnectionFactory factory = new ManagerConnectionFactory(host, username, password);
        this.managerConnection = factory.createManagerConnection();
        this.eventListener = eventListener;
    }

    public void connect() throws Exception {
        managerConnection.addEventListener(eventListener::onEvent); // Register custom event listener
        managerConnection.login(); // Log in to AMI
        System.out.println("Connected to Issabel AMI");
    }

    public void disconnect() {
        managerConnection.logoff();
        System.out.println("Disconnected from Issabel AMI");
    }
}
