package com.aps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class JanelaMulti extends JFrame {

    private final PainelMulti painelMulti;
    private final JButton btnAddContato;

    public JanelaMulti(ClienteChat cliente, ArrayList<String> contatos, String idConversa) {

        super(" @ " + idConversa);

        painelMulti = new PainelMulti(cliente, contatos, idConversa);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(200, 200);
        this.getContentPane().add(painelMulti, BorderLayout.CENTER);
        this.setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cliente.removeListenerMensagem(painelMulti);
            }
        });
        btnAddContato = new JButton("+");
        btnAddContato.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame f = new JFrame("Adicionar");
                JTextField txtNome = new JTextField();
                txtNome.setColumns(7);
                JButton btnAdd = new JButton("Adicionar");
                btnAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!txtNome.getText().equals("")) {
                            painelMulti.addContato(txtNome.getText());
                            f.setVisible(false);
                        }
                    }
                });
                f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                f.getContentPane().add(txtNome, BorderLayout.NORTH);
                f.getContentPane().add(btnAdd, BorderLayout.SOUTH);
                f.pack();
                f.setVisible(true);
            }
        });
        this.getContentPane().add(btnAddContato, BorderLayout.NORTH);
    }

    public PainelMulti getPainelMulti() {
        return this.painelMulti;
    }
}
