import BlockChain.BlockChain;

public class Validator {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO VALIDAÇÃO DE INTEGRIDADE DA REDE ===");
        
        BlockChain bc = new BlockChain(); 
        // O construtor já chama o loadData() que carrega o JSON
        
        if (bc.verifyImmutability()) {
            System.out.println(">>> SUCESSO: A Blockchain está íntegra e os hashes conferem.");
            System.exit(0); // Código 0 diz ao Linux/GitHub que está tudo bem
        } else {
            System.err.println(">>> ERRO CRÍTICO: Fraude detectada ou hashes inconsistentes!");
            System.exit(1); // Código 1 "quebra" a automação do GitHub
        }
    }
}