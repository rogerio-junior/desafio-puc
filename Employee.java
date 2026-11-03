/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.puc_rio.tecgraf.challenge;

import java.util.Date;

/**
 *
 */
public abstract class Employee {
    
    private final String firstName;
    private final String lastName;
    private final String socialNumber;
    private final Date birthDate;
    private final Gender gender;
    
    public Employee(String firstName, String lastName, Integer socialNumber, Date birthDate, Gender gender){
        /*To-Do:
        * Preencher a lógica do construtor
        */
    }
    
    public abstract double computeSalary(Date referenceDate);
    /**
     * To-do
     * 
     * 1) Criar outros construtores adicionais se for necessario
     * 2) Incluir os métodos de acesso aos valores das variaveis privadas
     * 3) 
     *  
     */
    
    
    
}
