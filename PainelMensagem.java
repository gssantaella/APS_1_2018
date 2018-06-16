package com.aps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class PainelMensagem extends JPanel implements ListenerMensagem {

    private final ClienteChat cliente;
    private final String login;

    private DefaultListModel<String> lstModelo = new DefaultListModel<>();
    private JList<String> lstMsg = new JList<>(lstModelo);
    private JTextField fdInput = new JTextField();

    public PainelMensagem(ClienteChat cliente, String login) {
        this.cliente = cliente;
        this.login = login;

        cliente.addListenerMensagem(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(lstMsg), BorderLayout.CENTER);
        add(fdInput, BorderLayout.SOUTH);

        fdInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String texto = fdInput.getText();
                    if (!(texto.equals(""))) {
                        cliente.msg(login, texto);
                        lstModelo.addElement("Eu: " + texto);
                    }
                    fdInput.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean recebeMsg(String tipo, String quemEnviou, ArrayList<String> contatos, String msgCorpo) {
        if (tipo.equals("U") &&
                login.equalsIgnoreCase(quemEnviou)) {
            String linha = quemEnviou + ": " + msgCorpo;
            lstModelo.addElement(linha);
            return true;
        }
        return false;
    }

    public String getLogin() {
        return login;
    }
}
