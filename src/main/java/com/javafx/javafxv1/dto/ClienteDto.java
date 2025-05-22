package com.javafx.javafxv1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteDto {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 a 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(
            regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "CPF deve estar no formato 000.000.000-00"
    )
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(
            regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
            message = "Telefone deve estar no formato (00) 00000-0000 ou (00) 0000-0000"
    )
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Size(min = 5, max = 50, message = "E-mail deve ter entre 5 a 50 caracteres")
    @Pattern(
            regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            message = "E-mail deve ser válido"
    )
    private String email;


    public ClienteDto(String nome, String cpf, String telefone, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
