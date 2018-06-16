package com.aps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class PainelContatos extends JPanel implements ListenerStatusUser {

    private final ClienteChat cliente;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    public PainelContatos(ClienteChat cliente) {
        this.cliente = cliente;
        this.cliente.addListenerStatusUser(this);

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = userListUI.getSelectedValue();
                    if (login != null) {
                        final JanelaMensagem mf = new JanelaMensagem(cliente,login);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        ClienteChat cliente = new ClienteChat("localhost", 8818);

        PainelContatos painelContatos = new PainelContatos(cliente);
        JFrame frame = new JFrame("Lista de usuarios");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,600);

        frame.getContentPane().add(new JScrollPane(painelContatos), BorderLayout.CENTER);
        frame.setVisible(true);

        if (cliente.conectar()) {
            try {
                cliente.login("gil","gil");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }

    public DefaultListModel getUserListModel() {
        return userListModel;
    }
}
