package com.javafx.javafxv1.service;

import com.javafx.javafxv1.model.Cliente;
import com.javafx.javafxv1.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;


    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    public void deletar(Cliente cliente) {
        clienteRepository.delete(cliente);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public void popularDados() {
        salvar(new Cliente("Ana Maria Silva", "123.456.789-00", "(11) 91234-5678", "ana.silva@email.com"));
        salvar(new Cliente("João Pedro Souza", "987.654.321-99", "(21) 99876-5432", "joao.souza@email.com"));
        salvar(new Cliente("Carla Fernandes", "111.222.333-44", "(31) 91234-9876", "carla.fernandes@email.com"));
        salvar(new Cliente("Marcos Silva", "555.666.777-88", "(41) 91234-5678", "marcos.silva@email.com"));
    }

}
