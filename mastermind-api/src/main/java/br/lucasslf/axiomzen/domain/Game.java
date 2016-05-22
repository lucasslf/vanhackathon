/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.lucasslf.axiomzen.domain;

import br.lucasslf.axiomzen.domain.builder.GuessResultBuilder;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Lucas Furtado
 */
@Entity
public class Game implements Serializable {

    @ElementCollection
    private List<String> colors;
    @Column
    private int codeLength;
    @Id
    private String gameKey;
    @Column
    private int numGuesses;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER )
    private List<GuessResult> pastResults = new ArrayList<>();

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @Column
    private boolean solved;

    @ElementCollection
    private List<String> users = new ArrayList<>();

    @Column
    private String solution;

    @Column
    private int players;

    public Game() {

    }
    
    public void generateSolution() {
        StringBuilder sol = new StringBuilder(codeLength);
        Random random = new Random();
        random.ints(codeLength, 0, colors.size()).forEach(index -> sol.append(colors.get(index)));

        this.solution = sol.toString();
    }

    public boolean isOlderThan(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationLDT = LocalDateTime.ofInstant(creation.toInstant(), ZoneId.systemDefault());
        return creationLDT.plusMinutes(minutes).isBefore(now);
    }

    public GuessResult evaluate(String guessCode) {

        int matches = 0;

        int exact = 0;

        int near = 0;

        //Indexed by colorIndex (in colors list), value = number of color appearances. 
        int[] solutionColorCount = new int[colors.size()];
        int[] guessColorCount = new int[colors.size()];
        for (int i = 0; i < guessCode.length(); i++) {
            String s = guessCode.substring(i, i + 1);
            String g = solution.substring(i, i + 1);
            ++solutionColorCount[colors.indexOf(s)];
            ++guessColorCount[colors.indexOf(g)];
            
            //Exact match
            if (s.equals(g)) {
                exact++;
            }
        }

        //Number of matches is minimum amount of appearances of each color between guess and solution
        for (int i = 0; i < colors.size(); i++) {
            matches += Math.min(solutionColorCount[i], guessColorCount[i]);
        }

        //Near is total matches minus exact matches
        near = matches - exact;

        //Creating data object
        GuessResult gr = GuessResultBuilder.newGuessResult().withGame(this).withGuess(guessCode).withResult(exact, near).build();

        if (!solved) {
            this.setSolved(exact == codeLength);
        }

        pastResults.add(gr);
        numGuesses++;
        return gr;
    }

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

    public void addUser(String user) {
        users.add(user);
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

    public List<GuessResult> getPastResults() {
        return pastResults;
    }

    public void setPastResults(List<GuessResult> pastResults) {
        this.pastResults = pastResults;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

}
