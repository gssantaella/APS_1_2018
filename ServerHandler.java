package com.aps;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends Thread {
    private final Socket clienteSocket;
    private final Server server;
    private String login = null;
    private OutputStream out;

    public ServerHandler(Server server, Socket clienteSocket) {
        this.server = server;
        this.clienteSocket = clienteSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket(clienteSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLogin() {
        return login;
    }

    private void handleClientSocket(Socket clienteSocket) throws IOException {
        InputStream in = clienteSocket.getInputStream();
        this.out = clienteSocket.getOutputStream();

        BufferedReader rd = new BufferedReader(new InputStreamReader(in));
        String linha;
        while ((linha = rd.readLine()) != null) {
            String[] tokens = StringUtils.split(linha);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd) ||
                        "quit".equalsIgnoreCase(linha)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(out,tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(linha, null, 3);
                    handleMensagem(tokensMsg);
                } else if ("msgMulti".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(linha,null,4);
                    handleMensagemMulti(tokensMsg);
                } else if ("msgAll".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(linha,null,2);
                    handleMensagemTodos(tokensMsg);
                } else {
                    String msg = "Comando desconhecido: " + cmd + "\n";
                    out.write(msg.getBytes()); out.flush();
                }
            }
        }
        clienteSocket.close();
    }

    private void handleLogin(OutputStream out, String[] tokens) throws  IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String pw = tokens[2];
            String msg;
            boolean estaOn;

            try {
                estaOn = server.estaOnline(login);
            } catch (NullPointerException ex) {
                handleErro(-3, login);
                estaOn = false;
            }

            if (estaOn) {
                handleErro(-1, login);
            } else if ((login.equals("gil") && pw.equals("gil")) ||
                    (login.equals("messias") && pw.equals("messias")) ||
                    (login.equals("cuin") && pw.equals("cuin"))) {
                msg = "Logando\n";

                server.mudaStatus(login,true);

                out.write(msg.getBytes()); out.flush();
                this.login = login;

                System.out.println(">>> " + login + " conectou.");

                List<ServerHandler> workerList = server.getHandlerList();
                for (ServerHandler w : workerList) {
                    if (!login.equals(w.getLogin()) &&
                            w.getLogin() != null) {
                        String msg2 = "online " + w.getLogin() + "\n";
                        envia(msg2);
                    }
                }

                String onlineMsg = "online " + login + " esta online.\n";

                for (ServerHandler w : workerList) {
                    if (!login.equals(w.getLogin()) &&
                            w.getLogin() != null) {
                        w.envia(onlineMsg);
                    }
                }
            } else {
                handleErro(-2, login);
            }
        } else {
            handleErro(-4,"null");
        }
    }

    private void handleLogoff() throws IOException {
        server.removeHandler(this);
        List<ServerHandler> handlerList = server.getHandlerList();

        String offlineMsg = "offline " + login + " esta offline.\n";

        for (ServerHandler handler : handlerList) {
            if (login != null &&
                    !login.equals(handler.getLogin()) &&
                    handler.getLogin() != null) {
                handler.envia(offlineMsg);
            }
        }
        clienteSocket.close();

        server.mudaStatus(login,false);
        System.out.println("--- " + login + " desconectou.");
    }

    // formato: "msg" "login" corpoMsg
    private void handleMensagem(String[] tokens) throws IOException {
        String enviaPara = tokens[1];
        String corpo = tokens[2];

        if (corpo == null || corpo.equals("")) {
            handleErro(-5, login);
        }

        List<ServerHandler> handlerList = server.getHandlerList();
        for (ServerHandler handler : handlerList) {
            if (enviaPara.equalsIgnoreCase(handler.getLogin())) {
                String msgSaida = "msg " + login + " " + corpo + "\n";
                handler.envia(msgSaida);
            }
        }
    }

    private void handleMensagemMulti(String[] tokens) throws IOException {
        String id = tokens[1];
        int numContato = Integer.parseInt(tokens[2]);
        String[] texto = StringUtils.split(tokens[3], null, numContato+1);
        ArrayList<String> logins = new ArrayList<>();

        for (int i = 0; i < numContato; i++) { logins.add(texto[i]); }

        List<ServerHandler> handlerList = server.getHandlerList();
        for (ServerHandler handler : handlerList) {
            for (String enviaPara : logins) {
                if (enviaPara.equalsIgnoreCase(handler.getLogin()) && !enviaPara.equals(this.login)) {
                    //passa numContato com -1 pois o metodo vai retirar o proprio login da msg
                    String msgSaida = handler.constroiMsg(id, this.login, numContato-1, logins);
                    msgSaida += texto[numContato] + "\n";
                    handler.envia(msgSaida);
                }
            }
        }
    }

    private String constroiMsg(String id, String quemEnviou, int numContato, ArrayList<String> contatos) {
        String msg = "msgMulti " + id + " " + String.valueOf(numContato) + " " + quemEnviou + " ";
        for (String c : contatos) {
            if (!login.equalsIgnoreCase(c)) { msg += c + " "; }
        }
        return msg;
    }

    private void handleMensagemTodos(String[] tokens) throws IOException {
        String corpo = tokens[1];
        List<ServerHandler> handlerList = server.getHandlerList();
        for (ServerHandler handler : handlerList) {
            String msgSaida = "msgAll " + login + " " + corpo + "\n";
            handler.envia(msgSaida);
        }
    }

    private void envia(String msg) throws IOException {
        if (login != null)
            out.write(msg.getBytes());
        out.flush();
    }

    private void handleErro(int idErro, String login) {
        String msgErro;

        switch (idErro) {
            case -1:
                msgErro = "Usuario ja esta conectado";
                break;
            case -2:
                msgErro = "Login/senha incorretos";
                break;
            case -3:
                msgErro = "Usuario inexistente";
                break;
            case -4:
                msgErro = "Preencha os campos necessarios";
                break;
            case -5:
                msgErro = "Mensagem vazia";
                break;
            default:
                msgErro = "--- ERRO ---";
                break;
        }
        try {
            out.write(msgErro.getBytes()); out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Login falhou: " + login + ", motivo: " + msgErro);
    }
}
