//GitHub - @Gabriel-Pestana44
/*
 * Copyright (c) 2026 [Gabriel Silva pestana]
 * * Este software foi desenvolvido como parte de estudos sobre o assunto.
 */

package BlockChain;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//Caracteristicas da rede
public class BlockChain {

    private List<Block> chain;
    private Block activeBlock;
    private int dificult = 5;
    private final String FILE_NAME = "BlockChain.json";
    public int qData = 2;
    public boolean redeIniciada = false;

    
    public BlockChain() {
        chain = new ArrayList<>();
        loadData();
    }
    //Persistencia de Dados por Json
    //Salvar dados 
    public void save(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try (java.io.FileWriter writer = new java.io.FileWriter(FILE_NAME)){
            
            gson.toJson(this.chain, writer);
            System.out.println("Dados Registrados com Suceso!!");
        } catch (java.io.IOException e) {
            System.out.println("ERR: "+ e.getMessage());
        }
    }

    //Carregar dados
    public void loadData(){
        java.io.File file = new java.io.File(FILE_NAME);
        
        if (!file.exists())return;

        try (java.io.FileReader reader = new java.io.FileReader(FILE_NAME)) {

            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Block>>(){}.getType();
            this.chain = new Gson().fromJson(reader,listType);
            this.redeIniciada = !chain.isEmpty(); // Se tem blocos, a rede está iniciada
            this.activeBlock = null; // Sempre começa sem bloco ativo
            System.out.println("BlockChain Carregada (" + chain.size() + " blocos).");
            
        } catch (java.io.IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    //Cria a BlockChain
    public void initChain(){
        //Evita duplicaçao de Blockchain
        if(!chain.isEmpty()){
            System.out.println("Blockchain já existente carregada do disco. Use 'craftBlock' para continuar.");
            return;
        }

        // Bloco Genesis
        Block genesis = new Block(0, System.currentTimeMillis(), "0", "Genesis Block");
        minerBlock(genesis);
        chain.add(genesis);
        save();
        redeIniciada = true;
        activeBlock= null;
        System.out.println("Bloco Gênesis (#0) criado e registrado. Use 'Criar Bloco' para começar a inserir dados.");
    }
    //Cria novo bloco
    public void craftBlock() {
        if (!redeIniciada) {
            System.out.println("Inicie a rede primeiro!");
            return;
        }
        // O novo bloco aponta para o hash do último bloco minerado na chain
        String prevHash = chain.get(chain.size() - 1).getHash();
        activeBlock = new Block(chain.size(), System.currentTimeMillis(), prevHash, "");
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
        save();
        activeBlock = null; // Bloco passa a ser imutavel
        System.out.println("Bloco #" + (chain.size()-1) + " minerado e adicionado à cadeia.");
    }

    public boolean addInfo(String tipo, String valor) {

        if (activeBlock == null) {
        System.out.println("Erro: Não há bloco aberto! Crie um bloco primeiro.");
        return false;
        }

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


