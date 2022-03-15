package it.zmario.zspleef.enums;

public enum GameState {

    WAITING, INGAME;

    private static GameState gameState;

    public static void setGameState(GameState state) {
        gameState = state;
    }

    public static boolean isState(GameState state) {
        return gameState == state;
    }

    public static GameState getState() {
        return gameState;
    }

}
