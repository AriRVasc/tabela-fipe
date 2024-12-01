package br.com.alura.tabelaFipe.principal;

import br.com.alura.tabelaFipe.model.DadosCarros;
import br.com.alura.tabelaFipe.model.Modelos;
import br.com.alura.tabelaFipe.model.Veiculo;
import br.com.alura.tabelaFipe.service.ConsumoApi;
import br.com.alura.tabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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

            // Filtrar modelos por nome
            List<DadosCarros> modelosFiltrados = filtrarModelosPorNome(modelos);

            // Obter avaliação por anos
            List<Veiculo> veiculos = buscarValoresAvaliacao(endereco, codigoMarca, modelosFiltrados);

            // Exibir veículos avaliados
            System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
            veiculos.forEach(System.out::println);

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

    private List<DadosCarros> filtrarModelosPorNome(List<DadosCarros> modelos) {
        System.out.println("\nDigite um trecho do nome do carro a ser buscado:");
        String nomeVeiculo = leitura.nextLine();
        List<DadosCarros> modelosFiltrados = modelos.stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados:");
        modelosFiltrados.forEach(modelo -> System.out.println(modelo.codigo() + " - " + modelo.nome()));

        return modelosFiltrados;
    }

    private List<Veiculo> buscarValoresAvaliacao(String enderecoBase, String codigoMarca, List<DadosCarros> modelosFiltrados) {
        System.out.println("\nDigite o código do modelo para buscar os valores de avaliação:");
        String codigoModelo = leitura.nextLine();

        String urlAnos = enderecoBase + "/" + codigoMarca + "/modelos/" + codigoModelo + "/anos";
        String jsonAnos = consumo.obterDados(urlAnos);
        List<DadosCarros> anos = conversor.obterLista(jsonAnos, DadosCarros.class);

        List<Veiculo> veiculos = new ArrayList<>();
        for (DadosCarros ano : anos) {
            String urlDetalhes = urlAnos + "/" + ano.codigo();
            String jsonDetalhes = consumo.obterDados(urlDetalhes);
            Veiculo veiculo = conversor.obterDados(jsonDetalhes, Veiculo.class);
            veiculos.add(veiculo);
        }
        return veiculos;
    }

}
