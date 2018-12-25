package com.arjun.deeper.singletons;

public class GameStateSingleton {

    private static volatile GameStateSingleton singleton = new GameStateSingleton();

    public enum GameState {
        STOPPED,
        INTRO,
        RUNNING,
        PAUSED
    }

    private GameState gameState;

    private GameStateSingleton() {
        gameState = GameState.STOPPED;
    }

    public static GameStateSingleton getInstance() {
        return singleton;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}