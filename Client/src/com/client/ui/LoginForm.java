/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.ui;

import com.client.service.Communication;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author nhanlebka
 */
public class LoginForm extends javax.swing.JPanel {

    public Communication communication;

    /**
     * Creates new form LoginForm
     */
    public LoginForm(Communication communication) {
        initComponents();
        this.communication = communication;
        chBoxDisplayPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chBoxDisplayPassword.isSelected()) {
                    txtPassword.setEchoChar((char) 0);
                } else {
                    txtPassword.setEchoChar('*');
                }
            }
        });
        btnRegister.addActionListener(new LoginRegisterButton());
        btnLogin.addActionListener(new LoginRegisterButton());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JSplitPane();
        headerPanel = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        txtFieldUserName = new javax.swing.JTextField();
        lblUserName = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        btnRegister = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        chBoxDisplayPassword = new javax.swing.JCheckBox();
        lblerrorInform = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(480, 300));
        setMinimumSize(new java.awt.Dimension(480, 300));
        setPreferredSize(new java.awt.Dimension(480, 300));

        mainPanel.setDividerLocation(80);
        mainPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainPanel.setMaximumSize(new java.awt.Dimension(480, 300));
        mainPanel.setMinimumSize(new java.awt.Dimension(480, 300));
        mainPanel.setPreferredSize(new java.awt.Dimension(480, 300));

        headerPanel.setMaximumSize(new java.awt.Dimension(398, 80));
        headerPanel.setMinimumSize(new java.awt.Dimension(398, 80));
        headerPanel.setPreferredSize(new java.awt.Dimension(398, 80));

        headerLabel.setFont(new java.awt.Font("Cambria", 1, 30)); // NOI18N
        headerLabel.setText("Login");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(195, 195, 195)
                .addComponent(headerLabel)
                .addContainerGap(206, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(headerLabel)
                .addGap(21, 21, 21))
        );

        mainPanel.setTopComponent(headerPanel);

        contentPanel.setMaximumSize(new java.awt.Dimension(480, 220));
        contentPanel.setMinimumSize(new java.awt.Dimension(480, 220));
        contentPanel.setPreferredSize(new java.awt.Dimension(480, 220));

        txtFieldUserName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtFieldUserName.setMaximumSize(new java.awt.Dimension(180, 23));
        txtFieldUserName.setMinimumSize(new java.awt.Dimension(180, 23));
        txtFieldUserName.setPreferredSize(new java.awt.Dimension(180, 23));

        lblUserName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblUserName.setText("Username");

        lblPassword.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPassword.setText("Password");

        btnRegister.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnRegister.setText("Register");
        btnRegister.setMaximumSize(new java.awt.Dimension(100, 33));
        btnRegister.setMinimumSize(new java.awt.Dimension(100, 33));
        btnRegister.setPreferredSize(new java.awt.Dimension(100, 33));

        txtPassword.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPassword.setMaximumSize(new java.awt.Dimension(180, 23));
        txtPassword.setMinimumSize(new java.awt.Dimension(180, 23));
        txtPassword.setPreferredSize(new java.awt.Dimension(180, 23));

        btnLogin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnLogin.setText("Login");
        btnLogin.setMaximumSize(new java.awt.Dimension(100, 33));
        btnLogin.setMinimumSize(new java.awt.Dimension(100, 33));
        btnLogin.setPreferredSize(new java.awt.Dimension(100, 33));

        chBoxDisplayPassword.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chBoxDisplayPassword.setText("Display Password");

        lblerrorInform.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblerrorInform.setForeground(new java.awt.Color(255, 0, 0));
        lblerrorInform.setToolTipText("");

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addContainerGap(96, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chBoxDisplayPassword)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPassword)
                            .addComponent(lblUserName))
                        .addGap(24, 24, 24)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblerrorInform))))
                .addGap(117, 117, 117))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblerrorInform)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(chBoxDisplayPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );

        mainPanel.setRightComponent(contentPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JCheckBox chBoxDisplayPassword;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JLabel lblerrorInform;
    private javax.swing.JSplitPane mainPanel;
    private javax.swing.JTextField txtFieldUserName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables

    private class LoginRegisterButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String mess = null;
            String uname = txtFieldUserName.getText();
            String pwd = txtPassword.getPassword().toString();
            if (uname != "" && pwd != "") {
                mess += uname + "|" + pwd;
                if (e.getActionCommand().toLowerCase().equals("login")) {
                    mess = "0" + mess;
                    communication.send(mess);
                } else if (e.getActionCommand().toLowerCase().equals("register")) {
                    if(uname.contains("|") || pwd.contains("|")){
                        lblerrorInform.setText("Must not include special character!");
                    }
                    mess = "1" + mess;
                    communication.send(mess);
                }
            } else {
                lblerrorInform.setText("Two field must be filled");
            }
        }

    }
}
