package com.aps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class JanelaLogin extends JFrame {
    private final ClienteChat cliente;
    private JTextField fdLogin = new JTextField();
    private JPasswordField fdSenha = new JPasswordField();
    private JButton btnLogin = new JButton("Login");

    public JanelaLogin() {
        super("Login");

        this.cliente = new ClienteChat("localhost", 8818);
        cliente.conectar();

        setLocation(500,400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    fdLogin.setText("");
                    fdSenha.setText("");
                    cliente.logoff();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(fdLogin);
        p.add(fdSenha);
        p.add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fazLogin();
            }
        });

        getContentPane().add(p, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }

    private void fazLogin() {
        String login = fdLogin.getText();
        String pw = fdSenha.getText();

        try {
            if (cliente.login(login,pw)) {
                PainelContatos painelContatos = new PainelContatos(cliente);
                final JFrame frame = new JFrame("Lista de usuarios");
                frame.setSize(300,400);

                JanelaGlobal chatGlobal = new JanelaGlobal(cliente, login);

                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        if (JOptionPane.showConfirmDialog(frame,
                                "Voce quer mesmo sair?", "Sair",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                            try {
                                cliente.logoff();
                                System.exit(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                frame.getContentPane().add(new JScrollPane(painelContatos), BorderLayout.CENTER);

                JButton btnAdd = new JButton("Criar grupo");
                btnAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new JanelaAdd(cliente, painelContatos.getUserListModel());
                    }
                });
                frame.add(btnAdd, BorderLayout.SOUTH);

                frame.setVisible(true);

                setVisible(false);

            } else {
                JOptionPane.showMessageDialog(this, cliente.getMsgErro());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JanelaLogin janelaLogin = new JanelaLogin();
        janelaLogin.setVisible(true);
    }
}
