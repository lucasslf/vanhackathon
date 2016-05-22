VanHackathon
=======
Axiom Zen - Mastermind API
-----------

A Rest API to play Mastermind

### Introduction

In this challenge we were asked to develop a server side API to play Mastermind (https://en.wikipedia.org/wiki/Mastermind_(board_game)) against.

I decided to design and develop a robust, well tested and scalable solution, and in order to achieve those features with productivity, I have chosen to use Java and Spring-Boot.

### The API

The API is a simple REST API, with two resources: Game and Guess.
With the first resource it is possible to post a new Game, informing user and number of players, and to post a new User into a Game, when it is a multiplayer game.
With the Guess resource it is possible to post a new Guess.
All operations return a Game Status object, with the game key, which identifies the game.

### Resources

#### Game

To create a new game:  

POST http://{server}/game  

json: {   
"user": "Lucas",   
"players": "1"  
}  

To join a existing multi player game:  

POST http://{server}/game/{gameKey}/user/{username}  

#### Guess

To post a guess:  

POST http://{server}/guess  
json: {  
  "gameKey": "8b99b93c-09c4-4715-8813-f143ed43f92e",  
  "code":  "CYOOOOOB",  
  "user": "Lucas"  
}  

#### Game Status

Example of a Game Status object:  

{  
  "colors": [  
    "R",  
    "B",  
    "G",  
    "Y",  
    "O",  
    "P",  
    "C",  
    "M"  
  ],  
  "codeLength": 8,  
  "gameKey": "591b09be-bd5d-47b8-87e2-80ef7446bf56",  
  "numGuesses": 3,  
  "message": "Nice try!",  
  "solved": false,  
  "user": "Lucas",  
  "pastResults": [  
    {  
      "exact": 0,  
      "near": 3,  
      "guess": "CYOOOOOB",  
      "user": "Lucas"  
    },  
    {  
      "exact": 1,  
      "near": 2,  
      "guess": "CYBBOOOB",  
      "user": "Lucas"  
    }  
  ],  
  "result": {  
    "exact": 1,  
    "near": 2,  
    "guess": "CYBBOOOB",  
    "user": "Lucas"  
  }  
}  






