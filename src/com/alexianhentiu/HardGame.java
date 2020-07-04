package com.alexianhentiu;

public class HardGame {

    private static Player player;
    private static Player bot;
    private static Player winner;

    public HardGame() {
        //prepares for hard game similarly to regular game
        GUI.prepareNewGrid();
        winner = null;
        Main.setWinnerIndexes(new int[3]);
        player = new HumanPlayer(GUI.getCurrentLanguage().equals("English") ? "You" : "Tu", GUI.getFrame());
        bot = new BotPlayer();
        int index = bot.getRandomEmptyPosition();
        GUI.getGridSpace(index).setText(GUI.getNextMove(index));
    }

    public static void next(int index) {
        //player move
        if (winner != null || Main.isDraw()) {
            return;
        }
        if (GUI.getGridSpace(index).getText().equals("")) {
            GUI.getGridSpace(index).setText(GUI.getNextMove(index));
            checkEndOfGame();
            nextBotMove();
        }
    }

    private static void nextBotMove() {
        //bot makes the first move
        if (winner != null || Main.isDraw()) {
            return;
        }
        int index;
        if ((index = Main.trySmartPosition("X", "0", bot)) == -1) {
            index = bot.getRandomEmptyPosition();
        }
        GUI.getGridSpace(index).setText(GUI.getNextMove(index));
        checkEndOfGame();

    }

    private static void checkEndOfGame() {
        if ((winner = Main.findWinner(bot, player)) != null) {
            GUI.showPopup("win", winner, winner.equals(bot) ? "X" : "0");
        } else if (Main.isDraw()) {
            GUI.showPopup("draw", winner, "");
        }
    }

}
