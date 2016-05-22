/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.domain;

import br.lucasslf.axiomzen.dto.GuessDTO;
import br.lucasslf.axiomzen.MastermindApiApplication;
import br.lucasslf.axiomzen.domain.builder.GameBuilder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Lucas Furtado
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MastermindApiApplication.class)
public class GameTest {
    @Value("${colors}")
    private String[] colors;

    @Value("${code.length}")
    private int codeLength;
    
    @Test
    public void mustReturnTrueWhenOlder() {
        LocalDateTime ldt = LocalDateTime.now().minusMinutes(100);
        Game game = GameBuilder.newGame().withCreationDate(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())).build();
        Assert.assertNotNull(game);
        Assert.assertTrue(game.isOlderThan(5));
    }

    @Test
    public void mustReturnTrueWhenSolved() {

        Game game = GameBuilder.newGame().createdNow().solved().build();
        Assert.assertNotNull(game);
        Assert.assertTrue(game.isSolved());
    }

    @Test
    public void mustEvaluateTrueWhenGuessIsValid() {

        Game game = GameBuilder.newGame().createdNow().withCodeLength(codeLength).withColors(colors).withGameKey("AAA").withSolution("RBGYOPCM").build();
        GuessDTO guess = new GuessDTO();
        guess.setCode("RBGYOPCM");
        guess.setGameKey("AAA");
        GuessResult guessResult = game.evaluate(guess.getCode());
        Assert.assertTrue(game.isSolved());
        Assert.assertEquals(8, guessResult.getExact());
    }

    @Test
    public void mustEvaluateFalseWhenGuessIsNotValid() {

        Game game = GameBuilder.newGame().createdNow().withCodeLength(codeLength).withColors(colors).withGameKey("AAA").withSolution("RBGYOPCM").build();
        GuessDTO guess = new GuessDTO();
        guess.setCode("OBGYOPCM");
        guess.setGameKey("AAA");
        GuessResult guessResult = game.evaluate(guess.getCode());
        Assert.assertFalse(game.isSolved());
    }

    @Test
    public void mustEvaluateMatches() {

        Game game = GameBuilder.newGame().createdNow().withCodeLength(codeLength).withColors(colors).withGameKey("AAA").withSolution("RBGYOPCM").build();
        GuessDTO guess = new GuessDTO();
        guess.setCode("MBGOYPCR");
        guess.setGameKey("AAA");
        GuessResult guessResult = game.evaluate(guess.getCode());
        Assert.assertFalse(game.isSolved());
        Assert.assertEquals(4,guessResult.getExact());
        Assert.assertEquals(4,guessResult.getNear());
    }

}
