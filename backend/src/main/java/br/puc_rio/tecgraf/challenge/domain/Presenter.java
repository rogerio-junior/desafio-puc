package br.puc_rio.tecgraf.challenge.domain;

public interface Presenter<T, R> {
    R present(T input);
}
