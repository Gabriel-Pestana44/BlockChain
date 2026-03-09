//GitHub - @Gabriel-Pestana44
/*
 * Copyright (c) 2026 [Gabriel Silva pestana]
 * * Este software foi desenvolvido como parte de estudos sobre o assunto.
 */
package BlockChain;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Block {    

    private int index;
    private long timestamp;
    private String hash;
    private String prevHash;
    private ArrayList<String> dataList;
    private int nonce;
    //Definição e construção do bloco, index = posiçao na cadeia,  timestamp = horario e data que foi ligado a Cadeia, 
    // hash = chave de validação,
    // presvhash = hash do bloco anterior, 
    // Data = dado do bloco, 
    // Nonce = tentativas de validaçao (miner)
    public Block( int index, long timestamp, String prevHash, String data){

        this.index =index;
        this.timestamp = timestamp;
        this.prevHash =prevHash;
        this.dataList =new ArrayList<>();
        nonce = 0;
        hash = Block.calcularHash(this);

    }

    public String str() {
        return index + Long.toString(timestamp) + prevHash + dataList.toString() + nonce;
    }
    //re-calcula Hash a cada dado inserido
    public void addData( String data ){

        dataList.add(data);
        this.hash = calcularHash(this);

    }
    //Atualisa o Hash atual do bloco 
    public void updateHash() {

        this.hash = calcularHash(this);
    
    }
    //Calcula o Hash de acordo com o modelo "SHA-256" Usando a funçao str que agrupa os dados,nonce,prevhash,timestamp e index
    public static String calcularHash(Block block){
        if (block != null){
            MessageDigest digest = null;
        
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        String txt = block.str();
        final byte bytes[] =digest.digest(txt.getBytes());
        final StringBuilder builder = new StringBuilder();

        for (final byte b: bytes){
            String hex = Integer.toHexString(0xff & b);
            
            if(hex.length() ==1 ){
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    return null;
    
    }
    //printa informação do bloco
    public void exibirDetalhes() {
    System.out.println("\n--- DETALHES DO BLOCO ---");
    System.out.println("Índice: " + index);
    System.out.println("Hash:   " + hash);
    System.out.println("Prev:   " + prevHash);
    System.out.println("Nonce:  " + nonce);
    System.out.println("Dados:  " + dataList);
    }

    // Getters e Setters necessários
    public void setNonce(int nonce) { this.nonce = nonce; }
    public void setHash(String hash) { this.hash = hash; }
    public int getIndex() {return index;}
    public int getNonce(){return nonce;}
    public String getHash() { return hash; }//retorna hash atual
    public String getPreviousHash() { return prevHash; }//retorna o hash do bloco anterior
    public int getDataCount() { return dataList.size(); }//retorna quantidade de dados no bloco
    public ArrayList<String> getDataList() { return dataList; }//retorna conteudo do bloco


}


            