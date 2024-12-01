package br.com.alura.tabelaFipe.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

public class ConverteDados implements IConverteDados {
    private ObjectMapper mapper = new ObjectMapper(); //desserializa ou serializa Json p/Class

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> obterLista(String json, Class<T> classe) {
        // Cria o tipo de coleção para List<T>
        CollectionType collectionType = mapper.getTypeFactory()
                .constructCollectionType(List.class, classe);
        try {
            // Usa o collectionType para desserializar o JSON em uma lista
            return mapper.readValue(json, collectionType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao desserializar a lista: " + e.getMessage(), e);
        }

    }
}