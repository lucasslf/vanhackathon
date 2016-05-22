/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.service;

import br.lucasslf.axiomzen.domain.Game;
import br.lucasslf.axiomzen.domain.GuessResult;
import br.lucasslf.axiomzen.dto.GuessDTO;
import br.lucasslf.axiomzen.domain.builder.GameBuilder;
import br.lucasslf.axiomzen.dto.GameStatusDTO;
import br.lucasslf.axiomzen.dto.ResultDTO;
import br.lucasslf.axiomzen.exception.InvalidGameException;
import br.lucasslf.axiomzen.exception.InvalidGuessException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lucas Furtado
 */
@Component
public class MastermindService {

    //From application.properties
    @Value("${colors}")
    private String[] colors;
    //From application.properties
    @Value("${code.length}")
    private int codeLength;
    //From application.properties
    @Value("${guess.expirationtime}")
    private int expirationTime;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    @Transactional
    public GameStatusDTO createGame(String user, int players) {
        Game game = GameBuilder.newGame().createdNow().withPlayers(players).withRandomGameKey().withUser(user).withCodeLength(codeLength).withColors(colors).build();
        game.generateSolution();
        entityManager.persist(game);
        GameStatusDTO dto = GameStatusDTO.from(game);
        dto.setUser(user);
        return dto;

    }

    @Transactional
    public GameStatusDTO joinGame(String gameKey, String user) {
        //Load game
        Game game = entityManager.find(Game.class, gameKey);

        //Validate game
        if (game == null) {
            throw new InvalidGameException(messageSource.getMessage("game.notFound", new Object[]{codeLength}, Locale.getDefault()));
        }
        if (game.getPlayers() == 1) {
            throw new InvalidGameException(messageSource.getMessage("game.oneplayer", new Object[]{codeLength}, Locale.getDefault()));
        }
        if (game.getUsers().size() == game.getPlayers()) {

            throw new InvalidGameException(messageSource.getMessage("game.multiplayer.full", new Object[]{codeLength}, Locale.getDefault()));
        }

        if (game.getUsers().contains(user)) {
            throw new InvalidGameException(messageSource.getMessage("game.multiplayer.user.alreadyjoined", new Object[]{codeLength}, Locale.getDefault()));
        }

        if (game.isSolved()) {
            throw new InvalidGameException(messageSource.getMessage("game.solved", new Object[]{codeLength}, Locale.getDefault()));
        }
        if (game.isOlderThan(expirationTime)) {
            throw new InvalidGameException(messageSource.getMessage("game.expired", new Object[]{codeLength}, Locale.getDefault()));
        }

        //Add user to game
        game.addUser(user);
        //Persisting game
        entityManager.persist(game);

        GameStatusDTO dto = GameStatusDTO.from(game);
        dto.setUser(user);
        return dto;

    }

    @Transactional
    public GameStatusDTO makeGuess(GuessDTO guess) {

        //Validate guess
        if (guess.getGameKey() == null || guess.getGameKey().isEmpty()) {
            throw new InvalidGuessException(messageSource.getMessage("game.gamekey.required", null, Locale.getDefault()));
        }

        if (guess.getCode().length() != codeLength) {
            throw new InvalidGuessException(messageSource.getMessage("guess.codelength.size", new Object[]{codeLength}, Locale.getDefault()));
        }
        if (guess.getUser() == null || guess.getUser().isEmpty()) {

            throw new InvalidGuessException(messageSource.getMessage("guess.user.required", new Object[]{codeLength}, Locale.getDefault()));
        }
        //Load game
        Game game = entityManager.find(Game.class, guess.getGameKey());

        //Validate game
        if (game == null) {
            throw new InvalidGameException(messageSource.getMessage("game.notFound", new Object[]{codeLength}, Locale.getDefault()));
        }
        if (game.isSolved() && game.getUsers().size() == 1) {
            throw new InvalidGameException(messageSource.getMessage("game.solved", new Object[]{codeLength}, Locale.getDefault()));
        }
        if (game.isOlderThan(expirationTime)) {
            throw new InvalidGameException(messageSource.getMessage("game.expired", new Object[]{codeLength}, Locale.getDefault()));

        }
        if (game.getUsers().size() != game.getPlayers()) {

            throw new InvalidGuessException(messageSource.getMessage("guess.multiplayer.all.users.joined", new Object[]{codeLength}, Locale.getDefault()));
        }
        if (!game.getUsers().contains(guess.getUser())) {

            throw new InvalidGuessException(messageSource.getMessage("guess.user.invalid", new Object[]{codeLength}, Locale.getDefault()));
        }

        //Validate guess colors
        for (int ii = 0; ii < guess.getCode().length(); ii++) {
            String color = guess.getCode().substring(ii, ii + 1);
            if (!game.getColors().contains(color)) {
                throw new InvalidGuessException(messageSource.getMessage("guess.color.invalid", new Object[]{codeLength}, Locale.getDefault()));
            }
        }
        //Filter user's past results
        List<GuessResult> userResults = game.getPastResults().stream().filter(r -> r.getUser().equals(guess.getUser())).collect(Collectors.toList());
        //Validate user turn
        if (userResults.size() > (game.getNumGuesses() / game.getUsers().size())) {
            if (!game.isSolved()) {
                throw new InvalidGuessException(messageSource.getMessage("guess.multiplayer.user.turn", new Object[]{codeLength}, Locale.getDefault()));
            } else {
                throw new InvalidGameException(messageSource.getMessage("game.solved", new Object[]{codeLength}, Locale.getDefault()));

            }
        }

        //Evaluate guess
        GuessResult guessResult = game.evaluate(guess.getCode());
        
        guessResult.setUser(guess.getUser());
        userResults.add(guessResult);
        //Persisting guess result (past results)
        entityManager.persist(guessResult);

        //Persisting game
        entityManager.persist(game);
        String message = "";
        if (game.isSolved()) {
            if (guessResult.getExact() == game.getCodeLength()) {
                message = messageSource.getMessage("game.win", null, Locale.getDefault());
            } else {
                message = messageSource.getMessage("game.lose", null, Locale.getDefault());
            }
        } else {
            message = messageSource.getMessage("guess.nicetry",null, Locale.getDefault());
            game.setPastResults(userResults);
        }
        GameStatusDTO dto = GameStatusDTO.from(game);
        dto.setUser(guess.getUser());
        dto.setResult(new ResultDTO(guessResult.getExact(), guessResult.getNear(), guess.getCode(), guess.getUser()));
        dto.setUser(guess.getUser());
        dto.setMessage(message);
        return dto;
    }

}
