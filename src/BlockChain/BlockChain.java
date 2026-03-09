//GitHub - @Gabriel-Pestana44
/*
 * Copyright (c) 2026 [Gabriel Silva pestana]
 * * Este software foi desenvolvido como parte de estudos sobre o assunto.
 */

package BlockChain;
import java.util.ArrayList;
import java.util.List;

//Caracteristicas da rede
public class BlockChain {

    private List<Block> chain;
    private Block activeBlock;
    private int dificult = 5;
    public int qData = 2;
    public boolean redeIniciada = false;

    
    public BlockChain() {
        chain = new ArrayList<>();
    }

    //Cria a BlockChain
    public void initChain(){
        // Bloco Genesis
        Block genesis = new Block(0, System.currentTimeMillis(), "0", "Genesis Block");
        minerBlock(genesis);
        chain.add(genesis);
        System.out.println("Primeiro Bloco Pra Ediçao Criado!!");
        
        // Prepara o primeiro bloco vazio 
        activeBlock = new Block(1, System.currentTimeMillis(), genesis.getHash(), "");
        redeIniciada = true;
    }
    //Cria novo bloco
    public void craftBlock() {
        if (!redeIniciada) {
            System.out.println("Inicie a rede primeiro!");
            return;
        }
        // O novo bloco aponta para o hash do último bloco minerado na chain
        activeBlock = new Block(chain.size(), System.currentTimeMillis(), 
                      chain.get(chain.size() - 1).getHash(), "");
        System.out.println("Bloco #" + activeBlock.getIndex() + " criado com sucesso!");
    }
    //Fecha, Minera e Encadeia
    public void closeAndMiner() {
        if (activeBlock == null) {
            System.out.println("Não há bloco ativo para minerar!");
            return;
        }
        minerBlock(activeBlock);
        chain.add(activeBlock);
        activeBlock = null; // Bloco passa a ser imutavel
        System.out.println("Bloco #" + (chain.size()-1) + " minerado e adicionado à cadeia.");
    }

    public boolean addInfo(String tipo, String valor) {

        // Formata o dado como se fosse um log
        String info = "TIPO: " + tipo + " | DADO: " + valor + "\n";
        activeBlock.addData(info);
        System.out.println("Dado adicionado! Blocos acumulados: " + activeBlock.getDataCount());

        if (activeBlock.getDataCount() >= qData){ 
            System.out.println("--------------------------------------------------");
            System.out.println("AVISO: O bloco atingiu o limite.");
            System.out.println("--------------------------------------------------");
            return false;
        }
        return true;//gambiarra
    }

    //Funçao, para minerar e criar novo bloco ( nao sei se vou implementar)
    /*public void mineCraft() {
        if (!redeIniciada) return;
        
        minerBlock(activeBlock);
        chain.add(activeBlock);
        
        // Cria um novo container limpo
        activeBlock = new Block(chain.size(), System.currentTimeMillis(), activeBlock.getHash(), "");
        System.out.println("Novo bloco criado e pronto para uso.");
    }*/
    
    //Minera o bloco de acordo com a Dificuldade
    public void minerBlock(Block block){

        //Definir alvo
        String alvo = new String(new char[dificult]).replace("\0", "0");
        System.out.println("Iniciando mineração do Bloco " + block.getIndex() + "...");

        //Loop de mineração. Condição de saida: hash do bloco começar com o numero de dificuldade (atual:00000)
        while (!block.getHash().substring(0,dificult).equals(alvo)) {
            
            block.setNonce(block.getNonce()+1);
            block.updateHash();
            
            if (block.getNonce() % 100 == 0) {
                System.out.println("Minerando... tentativa: " + block.getNonce());
            }
        }
        System.out.println("\n\nBloco menerado com sucesso!!\nHASH:"+ block.getHash() + "\nNONCE:" + block.getNonce());
    }

    public void exibirBlockchain() {
        System.out.println("\n--- BLOCOS MINERADOS ---");
        System.out.printf("%-5s | %-64s | %-10s%n", "Index", "Hash", "Dados");
        for (Block b : chain) {
            System.out.printf("%-5d | %-64s | %-10d%n", b.getIndex(), b.getHash(), b.getDataCount());
        }
    }

    public Block buscarPorHash(String hash) {
    // Adicionamos a verificação: só checa o activeBlock se ele NÃO for nulo
    if (activeBlock != null && activeBlock.getHash().equals(hash)) {
        return activeBlock;
    }
    
    // Procura na chain normalmente
    for (Block b : chain) {
        if (b.getHash().equals(hash)) return b;
    }
    return null;
}

    public boolean verifyImmutability() {
    for(int i = 1; i < chain.size(); i++){
        Block atual = chain.get(i);
        Block anterior = chain.get(i-1);

        //Verifica se os dados internos foram adulterados
        String hashCalculado = Block.calcularHash(atual);
        if(!atual.getHash().equals(Block.calcularHash(atual))){
            System.err.println("!!! ALERTA DE INTRUSÃO - BLOCO " + i + " !!!");
            System.err.println("-> Hash original (Minerado): " + atual.getHash());
            System.err.println("-> Hash recalculado (Fraude detectada): " + hashCalculado);
            System.err.println("-> Conteúdo atual dos dados: " + atual.getDataList());
            System.err.println("Ação: O conteúdo deste bloco foi alterado após a mineração!");
            return false;
        }

        //Verifica se o elo (prevHash) aponta para o hash do bloco anterior real
        if(!atual.getPreviousHash().equals(anterior.getHash())){
            System.err.println("!!! ELO DA CORRENTE ROMPIDO NO BLOCO " + i + " !!!");
            System.err.println("-> O bloco " + i + " parou de apontar para o hash correto do anterior.");
            return false;
        }
    }
    return true;
}
    // Gera uma rede de testes automática
    public void gerarRedeTeste() {
        initChain();
        for (int i = 0; i < 10; i++) {
            activeBlock.addData("DADO TESTE " + i);
            closeAndMiner();
            craftBlock();
        }
        System.out.println("Rede de testes gerada com 3 blocos!");
    }
    //Isso foi chato de implementar
    // O "Modo Hacker": Altera o conteúdo de um bloco (index) sem refazer o hash
    public void adulterarDadoPorHash(String hash, int indexDado, String novoDado) {
        Block b = buscarPorHash(hash);
            if (b == null) {
                System.out.println("Bloco não encontrado!");
                return;
            }
            
            List<String> lista = b.getDataList();
            if (indexDado >= 0 && indexDado < lista.size()) {
                lista.set(indexDado, novoDado);
                System.out.println(">>> DADO ADULTERADO COM SUCESSO!");
            } else {
                System.out.println("Erro: Índice do dado inválido! O bloco tem " + lista.size() + " dados.");
            }
    }
    // Getters e Setters
    public Block getActiveBlock() {
        return activeBlock;
    }
}


