# Mancala
The game is divided in two projects

- Backend
- Frontend

## Backend
Is a `Java/Kotlin spring-boot`  application that consists of two modules. One is the `lib` that has all the game logic and the other one is the REST `api` implementation.

## How to run it locally
It will start a local server in the port `8081`
```
backend$ ./gradelw tasks bootRun
```

### Technologies
- Kotlin
- Spring Boot
- PostgresSQL
- Websockets
- Junit
- Testcontainers

## Frontend
Is a `Vue.js` applications that consumes the data from the `api` and allows a user to play or to view how to players play the game.

## How to run it locally
It will start a local server in the port `8080`
```
frontend$ npm run serve
```
### Technologies
- Vue.js
- Websockets