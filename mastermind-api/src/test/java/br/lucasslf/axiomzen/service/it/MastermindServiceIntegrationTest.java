/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.service.it;

import br.lucasslf.axiomzen.MastermindApiApplication;
import br.lucasslf.axiomzen.dto.GameStatusDTO;
import br.lucasslf.axiomzen.dto.GuessDTO;
import br.lucasslf.axiomzen.exception.InvalidGameException;
import br.lucasslf.axiomzen.exception.InvalidGuessException;
import br.lucasslf.axiomzen.service.MastermindService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 *
 * @author Lucas Furtado
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MastermindApiApplication.class)
@DatabaseSetup(MastermindServiceIntegrationTest.DATASET)
@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = {MastermindServiceIntegrationTest.DATASET})
@DirtiesContext
public class MastermindServiceIntegrationTest {

    protected static final String DATASET = "classpath:datasets/it-mastermindservice.xml";

    @Autowired
    MastermindService service;
    @Autowired
    private MessageSource messageSource;

    @Test
    public void mustReturnNewGameWithUser() {

        GameStatusDTO result = service.createGame("Mario", 1);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCodeLength());
        Assert.assertNotNull(result.getColors());
        Assert.assertNotNull(result.getGameKey());
        Assert.assertEquals("Mario", result.getUser());
    }

    @Test
    public void shouldNotAllowJoiningFullGame() {
        String expMess = messageSource.getMessage("game.multiplayer.full", null, Locale.getDefault());
        try {
            service.joinGame("aaa", "john");
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowJoiningExpiredGame() {
        String expMess = messageSource.getMessage("game.expired", null, Locale.getDefault());
        try {
            service.joinGame("ddd", "john");
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowJoiningGameTwice() {
        String expMess = messageSource.getMessage("game.multiplayer.user.alreadyjoined", null, Locale.getDefault());
        try {
            service.joinGame("ccc", "user1");
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldAllowJoiningGame() {
        service.joinGame("ccc", "user2");
    }

    @Test
    public void shouldNotAllowJoiningInexistentGame() {
        String expMess = messageSource.getMessage("game.notFound", null, Locale.getDefault());
        try {
            service.joinGame("zzz", "john");
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowJoiningOnePlayerGame() {
        String expMess = messageSource.getMessage("game.oneplayer", null, Locale.getDefault());
        try {
            service.joinGame("bbb", "john");
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowGuessingOnInexistentGame() {
        String expMess = messageSource.getMessage("game.notFound", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("zzz");
            guess.setUser("john");
            guess.setCode("YYYYYYYY");
            service.makeGuess(guess);
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowGuessingOnAnySinglePlayerSolvedGame() {
        String expMess = messageSource.getMessage("game.solved", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("eee");
            guess.setUser("user1");
            guess.setCode("YYYYYYYY");
            service.makeGuess(guess);
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowGuessingOnExpiredGame() {
        String expMess = messageSource.getMessage("game.expired", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("ddd");
            guess.setUser("user1");
            guess.setCode("YYYYYYYY");
            service.makeGuess(guess);
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowPlayerGuessTwiceOnSameTurnOnMultiplayerGame() {
        String expMess = messageSource.getMessage("guess.multiplayer.user.turn", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("aaa");
            guess.setUser("user1");
            guess.setCode("YYYYYYYY");
            service.makeGuess(guess);
        } catch (InvalidGuessException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldOnlyAllowGuessFromPlayerInGame() {
        String expMess = messageSource.getMessage("guess.user.invalid", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("aaa");
            guess.setUser("user3");
            guess.setCode("YYYYYYYY");
            service.makeGuess(guess);
        } catch (InvalidGuessException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldAllowPlayerGuessOnTurnOnMultiplayerGame() {
        String expMess = messageSource.getMessage("guess.nicetry", null, Locale.getDefault());
        GuessDTO guess = new GuessDTO();
        guess.setGameKey("aaa");
        guess.setUser("user2");
        guess.setCode("OYYYYYYY");
        GameStatusDTO gs = service.makeGuess(guess);
        Assert.assertEquals(expMess, gs.getMessage());
        Assert.assertEquals(2, gs.getPastResults().size());
    }

    @Test
    public void shouldNotAllowInvalidColorGuess() {
        String expMess = messageSource.getMessage("guess.color.invalid", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("aaa");
            guess.setUser("user2");
            guess.setCode("YYYZYYYY");
            service.makeGuess(guess);
        } catch (InvalidGuessException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowGuessingOnUnstartedGame() {
        String expMess = messageSource.getMessage("guess.multiplayer.all.users.joined", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("ccc");
            guess.setUser("user1");
            guess.setCode("YYYYYYYJ");
            service.makeGuess(guess);
        } catch (InvalidGuessException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowGuessingOnFinishedMultiplayerSolvedGame() {
        String expMess = messageSource.getMessage("game.solved", null, Locale.getDefault());
        try {
            GuessDTO guess = new GuessDTO();
            guess.setGameKey("fff");
            guess.setUser("user1");
            guess.setCode("YYYYYYYY");
            service.makeGuess(guess);
        } catch (InvalidGameException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void playerShouldWinWhenCodeIsCorrectAndGameIsNotSolved() {
        GuessDTO guessUser2 = new GuessDTO();
        guessUser2.setGameKey("aaa");
        guessUser2.setUser("user2");
        guessUser2.setCode("YYYYYYYY");
        GuessDTO guessUser1 = new GuessDTO();
        guessUser1.setGameKey("aaa");
        guessUser1.setUser("user1");
        guessUser1.setCode("OYYYYYYY");
        GameStatusDTO gsUser2 = service.makeGuess(guessUser2);
        GameStatusDTO gsUser1 = service.makeGuess(guessUser1);
        Assert.assertTrue(gsUser2.isSolved());
        Assert.assertEquals(8, gsUser2.getResult().getExact());
        Assert.assertEquals(4, gsUser2.getPastResults().size());
        String winMessage = messageSource.getMessage("game.win", null, Locale.getDefault());
        String loseMessage = messageSource.getMessage("game.lose", null, Locale.getDefault());
        Assert.assertEquals(winMessage, gsUser2.getMessage());
        Assert.assertEquals(loseMessage, gsUser1.getMessage());
    }
}
