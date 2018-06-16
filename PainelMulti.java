package com.aps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class PainelMulti extends JPanel implements ListenerMensagem {

    private ArrayList<String> lstContatos;
    private final ClienteChat cliente;

    private DefaultListModel<String> lstModelo = new DefaultListModel<>();
    private JList<String> lstMsg = new JList<>(lstModelo);
    private JTextField fdInput = new JTextField();
    private final String idConversa;

    public PainelMulti(ClienteChat cliente, ArrayList<String> contatos, String idConversa) {
        this.cliente = cliente;
        this.lstContatos = contatos;
        this.idConversa = idConversa;

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
                        cliente.msgMulti(lstContatos, texto, idConversa);
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

        if (tipo.equalsIgnoreCase(idConversa)) {
            for (String contato : contatos) {
                if (!lstContatos.contains(contato)) {
                    lstContatos.add(contato);
                }
            }
            String linha = quemEnviou + ": " + msgCorpo;
            lstModelo.addElement(linha);
            return true;
        }
        return false;
    }

    public ArrayList<String> getListaLogins() {
        return lstContatos;
    }
    public void addContato(String login) {
        if (!lstContatos.contains(login)) {
            lstContatos.add(login);
        }
    }
    public void removeContato(String login) {
        lstContatos.remove(login);
    }
}
