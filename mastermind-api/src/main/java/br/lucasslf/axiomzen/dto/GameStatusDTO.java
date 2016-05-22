/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.dto;

import br.lucasslf.axiomzen.domain.Game;
import br.lucasslf.axiomzen.domain.GuessResult;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lucas Furtado
 */
public class GameStatusDTO {

    private List<String> colors;
    private int codeLength;
    private String gameKey;
    private int numGuesses;
    private String message;
    private boolean solved;
    private String user;
    private List<ResultDTO> pastResults;
    private ResultDTO result;

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public int getNumGuesses() {
        return numGuesses;
    }

    public void setNumGuesses(int numGuesses) {
        this.numGuesses = numGuesses;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<ResultDTO> getPastResults() {
        return pastResults;
    }

    public void setPastResults(List<ResultDTO> pastResults) {
        this.pastResults = pastResults;
    }

    public ResultDTO getResult() {
        return result;
    }

    public void setResult(ResultDTO result) {
        this.result = result;
    }

    public static GameStatusDTO from(Game game) {
        GameStatusDTO dto = new GameStatusDTO();
        dto.setCodeLength(game.getCodeLength());
        dto.setColors(game.getColors());
        dto.setGameKey(game.getGameKey());
        dto.setNumGuesses(game.getNumGuesses());
        dto.setSolved(game.isSolved());
        List<ResultDTO> results = new ArrayList<>();
        game.getPastResults().stream().forEach((r) -> {
            results.add(new ResultDTO(r.getExact(), r.getNear(), r.getGuess(),r.getUser()));
        });
        dto.setPastResults(results);
        return dto;
    }
}
