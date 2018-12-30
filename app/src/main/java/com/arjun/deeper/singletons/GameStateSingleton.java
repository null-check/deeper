package com.arjun.deeper.singletons;

public class GameStateSingleton {

    private static volatile GameStateSingleton singleton = new GameStateSingleton();

    public enum GameState {
        MENU,
        TUTORIAL,
        INTRO,
        RUNNING,
        OVER,
        PAUSED
    }

    private GameState gameState;

    private GameStateSingleton() {
        gameState = GameState.MENU;
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