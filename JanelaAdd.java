package com.aps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class JanelaAdd extends JFrame {

    private final ClienteChat cliente;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    public JanelaAdd(ClienteChat cliente, DefaultListModel listModel) {

        super("Selecione");

        this.cliente = cliente;
        userListModel = listModel;
        userListUI = new JList<>(userListModel);

        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);
        setSize(200,200);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JLabel lblNome = new JLabel("Grupo:");
        JTextField txtId = new JTextField("");
        txtId.setColumns(10);
        JPanel pnlGrupo = new JPanel();
        pnlGrupo.add(lblNome);
        pnlGrupo.add(txtId);
        this.add(pnlGrupo, BorderLayout.NORTH);

        JButton btnAdd = new JButton("Adicionar");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se pelo menos um contato esta selecionado, deixa fazer operacao e o grupo tem nome
                if (!userListUI.isSelectionEmpty() && !(txtId.getText().equals(""))) {
                    new JanelaMulti(cliente, (ArrayList<String>) userListUI.getSelectedValuesList(), txtId.getText());
                    setVisible(false);
                }
            }
        });
        this.add(btnAdd, BorderLayout.SOUTH);

        setVisible(true);
    }
}
