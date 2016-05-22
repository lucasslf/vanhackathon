/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.domain.builder;

import br.lucasslf.axiomzen.domain.Game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Lucas Furtado
 */
public class GameBuilder {

    private final Game game = new Game();

    private GameBuilder() {
    }

    public static GameBuilder newGame() {
        return new GameBuilder();
    }

    public GameBuilder withRandomGameKey() {
        game.setGameKey(UUID.randomUUID().toString());
        return this;
    }

    public GameBuilder withGameKey(String key) {
        game.setGameKey(key);
        return this;
    }

    public GameBuilder solved() {
        game.setSolved(true);
        return this;
    }

    public GameBuilder withCreationDate(Date creationDate) {
        game.setCreation(creationDate);
        return this;
    }

    public GameBuilder withPlayers(int players) {
        game.setPlayers(players);
        return this;
    }

    public GameBuilder withCodeLength(int codeLength) {
        game.setCodeLength(codeLength);
        return this;
    }

    public GameBuilder createdNow() {
        game.setCreation(new Date());
        return this;
    }

    public GameBuilder withColors(String... colors) {
        game.setColors(Arrays.asList(colors));
        return this;
    }

    public GameBuilder withUser(String user) {
        List<String> users = new ArrayList<>();
        users.add(user);
        game.setUsers(users);
        return this;
    }

    public GameBuilder withSolution(String solution) {
        game.setSolution(solution);
        return this;
    }

    public Game build() {
        return game;
    }
}
