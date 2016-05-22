/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.controller;

import br.lucasslf.axiomzen.dto.GameRequestDTO;
import br.lucasslf.axiomzen.dto.GuessDTO;
import br.lucasslf.axiomzen.dto.GameStatusDTO;
import br.lucasslf.axiomzen.service.MastermindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lucas Furtado
 */
@RestController
public class MastermindController {

    @Autowired
    private MastermindService service;

    @RequestMapping(value = "/game", method = RequestMethod.POST)
    public GameStatusDTO createGame(@RequestBody GameRequestDTO gameRequest) {
        return service.createGame(gameRequest.getUser(), gameRequest.getPlayers());
    }

    @RequestMapping(value = "/game/{key}/user/{user}", method = RequestMethod.POST)
    public GameStatusDTO joinGame(@PathVariable String key, @PathVariable String user) {
        return service.joinGame(key, user);
    }

    @RequestMapping(value = "/guess", method = RequestMethod.POST)
    public GameStatusDTO makeGuess(@RequestBody GuessDTO guess) {
        return service.makeGuess(guess);
    }
}
