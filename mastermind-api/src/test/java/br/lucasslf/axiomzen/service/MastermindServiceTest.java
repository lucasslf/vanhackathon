/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.service;

import br.lucasslf.axiomzen.MastermindApiApplication;
import br.lucasslf.axiomzen.dto.GuessDTO;
import br.lucasslf.axiomzen.dto.GameStatusDTO;
import br.lucasslf.axiomzen.exception.InvalidGuessException;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Lucas Furtado
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MastermindApiApplication.class)
public class MastermindServiceTest {

    @Autowired
    private MastermindService service;
    @Autowired
    private MessageSource messageSource;

    @Value("${code.length}")
    private int codeLength;

    @Test
    public void shouldNotAllowGuessWithWrongSizeCodeLength() {
        GuessDTO guess = new GuessDTO();
        guess.setGameKey("AAA");
        guess.setCode("RBG");
        guess.setUser("john");
        String expMess = messageSource.getMessage("guess.codelength.size", new Object[]{codeLength}, Locale.getDefault());
        try {
            service.makeGuess(guess);
        } catch (InvalidGuessException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllotGuessWithoutGameKey() {
        GuessDTO guess = new GuessDTO();
        guess.setCode("RBG");
        guess.setUser("john");
        String expMess = messageSource.getMessage("game.gamekey.required", null, Locale.getDefault());
        try {
            service.makeGuess(guess);
        } catch (InvalidGuessException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

    @Test
    public void shouldNotAllowGuessWithoutUser() {
        GuessDTO guess = new GuessDTO();
        guess.setCode("RRRRRRRR");
        guess.setGameKey("aaa");
        String expMess = messageSource.getMessage("guess.user.required", null, Locale.getDefault());
        try {
            service.makeGuess(guess);
        } catch (InvalidGuessException ex) {
            Assert.assertEquals(expMess, ex.getMessage());
        }
    }

}
