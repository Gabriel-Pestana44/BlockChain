import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.file.*;
import java.util.*;

public class Validator {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("Erro: Nenhum bloco fornecido.");
                System.exit(1);
            }

            Gson gson = new Gson();
            String novoBlocoJson = args[0];
            
            // 1. Carrega o Bloco e a Chain oficial de src/BlockChain.json
            Map<String, Object> novoBloco = gson.fromJson(novoBlocoJson, new TypeToken<Map<String, Object>>(){}.getType());
            Path path = Paths.get("src/BlockChain.json");
            String content = new String(Files.readAllBytes(path));
            List<Map<String, Object>> chain = gson.fromJson(content, new TypeToken<List<Map<String, Object>>>(){}.getType());

            // 2. VERIFICAÇÃO 1: Integridade da Chain existente
            System.out.println("Analisando integridade da cadeia...");
            for (int i = 1; i < chain.size(); i++) {
                Map<String, Object> atual = chain.get(i);
                Map<String, Object> anterior = chain.get(i - 1);
                
                // USANDO "prevHash" PARA COMBINAR COM SEU JSON
                if (!atual.get("prevHash").equals(anterior.get("hash"))) {
                    System.err.println("ERRO: Elo quebrado no bloco " + i);
                    System.exit(1);
                }
            }

            // 3. VERIFICAÇÃO 2: O Novo Bloco aponta para o último oficial?
            Map<String, Object> ultimoOficial = chain.get(chain.size() - 1);
            
            // AJUSTE AQUI: Mudado de "previousHash" para "prevHash"
            if (!novoBloco.get("prevHash").equals(ultimoOficial.get("hash"))) {
                System.err.println("FRAUDE: prevHash do novo bloco não bate com o hash oficial.");
                System.exit(1);
            }

            // 4. VERIFICAÇÃO 3: Prova de Trabalho (Dificuldade 5)
            String hashNovo = (String) novoBloco.get("hash");
            if (!hashNovo.startsWith("00000")) {
                System.err.println("FRAUDE: Dificuldade insuficiente.");
                System.exit(1);
            }

            // 5. TUDO OK: Adiciona e Salva em src/BlockChain.json
            chain.add(novoBloco);
            Files.write(path, gson.toJson(chain).getBytes());
            System.out.println("CONSENSO ATINGIDO: Bloco registrado na nuvem!");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Erro Crítico no Validador: " + e.getMessage());
            e.printStackTrace(); // Isso ajuda a gente a ver a linha exata se falhar
            System.exit(1);
        }
    }
}