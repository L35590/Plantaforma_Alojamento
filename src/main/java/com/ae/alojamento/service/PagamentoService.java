package com.ae.alojamento.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Locale;

@Service
public class PagamentoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Injeção de dependências do construtor
    public PagamentoService(RestTemplateBuilder builder, ObjectMapper objectMapper) {
        this.restTemplate = builder.build();
        this.objectMapper = objectMapper;
    }

    // Record para mapear a resposta JSON (Java 14+)
    public record DadosPagamento(String mb_entity, String mb_reference) {}

    /**
     * Contacta o serviço externo da Universidade para gerar referência MB.
     * * @param valor O montante a pagar (ex: 150.00).
     * @return DadosPagamento com Entidade e Referência.
     * Retorna dados simulados em caso de falha do serviço externo.
     */
    public DadosPagamento obterReferencia(Double valor) {
        // Formata para garantir ponto decimal (ex: "10.50")
        String valorFormatado = String.format(Locale.US, "%.2f", valor);
        String url = "https://magno.di.uevora.pt/tweb/t2/mbref4payment?amount=" + valorFormatado;

        try {
            // Configura headers para forçar resposta JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Executa o pedido HTTP GET
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String corpoResposta = response.getBody();

            // Validação de segurança simples: Se vier HTML (erro comum em proxies), lança exceção
            if (corpoResposta != null && corpoResposta.trim().startsWith("<")) {
                throw new RuntimeException("O servidor devolveu HTML em vez de JSON.");
            }

            return objectMapper.readValue(corpoResposta, DadosPagamento.class);

        } catch (Exception e) {
            // --- FALLBACK (PLANO B) ---
            // Garante que a aplicação não "crasha" se a internet falhar ou a API da UE estiver em baixo.
            System.err.println("⚠️ FALHA NO SERVIÇO EXTERNO: " + e.getMessage());
            return new DadosPagamento("12345 (Simulado)", "999 888 777");
        }
    }
}