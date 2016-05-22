/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.domain.builder;

import br.lucasslf.axiomzen.domain.Game;
import br.lucasslf.axiomzen.domain.GuessResult;

/**
 *
 * @author Lucas Furtado
 */
public class GuessResultBuilder {

    private final GuessResult guessResult = new GuessResult();

    private GuessResultBuilder() {
    }

    public static GuessResultBuilder newGuessResult() {
        return new GuessResultBuilder();
    }

    public GuessResultBuilder withResult(int exact, int near) {

        guessResult.setExact(exact);
        guessResult.setNear(near);
        return this;
    }

    public GuessResultBuilder withGuess(String guess) {
        guessResult.setGuess(guess);
        return this;
    }

    public GuessResultBuilder withGame(Game game) {
        guessResult.setGame(game);
        return this;
    }

    public GuessResult build() {
        return guessResult;
    }
}
