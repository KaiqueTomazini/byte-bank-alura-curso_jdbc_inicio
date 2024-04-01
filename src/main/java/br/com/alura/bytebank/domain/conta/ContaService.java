package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaService {
    private final ConnectionFactory connectionFactory;

    public ContaService() {
        this.connectionFactory = new ConnectionFactory();
    }

    private final Set<Conta> contas = new HashSet<>();

    public Set<Conta> listarContasAbertas() {
        Connection connection = connectionFactory.connectDB();
        return new ContaDAO(connection).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public boolean abrir(DadosAberturaConta dadosDaConta) throws SQLException {
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

        conta.sacar(valor);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        conta.depositar(valor);
    }

    public void encerrar(Integer numeroDaConta) {
        Conta conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        contas.remove(conta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        Connection connection = connectionFactory.connectDB();
        Conta conta = new ContaDAO(connection).listarPorNumero(numero);
        if (conta != null)
            return conta;
        else
            throw new RegraDeNegocioException("Não existe conta cadastrada com esse número!");
    }
}
