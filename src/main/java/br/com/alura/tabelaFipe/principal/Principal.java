package br.com.alura.tabelaFipe.principal;

import br.com.alura.tabelaFipe.model.DadosCarros;
import br.com.alura.tabelaFipe.model.Modelos;
import br.com.alura.tabelaFipe.service.ConsumoApi;
import br.com.alura.tabelaFipe.service.ConverteDados;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private static final String URL_CARROS = "https://parallelum.com.br/fipe/api/v1/carros/marcas";
    private static final String URL_MOTOS = "https://parallelum.com.br/fipe/api/v1/motos/marcas";
    private static final String URL_CAMINHOES = "https://parallelum.com.br/fipe/api/v1/caminhoes/marcas";

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {
        try {
            String tipoVeiculo = obterTipoVeiculo();
            String endereco = obterUrlBase(tipoVeiculo);

            // Obter e exibir marcas
            List<DadosCarros> marcas = obterMarcas(endereco);
            marcas.forEach(marca -> System.out.println(marca.codigo() + " - " + marca.nome()));

            System.out.println("\nInforme o código da marca para consulta:");
            String codigoMarca = leitura.nextLine();

            // Obter e exibir modelos
            List<DadosCarros> modelos = obterModelos(endereco, codigoMarca);
            System.out.println("\nOs modelos dessa marca são:");
            modelos.forEach(modelo -> System.out.println(modelo.codigo() + " - " + modelo.nome()));
        } catch (Exception e) {
            System.err.println("Ocorreu um erro: " + e.getMessage());
        }
    }

    private String obterTipoVeiculo() {
        System.out.println(
                """
                Digite o tipo de veículo para busca:
                Carro
                Moto
                Caminhão

                """
        );
        return leitura.nextLine().toLowerCase();
    }

    private String obterUrlBase(String tipoVeiculo) {
        if (tipoVeiculo.contains("carr")) {
            return URL_CARROS;
        } else if (tipoVeiculo.contains("mot")) {
            return URL_MOTOS;
        } else if (tipoVeiculo.contains("camin")) {
            return URL_CAMINHOES;
        } else {
            throw new IllegalArgumentException("Tipo de veículo inválido.");
        }
    }

    private List<DadosCarros> obterMarcas(String endereco) {
        String json = consumo.obterDados(endereco);
        return conversor.obterLista(json, DadosCarros.class)
                .stream()
                .sorted(Comparator.comparing(DadosCarros::nome))
                .toList();
    }

    private List<DadosCarros> obterModelos(String endereco, String codigoMarca) {
        String urlModelos = endereco + "/" + codigoMarca + "/modelos";
        String json = consumo.obterDados(urlModelos);
        Modelos modeloObjeto = conversor.obterDados(json, Modelos.class);
        return modeloObjeto.modelos()
                .stream()
                .sorted(Comparator.comparing(DadosCarros::nome))
                .toList();
    }
}
