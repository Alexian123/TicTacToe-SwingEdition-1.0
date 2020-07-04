package com.alexianhentiu;

public class Main {

    private static Player player1;
    private static Player player2;
    private static Player winner;
    private static int[] winnerIndexes= new int[3];

    public static void main(String[] args) throws InterruptedException {
        new GUI();
    }

    public static void play(int numberOfPlayers) {
        //create new players and/or reset end of game variables
        GUI.prepareNewGrid();
        winner = null;
        winnerIndexes = new int[3];
        if (numberOfPlayers == 2) {
            String name = (GUI.getCurrentLanguage().equals("English")) ? "Player" : "Jucător";
            player1 = new HumanPlayer(name + "1", GUI.getFrame());
            player2 = new HumanPlayer(name + "2", GUI.getFrame());
        } else {
            player1 = new HumanPlayer(GUI.getCurrentLanguage().equals("English") ? "You" : "Tu", GUI.getFrame());
            player2 = new BotPlayer();
        }
        GUI.setNextTurn(player1, player2);
    }

    public static void next(int index) {
        //player move
        if (winner != null || isDraw()) {
            return;
        }
        if (GUI.getGridSpace(index).getText().equals("")) {
            GUI.getGridSpace(index).setText(GUI.getNextMove(index));
            checkEndOfGame();
            GUI.setNextTurn(player1, player2);
        }
    }

    public static void nextBotMove() {
        //easy and medium bot move
        if (winner != null || isDraw()) {
            return;
        }
        int index;
        if (GUI.getDifficulty().equals("Easy") || (index = trySmartPosition("0", "X", player2)) == -1) {
            index = player2.getRandomEmptyPosition();
        }
        GUI.getGridSpace(index).setText(GUI.getNextMove(index));
        checkEndOfGame();
    }

    public static int trySmartPosition(String self, String opponent, Player bot) {
        //make the bot try to win/stop player from winning
        int index;
        if ((index = bot.tryToTakeWin(self)) != -1) {
            return index;
        }
        return bot.tryToBlockWin(opponent);
    }

    public static boolean isDraw() {
        for (int i = 0; i < 9; i++) {
            if (GUI.getGridSpace(i).getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    public static Player findWinner(Player player1, Player player2) {
        if ((winner = checkFirstDiagonal(player1, player2)) != null) {
            return winner;
        }
        if ((winner = checkSecondDiagonal(player1, player2)) != null) {
            return winner;
        }
        if ((winner = checkHorizontally(player1, player2)) != null) {
            return winner;
        }
        return checkVertically(player1, player2);
    }

    public static int[] getWinnerIndexes() {
        return winnerIndexes;
    }

    public static void setWinnerIndexes(int[] winnerIndexes) {
        Main.winnerIndexes = winnerIndexes;
    }

    private static Player checkVertically(Player player1, Player player2) {
        for (int i = 0; i < 3; i++) {
            if (GUI.getGridSpace(i).getText().equals("X") &&
                    GUI.getGridSpace(i + 3).getText().equals("X") &&
                        GUI.getGridSpace(i + 6).getText().equals("X")) {
                winnerIndexes[0] = i;
                winnerIndexes[1] = i + 3;
                winnerIndexes[2] = i + 6;
                return player1;
            } else if (GUI.getGridSpace(i).getText().equals("0") &&
                            GUI.getGridSpace(i + 3).getText().equals("0") &&
                                GUI.getGridSpace(i + 6).getText().equals("0")) {
                winnerIndexes[0] = i;
                winnerIndexes[1] = i + 3;
                winnerIndexes[2] = i + 6;
                return player2;
            }
        }
        return null;
    }

    private static Player checkHorizontally(Player player1, Player player2) {
        for (int i = 0; i < 9; i+=3) {
            if (GUI.getGridSpace(i).getText().equals("X") &&
                    GUI.getGridSpace(i + 1).getText().equals("X") &&
                        GUI.getGridSpace(i + 2).getText().equals("X")) {
                winnerIndexes[0] = i;
                winnerIndexes[1] = i + 1;
                winnerIndexes[2] = i + 2;
                return player1;
            } else if (GUI.getGridSpace(i).getText().equals("0") &&
                            GUI.getGridSpace(i + 1).getText().equals("0") &&
                                GUI.getGridSpace(i + 2).getText().equals("0")) {
                winnerIndexes[0] = i;
                winnerIndexes[1] = i + 1;
                winnerIndexes[2] = i + 2;
                return player2;
            }
        }
        return null;
    }

    private static Player checkFirstDiagonal(Player player1, Player player2) {
        if (GUI.getGridSpace(0).getText().equals("X") &&
                GUI.getGridSpace(4).getText().equals("X") &&
                    GUI.getGridSpace(8).getText().equals("X")) {
            winnerIndexes[0] = 0;
            winnerIndexes[1] = 4;
            winnerIndexes[2] = 8;
            return player1;
        } else if (GUI.getGridSpace(0).getText().equals("0") &&
                        GUI.getGridSpace(4).getText().equals("0") &&
                            GUI.getGridSpace(8).getText().equals("0")) {
            winnerIndexes[0] = 0;
            winnerIndexes[1] = 4;
            winnerIndexes[2] = 8;
            return player2;
        }
        return null;
    }

    private static Player checkSecondDiagonal(Player player1, Player player2) {
        if (GUI.getGridSpace(2).getText().equals("X") &&
                GUI.getGridSpace(4).getText().equals("X") &&
                    GUI.getGridSpace(6).getText().equals("X")) {
            winnerIndexes[0] = 2;
            winnerIndexes[1] = 4;
            winnerIndexes[2] = 6;
            return player1;
        } else if (GUI.getGridSpace(2).getText().equals("0") &&
                        GUI.getGridSpace(4).getText().equals("0") &&
                            GUI.getGridSpace(6).getText().equals("0")) {
            winnerIndexes[0] = 2;
            winnerIndexes[1] = 4;
            winnerIndexes[2] = 6;
            return player2;
        }
        return null;
    }

    private static void checkEndOfGame() {
        if ((winner = findWinner(player1, player2)) != null) {
            GUI.showPopup("win", winner, winner.equals(player1) ? "X" : "0");
        } else if (isDraw()) {
            GUI.showPopup("draw", winner, "");
        }
    }
}
