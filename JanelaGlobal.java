package com.aps;

import javax.swing.*;
import java.awt.*;

public class JanelaGlobal extends JFrame {

    private final PainelGlobal painelGlobal;

    public JanelaGlobal(ClienteChat cliente, String login) {
        super("Chat global");
        painelGlobal = new PainelGlobal(cliente, login);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setSize(300, 400);
        setLocation(310,0);
        this.getContentPane().add(painelGlobal, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public PainelGlobal getPainelGlobal() {
        return this.painelGlobal;
    }
}
