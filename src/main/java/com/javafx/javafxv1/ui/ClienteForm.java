package com.javafx.javafxv1.ui;

import com.javafx.javafxv1.model.Cliente;
import com.javafx.javafxv1.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

@Component
public class ClienteForm extends JFrame {

    private final ClienteService clienteService;

    private final JTextField nomeField = new JTextField();
    private final JTextField cpfField = new JTextField();
    private final JTextField telefoneField = new JTextField();
    private final JTextField emailField = new JTextField();

    private final JButton salvarButton = new JButton("Salvar");
    private final JButton buscarButton = new JButton("Buscar");
    private final JButton alterarButton = new JButton("Alterar");
    private final JButton excluirButton = new JButton("Excluir");
    private final JButton listarButton = new JButton("Listar Todos");

    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Telefone", "E-mail"}, 0);
    private final JTable clientesTable = new JTable(tableModel);

    private Cliente clienteAtual = null;

    @Autowired
    public ClienteForm(ClienteService clienteService) {
        this.clienteService = clienteService;

        setTitle("Cadastro de Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels e campos
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(nomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cpfField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(telefoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(emailField, gbc);

        // Botões linha 4
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(buscarButton);
        buttonsPanel.add(alterarButton);
        buttonsPanel.add(excluirButton);
        buttonsPanel.add(listarButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonsPanel, gbc);

        // Tabela linha 5
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        add(scrollPane, gbc);

        // Desabilitar botões alterar e excluir inicialmente
        alterarButton.setEnabled(false);
        excluirButton.setEnabled(false);

        // Ações dos botões
        salvarButton.addActionListener(e -> salvarCliente());
        buscarButton.addActionListener(e -> buscarCliente());
        alterarButton.addActionListener(e -> alterarCliente());
        excluirButton.addActionListener(e -> excluirCliente());
        listarButton.addActionListener(e -> listarClientes());

        // Configuração da tabela para selecionar linha e popular campos
        clientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && clientesTable.getSelectedRow() != -1) {
                int row = clientesTable.getSelectedRow();
                Long id = (Long) clientesTable.getValueAt(row, 0);
                // Buscar cliente pelo ID (pode melhorar criando método, aqui simplifico)
                clienteService.listar().stream()
                        .filter(c -> c.getId().equals(id))
                        .findFirst()
                        .ifPresent(this::popularCampos);
            }
        });
    }

    private void salvarCliente() {
        if (!validarCampos()) return;

        Cliente cliente = new Cliente(
                nomeField.getText().trim(),
                cpfField.getText().trim(),
                telefoneField.getText().trim(),
                emailField.getText().trim()
        );

        try {
            clienteService.salvar(cliente);
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");
            limparCampos();
            listarClientes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarCliente() {
        String cpf = cpfField.getText().trim();
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o CPF para buscar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Optional<Cliente> clienteOpt = clienteService.buscarPorCpf(cpf);
        if (clienteOpt.isPresent()) {
            popularCampos(clienteOpt.get());
            clienteAtual = clienteOpt.get();
            alterarButton.setEnabled(true);
            excluirButton.setEnabled(true);
            salvarButton.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Cliente não encontrado.", "Info", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        }
    }

    private void alterarCliente() {
        if (clienteAtual == null) {
            JOptionPane.showMessageDialog(this, "Busque um cliente antes de alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;

        int resp = JOptionPane.showConfirmDialog(this, "Confirma alteração do cliente?", "Confirmar Alteração", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            clienteAtual.setNome(nomeField.getText().trim());
            clienteAtual.setCpf(cpfField.getText().trim());
            clienteAtual.setTelefone(telefoneField.getText().trim());
            clienteAtual.setEmail(emailField.getText().trim());

            try {
                clienteService.salvar(clienteAtual);
                JOptionPane.showMessageDialog(this, "Cliente alterado com sucesso!");
                limparCampos();
                listarClientes();
                alterarButton.setEnabled(false);
                excluirButton.setEnabled(false);
                salvarButton.setEnabled(true);
                clienteAtual = null;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao alterar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirCliente() {
        if (clienteAtual == null) {
            JOptionPane.showMessageDialog(this, "Busque um cliente antes de excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this, "Confirma exclusão do cliente?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            try {
                clienteService.deletar(clienteAtual);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                limparCampos();
                listarClientes();
                alterarButton.setEnabled(false);
                excluirButton.setEnabled(false);
                salvarButton.setEnabled(true);
                clienteAtual = null;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarClientes() {
        List<Cliente> clientes = clienteService.listar();

        tableModel.setRowCount(0);
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getTelefone(), c.getEmail()});
        }
    }

    private void popularCampos(Cliente cliente) {
        nomeField.setText(cliente.getNome());
        cpfField.setText(cliente.getCpf());
        telefoneField.setText(cliente.getTelefone());
        emailField.setText(cliente.getEmail());
    }

    private void limparCampos() {
        nomeField.setText("");
        cpfField.setText("");
        telefoneField.setText("");
        emailField.setText("");
        clienteAtual = null;
        alterarButton.setEnabled(false);
        excluirButton.setEnabled(false);
        salvarButton.setEnabled(true);
        clientesTable.clearSelection();
    }

    private boolean validarCampos() {
        if (nomeField.getText().trim().isEmpty() ||
                cpfField.getText().trim().isEmpty() ||
                telefoneField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
