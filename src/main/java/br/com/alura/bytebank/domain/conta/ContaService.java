package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;

public class ContaService {
    private final ConnectionFactory connectionFactory;

    public ContaService() {
        this.connectionFactory = new ConnectionFactory();
    }

    public Set<Conta> listarContasAbertas() {
        Connection connection = connectionFactory.connectDB();
        return new ContaDAO(connection).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public boolean abrir(DadosAberturaConta dadosDaConta) {
        Connection connection = connectionFactory.connectDB();
        return new ContaDAO(connection).insere(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        if (!conta.getEstaAtiva()) {
            throw new RegraDeNegocioException("Para realizar um saque a conta deve estar ativa");
        }

        Connection connection = connectionFactory.connectDB();

        new ContaDAO(connection).alterar(numeroDaConta, conta.getSaldo().subtract(valor));
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }
        if (!conta.getEstaAtiva()) {
            throw new RegraDeNegocioException("Para realizar um deposito a conta deve estar ativa");
        }

        Connection connection = connectionFactory.connectDB();
        new ContaDAO(connection).alterar(numeroDaConta, conta.getSaldo().add(valor));
    }

    public void desativarConta(Integer numeroDaConta) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }
        Connection connection = connectionFactory.connectDB();
        new ContaDAO(connection).alterarLogico(numeroDaConta);
    }

    /*public void encerrar(Integer numeroDaConta) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }
        Connection connection = connectionFactory.connectDB();
        new ContaDAO(connection).deletar(numeroDaConta);
    }*/

    private Conta buscarContaPorNumero(Integer numero) {
        Connection connection = connectionFactory.connectDB();
        Conta conta = new ContaDAO(connection).listarPorNumero(numero);
        if (conta != null)
            return conta;
        else
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
    }

    public void realizarTransferencia(Integer numeroContaOrigem, Integer numeroContaDestino, BigDecimal valor) {
        this.realizarSaque(numeroContaOrigem, valor);
        this.realizarDeposito(numeroContaDestino, valor);
    }
}
