package com.aps;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClienteChat {
    private final int serverPort;
    private final String serverNome;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;
    private String msgErro;

    private ArrayList<ListenerStatusUser> listenerStatusUsers = new ArrayList<>();
    private ArrayList<ListenerMensagem> listenerMensagems = new ArrayList<>();

    public ClienteChat(String serverNome, int serverPort) {
        this.serverNome = serverNome;
        this.serverPort = serverPort;

        this.addListenerStatusUser(new ListenerStatusUser() {
            @Override
            public void online(String login) {
                System.out.println("Online: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("Offline: " + login);
            }
        });
    }

    public static void  main(String[] args) throws IOException {
        ClienteChat client = new ClienteChat("localhost", 8818);

        if (!client.conectar()) {
            System.err.println("Conexao falhou.");
        } else {
            System.out.println("Conexao sucedida.");
            if (client.login("guest","guest")) {
                System.out.println("Login confirmado");
            } else
                System.out.println("Login falhou");
        }
    }

    public void msg(String enviaPara, String msgCorpo) throws IOException {
        String cmd = "msg " + enviaPara + " " + msgCorpo + "\n";
        serverOut.write(cmd.getBytes()); serverOut.flush();
    }

    public void msgMulti(ArrayList<String> enviaPara, String msgCorpo, String idConversa) throws IOException {
        String aux = "";
        for (String s : enviaPara) {
            aux += s + " ";
        }
        String cmd = "msgMulti " + idConversa + " " + enviaPara.size() + " " + aux + msgCorpo + "\n";
        serverOut.write(cmd.getBytes()); serverOut.flush();
    }

    public void msgTodos(String msgCorpo) throws IOException {
        String cmd = "msgAll " + msgCorpo + "\n";
        serverOut.write(cmd.getBytes()); serverOut.flush();
    }

    public boolean login(String login, String pw) throws IOException {
        String cmd = "login " + login + " " + pw + "\n";
        serverOut.write(cmd.getBytes()); serverOut.flush();

        String resposta = bufferedIn.readLine();
        System.out.println("Servidor: " + resposta);

        if ("Logando".equalsIgnoreCase(resposta)) {
            iniciaIdComando();
            return true;
        }
        msgErro = resposta;
        return false;
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes()); serverOut.flush();
    }

    private void iniciaIdComando() {
        Thread t = new Thread() {
            @Override
            public void run() {
                identificaComando();
            }
        };
        t.start();
    }

    private void identificaComando() {
        try {
            String linha;
            while ((linha = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(linha);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(linha, null, 3);
                        handleMsg(tokensMsg);
                    } else if ("msgMulti".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(linha,null,5);
                        handleMsgMulti(tokensMsg);
                    } else if ("msgAll".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(linha,null,3);
                        handleMsgGlobal(tokensMsg);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (ListenerStatusUser l : listenerStatusUsers) {
            l.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (ListenerStatusUser l : listenerStatusUsers) {
            l.online(login);
        }
    }

    private void handleMsg(String[] tokenMsg) {
        String quemEnviou = tokenMsg[1];
        String msgCorpo = tokenMsg[2];
        boolean conversaExiste = false;

        for (ListenerMensagem l : listenerMensagems) {
            conversaExiste = l.recebeMsg("U", quemEnviou, null, msgCorpo);
            if (conversaExiste) { break; }
        }

        if (!conversaExiste) {
            JanelaMensagem janelaMensagem = new JanelaMensagem(this, quemEnviou);
            conversaExiste = janelaMensagem.getPainelMensagem().recebeMsg("U", quemEnviou, null, msgCorpo);
        }
    }

    private void handleMsgMulti(String[] tokenMsg) {
        String id = tokenMsg[1];
        int numContato = Integer.parseInt(tokenMsg[2]);
        String quemEnviou = tokenMsg[3];
        String[] texto = StringUtils.split(tokenMsg[4], null, numContato+1);
        ArrayList<String> logins = new ArrayList<>();
        boolean conversaExiste = false;

        logins.add(quemEnviou);
        for (int i = 0; i < numContato; i++) { logins.add(texto[i]); }

        for (ListenerMensagem l : listenerMensagems) {
            conversaExiste = l.recebeMsg(id, quemEnviou, logins, texto[numContato]);
            if (conversaExiste) { break; }
        }

        if (!conversaExiste) {
            JanelaMulti janelaMulti = new JanelaMulti(this, logins, id);
            conversaExiste = janelaMulti.getPainelMulti().recebeMsg(id, quemEnviou, logins, texto[numContato]);
        }
    }

    private void handleMsgGlobal(String[] tokens) {
        String quemEnviou = tokens[1];
        String msgCorpo = tokens[2];
        boolean conversaExiste = false;

        for (ListenerMensagem l : listenerMensagems) {
            conversaExiste = l.recebeMsg("G", quemEnviou, null, msgCorpo);
        }
    }

    public boolean conectar() {
        try {
            this.socket = new Socket(serverNome, serverPort);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addListenerStatusUser(ListenerStatusUser listener) {
        listenerStatusUsers.add(listener);
    }

    public void removeListenerStatus(ListenerStatusUser listener) {
        listenerStatusUsers.remove(listener);
    }

    public void addListenerMensagem(ListenerMensagem l) {
        listenerMensagems.add(l);
    }

    public void removeListenerMensagem(ListenerMensagem l) {
        listenerMensagems.remove(l);
    }

    public String getMsgErro() { return msgErro; }


}
