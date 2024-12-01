package br.com.alura.tabelaFipe.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) //ignora os campos que faltam do json aqui na classe
public record DadosCarros( String codigo, String nome) {


}

