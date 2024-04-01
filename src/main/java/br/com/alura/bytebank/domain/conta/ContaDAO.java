package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private final Connection connection;

    ContaDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insere(DadosAberturaConta dadosDaConta) throws SQLException {
        Cliente cliente = new Cliente(dadosDaConta.dadosCliente());
        Conta conta = new Conta(dadosDaConta.numero(), cliente);
        boolean success = false;

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());

            success = preparedStatement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == 1062) {
                System.err.printf("A conta %d já existe%n", conta.getNumero());
            } else {
                System.err.printf("Error code %d: Um erro de integridade inesperado aconteceu com a mensagem de erro = %s",
                        e.getErrorCode(),
                        e.getMessage());
            }
        } finally {
            connection.close();
        }
        return success;
    }

    public Set<Conta> listar() {
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                Cliente cliente = new Cliente(
                        new DadosCadastroCliente(nome, cpf, email));

                contas.add(new Conta(numero, cliente));
            }

            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contas;
    }

    public Conta listarPorNumero(Integer numero) {
        Conta conta = null;
        String sql = "SELECT * FROM conta WHERE numero = ?";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, numero);
            preparedStatement.setMaxRows(1);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                Integer numeroConta = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                conta = new Conta(numeroConta, saldo,
                        new Cliente(
                                new DadosCadastroCliente(nome, cpf, email)));
            }
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

}
