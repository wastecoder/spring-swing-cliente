package com.javafx.javafxv1.ui;

import com.javafx.javafxv1.dto.ClienteDto;
import com.javafx.javafxv1.model.Cliente;
import com.javafx.javafxv1.service.ClienteService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Component
public class ClienteForm extends JFrame {

    private final ClienteService clienteService;
    private final Validator validator;

    private final JTextField nomeField = new JTextField();
    private final JLabel nomeErrorLabel = new JLabel();

    private final JFormattedTextField cpfField;
    private final JLabel cpfErrorLabel = new JLabel();
    private final JButton buscarCpfButton = new JButton("🔍");

    private final JFormattedTextField telefoneField;
    private final JLabel telefoneErrorLabel = new JLabel();

    private final JTextField emailField = new JTextField();
    private final JLabel emailErrorLabel = new JLabel();

    private final JButton salvarButton = new JButton("Salvar");
    private final JButton alterarButton = new JButton("Alterar");
    private final JButton excluirButton = new JButton("Excluir");
    private final JButton listarButton = new JButton("Listar Todos");
    private final JButton limparButton = new JButton("Limpar");
    private final JButton filtrarNomeButton = new JButton("🔍");

    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Telefone", "E-mail"}, 0);
    private final JTable clientesTable = new JTable(tableModel);

    private Cliente clienteAtual = null;

    @Autowired
    public ClienteForm(ClienteService clienteService, Validator validator) {
        this.clienteService = clienteService;
        this.validator = validator;

        // Máscaras
        JFormattedTextField cpf = null;
        JFormattedTextField tel = null;
        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            cpf = new JFormattedTextField(cpfMask);

            MaskFormatter telMask = new MaskFormatter("(##) #####-####");
            telMask.setPlaceholderCharacter('_');
            tel = new JFormattedTextField(telMask);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cpfField = cpf != null ? cpf : new JFormattedTextField();
        telefoneField = tel != null ? tel : new JFormattedTextField();

        // Estilo para labels de erro
        Color errorColor = Color.RED;
        Font errorFont = new Font("Arial", Font.PLAIN, 10);
        nomeErrorLabel.setForeground(errorColor); nomeErrorLabel.setFont(errorFont);
        cpfErrorLabel.setForeground(errorColor); cpfErrorLabel.setFont(errorFont);
        telefoneErrorLabel.setForeground(errorColor); telefoneErrorLabel.setFont(errorFont);
        emailErrorLabel.setForeground(errorColor); emailErrorLabel.setFont(errorFont);

        setTitle("Cadastro de Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 2, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 1.0;
        add(nomeField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        add(filtrarNomeButton, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        add(nomeErrorLabel, gbc);

        // CPF
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        add(cpfField, gbc);
        gbc.gridx = 2;
        add(buscarCpfButton, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        add(cpfErrorLabel, gbc);

        // Telefone
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(telefoneField, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        add(telefoneErrorLabel, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(emailField, gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        add(emailErrorLabel, gbc);

        // Botões principais
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 3;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.add(salvarButton);
        buttonsPanel.add(alterarButton);
        buttonsPanel.add(excluirButton);
        buttonsPanel.add(listarButton);
        buttonsPanel.add(limparButton);
        add(buttonsPanel, gbc);

        // Tabela
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 3; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        add(scrollPane, gbc);

        alterarButton.setEnabled(false);
        excluirButton.setEnabled(false);

        // Ações
        salvarButton.addActionListener(e -> salvarCliente());
        buscarCpfButton.addActionListener(e -> buscarClientePorCpf());
        filtrarNomeButton.addActionListener(e -> filtrarPorNome());
        alterarButton.addActionListener(e -> alterarCliente());
        excluirButton.addActionListener(e -> excluirCliente());
        listarButton.addActionListener(e -> listarClientes());
        limparButton.addActionListener(e -> limparCampos());

        clientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && clientesTable.getSelectedRow() != -1) {
                int row = clientesTable.getSelectedRow();
                Long id = (Long) clientesTable.getValueAt(row, 0);
                clienteService.listar().stream()
                        .filter(c -> c.getId().equals(id))
                        .findFirst()
                        .ifPresent(this::popularCampos);
            }
        });
    }

    private boolean validarComDto() {
        nomeErrorLabel.setText("");
        cpfErrorLabel.setText("");
        telefoneErrorLabel.setText("");
        emailErrorLabel.setText("");

        ClienteDto dto = new ClienteDto(
                nomeField.getText().trim(),
                cpfField.getText().trim(),
                telefoneField.getText().trim(),
                emailField.getText().trim()
        );

        Set<ConstraintViolation<ClienteDto>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<ClienteDto> v : violations) {
                switch (v.getPropertyPath().toString()) {
                    case "nome": nomeErrorLabel.setText(v.getMessage()); break;
                    case "cpf": cpfErrorLabel.setText(v.getMessage()); break;
                    case "telefone": telefoneErrorLabel.setText(v.getMessage()); break;
                    case "email": emailErrorLabel.setText(v.getMessage()); break;
                }
            }
            return false;
        }
        return true;
    }

    private void salvarCliente() {
        if (!validarComDto()) return;

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

    private void buscarClientePorCpf() {
        String cpf = cpfField.getText().trim();
        if (cpf.contains("_")) {
            JOptionPane.showMessageDialog(this, "Preencha o CPF completo.", "Aviso", JOptionPane.WARNING_MESSAGE);
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

    private void filtrarPorNome() {
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe parte do nome para filtrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Cliente> clientes = clienteService.buscarPorNome(nome);
        tableModel.setRowCount(0);
        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getTelefone(), c.getEmail()});
        }
    }

    private void alterarCliente() {
        if (clienteAtual == null || !validarComDto()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja alterar os dados do cliente?", "Confirmar Alteração", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            clienteAtual.setNome(nomeField.getText().trim());
            clienteAtual.setCpf(cpfField.getText().trim());
            clienteAtual.setTelefone(telefoneField.getText().trim());
            clienteAtual.setEmail(emailField.getText().trim());

            try {
                clienteService.salvar(clienteAtual);
                JOptionPane.showMessageDialog(this, "Cliente alterado com sucesso!");
                limparCampos();
                listarClientes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao alterar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirCliente() {
        if (clienteAtual == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir este cliente?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteService.deletar(clienteAtual);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                limparCampos();
                listarClientes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarClientes() {
        tableModel.setRowCount(0);
        for (Cliente c : clienteService.listar()) {
            tableModel.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getTelefone(), c.getEmail()});
        }
    }

    private void popularCampos(Cliente cliente) {
        nomeField.setText(cliente.getNome());
        cpfField.setText(cliente.getCpf());
        telefoneField.setText(cliente.getTelefone());
        emailField.setText(cliente.getEmail());
        clienteAtual = cliente;
        alterarButton.setEnabled(true);
        excluirButton.setEnabled(true);
        salvarButton.setEnabled(false);
    }

    private void limparCampos() {
        nomeField.setText("");
        cpfField.setValue(null);
        telefoneField.setValue(null);
        emailField.setText("");

        nomeErrorLabel.setText("");
        cpfErrorLabel.setText("");
        telefoneErrorLabel.setText("");
        emailErrorLabel.setText("");

        clienteAtual = null;
        alterarButton.setEnabled(false);
        excluirButton.setEnabled(false);
        salvarButton.setEnabled(true);
        clientesTable.clearSelection();
        tableModel.setRowCount(0);
    }
}
