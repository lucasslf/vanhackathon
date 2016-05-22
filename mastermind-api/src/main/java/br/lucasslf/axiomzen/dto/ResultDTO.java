/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.dto;

/**
 *
 * @author Lucas Furtado
 */
public class ResultDTO {

    private int exact;
    private int near;
    private String guess;
    private String user;

    public ResultDTO() {
    }

    public ResultDTO(int exact, int near, String guess, String user) {
        this.exact = exact;
        this.near = near;
        this.guess = guess;
        this.user = user;
    }

    public int getExact() {
        return exact;
    }

    public void setExact(int exact) {
        this.exact = exact;
    }

    public int getNear() {
        return near;
    }

    public void setNear(int near) {
        this.near = near;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
