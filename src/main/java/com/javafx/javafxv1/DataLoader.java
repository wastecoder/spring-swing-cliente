package com.javafx.javafxv1;

import com.javafx.javafxv1.service.ClienteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClienteService clienteService;

    public DataLoader(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (clienteService.listar().isEmpty()) {
            clienteService.popularDados();
            System.out.println("Dados iniciais inseridos no banco.");
        }
    }
}
