//GitHub - @Gabriel-Pestana44
/*
 * Copyright (c) 2026 [Gabriel Silva pestana]
 * * Este software foi desenvolvido como parte de estudos sobre o assunto.
 */
import java.util.Scanner;

import BlockChain.Block;
import BlockChain.BlockChain;

public class App {
    //Função pra limpar a tela
    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        //Instancia de objetos
        BlockChain bc = new BlockChain();
        Scanner sc = new Scanner(System.in);
        boolean rodando = true; 

        while (rodando) {
            limparTela();
            System.out.println("==========================================");
            System.out.println("             MINI  BLOCKCHAIN             ");
            System.out.println("==========================================");
            System.out.println(" 1. Iniciar Rede (Genesis)");
            System.out.println(" 2. Criar Novo Bloco Vazio");
            System.out.println(" 3. Adicionar Dado ao Bloco Atual");
            System.out.println(" 4. Minerar Bloco Atual");
            System.out.println(" 5. Exibir Blockchain");
            System.out.println(" 6. Consultar por Hash");
            System.out.println(" 7. Verificar Validade da BlockChain");
            System.out.println(" 8. Sair");
            System.out.println("==========================================");
            System.out.print(" Escolha uma opção: ");

            String opcao = sc.nextLine();
            
            switch (opcao) {
                
                case "pestana": // HACKER MODE  >:) hahahaha
                    bc.exibirBlockchain();
                    System.out.print(" Digite o Hash do bloco para adulterar: ");
                    String hashAlvo = sc.nextLine();
                    
                    // Busca o bloco para mostrar os dados atuais
                    Block bTemp = bc.buscarPorHash(hashAlvo);
                    if (bTemp != null) {
                        System.out.println("Dados atuais:");
                        for (int i = 0; i < bTemp.getDataList().size(); i++) {
                            System.out.println("[" + i + "] " + bTemp.getDataList().get(i));
                        }
                        
                        System.out.print(" Índice do dado que deseja alterar: ");
                        int idx = Integer.parseInt(sc.nextLine());
                        System.out.print(" Novo conteúdo: ");
                        String novo = sc.nextLine();
                        
                        bc.adulterarDadoPorHash(hashAlvo, idx, novo);
                    } else {
                        System.out.println("Bloco não encontrado.");
                    }
                    break;
                    //Inicia a rede manualmente
                case "1": 
                    if (bc.redeIniciada) {
                        System.out.println("Aviso: A rede já foi iniciada!");
                    } else {
                        bc.initChain();
                        System.out.println("Rede iniciada com sucesso!");
                    }
                    break;
                    //Cria Bloco
                case "2": bc.craftBlock(); break;
                //Inserir dados no bloco atual
                case "3": 
                    if (bc.getActiveBlock() == null) {
                        System.out.println("Erro: Nenhum bloco ativo. Crie um bloco primeiro!");
                    } else if (bc.getActiveBlock().getDataCount() >= bc.qData) {
                        System.out.println("Erro: Bloco cheio ("+bc.getActiveBlock().getDataCount()+"/"+bc.qData+").");
                    } else {
                        // Só pede os dados se tiver espaço
                        System.out.print(" Tipo: "); String t = sc.nextLine();
                        System.out.print(" Conteúdo: "); String v = sc.nextLine();
                        if(!bc.addInfo(t, v)){
                            System.out.print("\n Pressione Enter para continuar...");
                            sc.nextLine();
                            bc.closeAndMiner();
                        } 
                    }
                    break;
                    //Fecha o bloco, Minera, Valida e Liga a BlockChain
                case "4": bc.closeAndMiner(); break;
                //Exibe estado da BlockChain
                case "5": bc.exibirBlockchain(); break;
                //Busca Bloco por Hash
                case "6": 
                    System.out.print(" Digite o Hash: ");
                    Block b = bc.buscarPorHash(sc.nextLine());
                    if (b != null) b.exibirDetalhes();
                    else System.out.println(" Bloco não encontrado.");
                    break;
                    //Verifica integridade da BlockChain
                case "7": 
                    if (bc.verifyImmutability()) {
                        System.out.println("SISTEMA SEGURO: A Blockchain está íntegra.");
                    } else {
                        System.out.println("ALERTA: A Blockchain foi corrompida!");
                    }
                    break;
                    //Saida do programa
                case "8": 
                    rodando = false;
                    break;
                default: System.out.println(" Opção inválida!");
            }
            
            if (rodando) {
                System.out.print("\n Pressione Enter para continuar...");
                sc.nextLine();
            }
        }
        sc.close(); 
        System.out.println("Sistema encerrado.");
    }
}