import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.file.*;
import java.util.*;
import java.security.MessageDigest;

public class Validator {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("Erro: Nenhum bloco fornecido para validação.");
                System.exit(1);
            }

            Gson gson = new Gson();
            String novoBlocoJson = args[0];
            
            // 1. Carrega o Bloco sugerido e a Chain oficial do arquivo
            Map<String, Object> novoBloco = gson.fromJson(novoBlocoJson, new TypeToken<Map<String, Object>>(){}.getType());
            String content = new String(Files.readAllBytes(Paths.get("BlockChain.json")));
            List<Map<String, Object>> chain = gson.fromJson(content, new TypeToken<List<Map<String, Object>>>(){}.getType());

            // 2. VERIFICAÇÃO 1: Integridade de toda a Chain existente
            System.out.println("Analisando integridade da cadeia existente...");
            for (int i = 1; i < chain.size(); i++) {
                Map<String, Object> atual = chain.get(i);
                Map<String, Object> anterior = chain.get(i - 1);
                
                // Verifica se o elo anterior foi alterado
                if (!atual.get("previousHash").equals(anterior.get("hash"))) {
                    System.err.println("ERRO: Cadeia corrompida no bloco " + i);
                    System.exit(1);
                }
            }

            // 3. VERIFICAÇÃO 2: O Novo Bloco aponta para o último da Chain?
            Map<String, Object> ultimoOficial = chain.get(chain.size() - 1);
            if (!novoBloco.get("previousHash").equals(ultimoOficial.get("hash"))) {
                System.err.println("FRAUDE: O novo bloco não está encadeado no último bloco oficial.");
                System.exit(1);
            }

            // 4. VERIFICAÇÃO 3: Prova de Trabalho (Proof of Work)
            String hashNovo = (String) novoBloco.get("hash");
            if (!hashNovo.startsWith("00000")) {
                System.err.println("FRAUDE: Hash não possui a dificuldade necessária (00000).");
                System.exit(1);
            }

            // 5. TUDO OK: Adiciona o bloco e salva o arquivo
            chain.add(novoBloco);
            Files.write(Paths.get("BlockChain.json"), gson.toJson(chain).getBytes());
            System.out.println("CONSENSO ATINGIDO: Bloco validado e adicionado ao Ledger!");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Erro Crítico no Validador: " + e.getMessage());
            System.exit(1);
        }
    }
}