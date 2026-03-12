import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
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

            // CONFIGURAÇÃO DO GSON: Estética + Números Grandes
            Gson gson = new GsonBuilder()
                .setPrettyPrinting() // Deixa o JSON indentado (bonitinho)
                .setNumberToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE) // Evita o "E12" nos números
                .create();

            String novoBlocoJson = args[0];
            
            // 1. Carrega o Bloco e a Chain oficial
            Map<String, Object> novoBloco = gson.fromJson(novoBlocoJson, new TypeToken<Map<String, Object>>(){}.getType());
            Path path = Paths.get("src/BlockChain.json");
            String content = new String(Files.readAllBytes(path));
            List<Map<String, Object>> chain = gson.fromJson(content, new TypeToken<List<Map<String, Object>>>(){}.getType());

            // 2. VERIFICAÇÃO 1: Integridade da Chain existente
            System.out.println("Analisando integridade da cadeia...");
            for (int i = 1; i < chain.size(); i++) {
                Map<String, Object> atual = chain.get(i);
                Map<String, Object> anterior = chain.get(i - 1);
                
                if (!atual.get("prevHash").equals(anterior.get("hash"))) {
                    System.err.println("ERRO: Elo quebrado no bloco " + i);
                    System.exit(1);
                }
            }

            // 3. VERIFICAÇÃO 2: O Novo Bloco aponta para o último oficial?
            Map<String, Object> ultimoOficial = chain.get(chain.size() - 1);
            if (!novoBloco.get("prevHash").equals(ultimoOficial.get("hash"))) {
                System.err.println("FRAUDE: prevHash do novo bloco não bate com o hash oficial.");
                System.exit(1);
            }

            // 4. VERIFICAÇÃO 3: Prova de Trabalho
            String hashNovo = (String) novoBloco.get("hash");
            if (!hashNovo.startsWith("00000")) {
                System.err.println("FRAUDE: Dificuldade insuficiente.");
                System.exit(1);
            }

            // 5. TUDO OK: Adiciona e Salva com formatação
            chain.add(novoBloco);
            String jsonFinal = gson.toJson(chain); // Aqui ele usa o Pretty Printing
            Files.write(path, jsonFinal.getBytes());
            
            System.out.println("CONSENSO ATINGIDO: Bloco registrado na nuvem de forma organizada!");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Erro Crítico no Validador: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}