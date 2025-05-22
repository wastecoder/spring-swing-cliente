package com.javafx.javafxv1.ui;

import com.javafx.javafxv1.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class MainWindow extends JFrame {

    private final MessageService messageService;

    @Autowired
    public MainWindow(MessageService messageService) {
        this.messageService = messageService;

        setTitle("Swing + Spring");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // centraliza na tela

        JLabel label = new JLabel(messageService.getMessage(), SwingConstants.CENTER);
        add(label);
    }
}
