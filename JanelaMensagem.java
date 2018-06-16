package com.aps;

import javax.swing.*;
import java.awt.*;

public class JanelaMensagem extends JFrame {

    private final PainelMensagem painelMensagem;

    public JanelaMensagem(ClienteChat cliente, String login) {

        super("Conversa com " + login);

        painelMensagem = new PainelMensagem(cliente, login);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(200, 200);
        this.getContentPane().add(painelMensagem, BorderLayout.CENTER);
        this.setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cliente.removeListenerMensagem(painelMensagem);
            }
        });
    }

    public PainelMensagem getPainelMensagem() {
        return this.painelMensagem;
    }
}