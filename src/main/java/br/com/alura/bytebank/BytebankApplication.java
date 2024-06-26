package br.com.alura.bytebank;

import br.com.alura.bytebank.domain.RegraDeNegocioException;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;
import br.com.alura.bytebank.domain.conta.Conta;
import br.com.alura.bytebank.domain.conta.ContaService;
import br.com.alura.bytebank.domain.conta.DadosAberturaConta;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Set;

public class BytebankApplication {

    private static final ContaService service = new ContaService();
    private static final Scanner teclado = new Scanner(System.in).useDelimiter("\n");

    public static void main(String[] args) {
        byte opcao = exibirMenu();
        while (opcao != 8) {
            try {
                switch (opcao) {
                    case 1:
                        listarContas();
                        break;
                    case 2:
                        abrirConta();
                        break;
                    case 3:
                        encerrarConta();
                        break;
                    case 4:
                        consultarSaldo();
                        break;
                    case 5:
                        realizarSaque();
                        break;
                    case 6:
                        realizarDeposito();
                        break;
                    case 7:
                        realizarTransferencia();
                        break;
                }
            } catch (RegraDeNegocioException e) {
                System.out.println("Erro: " + e.getMessage());
                System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu");
                teclado.next();
            }
            opcao = exibirMenu();
        }

        System.out.println("Finalizando a aplicação.");
    }

    private static byte exibirMenu() {
        System.out.println("""
                BYTEBANK - ESCOLHA UMA OPÇÃO:
                1 - Listar contas abertas
                2 - Abertura de conta
                3 - Encerramento de conta
                4 - Consultar saldo de uma conta
                5 - Realizar saque em uma conta
                6 - Realizar depósito em uma conta
                7 - Realizar transferência
                8 - Sair
                """);
        return teclado.nextByte();
    }

    private static void listarContas() {
        System.out.println("Contas cadastradas:");
        Set<Conta> contas = service.listarContasAbertas();
        contas.forEach(System.out::println);

        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        teclado.next();
    }

    private static void abrirConta() {
        System.out.println("Digite o número da conta:");
        int numeroDaConta = teclado.nextInt();

        System.out.println("Digite o nome do cliente:");
        String nome = teclado.next();

        System.out.println("Digite o cpf do cliente:");
        String cpf = teclado.next();

        System.out.println("Digite o email do cliente:");
        String email = teclado.next();

        if (service.abrir(
                new DadosAberturaConta(numeroDaConta,
                        new DadosCadastroCliente(nome,
                                cpf,
                                email)))) {
            System.out.println("Conta aberta com sucesso!");
        } else System.out.println("Não foi possível abrir sua conta!");

        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        teclado.next();
    }

    private static void encerrarConta() {
        System.out.println("Digite o número da conta:");
        int numeroDaConta = teclado.nextInt();

        service.desativarConta(numeroDaConta);

        System.out.println("Conta encerrada com sucesso!");
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        teclado.next();
    }

    private static void consultarSaldo() {
        System.out.println("Digite o número da conta:");
        int numeroDaConta = teclado.nextInt();
        BigDecimal saldo = service.consultarSaldo(numeroDaConta);
        System.out.println("Saldo da conta: " + saldo);

        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        teclado.next();
    }

    private static void realizarSaque() {
        System.out.println("Digite o número da conta:");
        int numeroDaConta = teclado.nextInt();

        System.out.println("Digite o valor do saque:");
        BigDecimal valor = teclado.nextBigDecimal();

        service.realizarSaque(numeroDaConta, valor);
        System.out.println("Saque realizado com sucesso!");
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        teclado.next();
    }

    private static void realizarDeposito() {
        System.out.println("Digite o número da conta:");
        int numeroDaConta = teclado.nextInt();

        System.out.println("Digite o valor do depósito:");
        BigDecimal valor = teclado.nextBigDecimal();

        service.realizarDeposito(numeroDaConta, valor);

        System.out.println("Depósito realizado com sucesso!");
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        teclado.next();
    }

    private static void realizarTransferencia() {
        System.out.println("Digite o número da conta de Origem:");
        int numeroDaContaOrigem = teclado.nextInt();
        System.out.println("Digite o número da conta de Destino:");
        int numeroDaContaDestino = teclado.nextInt();

        System.out.println("Digite o valor a ser transferido:");
        BigDecimal valor = teclado.nextBigDecimal();

        service.realizarTransferencia(numeroDaContaOrigem, numeroDaContaDestino, valor);

        System.out.println("Transferência realizada com sucesso!");
        System.out.println("Pressione qualquer tecla e de ENTER para voltar ao menu principal");
        teclado.next();
    }
}
