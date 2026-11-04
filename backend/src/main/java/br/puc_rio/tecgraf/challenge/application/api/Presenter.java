package br.puc_rio.tecgraf.challenge.application.api;

public interface Presenter<T, R> {
    R present();
}
