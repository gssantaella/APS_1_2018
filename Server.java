package com.aps;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server extends Thread {
    private final int serverPort;

    private ArrayList<ServerHandler> handlerList = new ArrayList<>();
    private HashMap<String, Boolean> statusUser = new HashMap<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ServerHandler> getHandlerList() {
        return handlerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            //usuarios aceitos
            statusUser.put("gil",false);
            statusUser.put("cuin",false);
            statusUser.put("messias",false);

            while (true) {
                System.out.println("Aceitando conexao...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexao aceita de: " + clientSocket);
                ServerHandler handler = new ServerHandler(this, clientSocket);
                handlerList.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHandler(ServerHandler serverHandler) {
        handlerList.remove(serverHandler);
    }

    public boolean estaOnline(String login) { return statusUser.get(login); }

    public void mudaStatus(String login, boolean status) { statusUser.put(login, status); }
}
