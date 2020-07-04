package com.alexianhentiu;

public class BotPlayer implements Player {

    public String getName() {
        return GUI.getCurrentLanguage().equals("English") ? "The Computer" : "Calculatorul";
    }

    public int getRandomEmptyPosition() {
        int index;
        do {
            index = (int) (Math.random() * 9);
        } while (!GUI.getGridSpace(index).getText().equals(""));
        return index;
    }


    //smart move methods

    public int tryToTakeWin(String self) {
        int index;
        if ((index = tryHorizontally(self)) != -1) {
            return index;
        }
        if ((index = tryVertically(self)) != -1) {
            return index;
        }
        if ((index = tryFirstDiagonal(self)) != -1) {
            return index;
        }
        return trySecondDiagonal(self);
    }

    public int tryToBlockWin(String opponent) {
        int index;
        if ((index = tryHorizontally(opponent)) != -1) {
            return index;
        }
        if ((index = tryVertically(opponent)) != -1) {
            return index;
        }
        if ((index = tryFirstDiagonal(opponent)) != -1) {
            return index;
        }
        return trySecondDiagonal(opponent);
    }

    private int tryHorizontally(String sign) {
        for (int i = 0; i < 9;  i+= 3) {
            if (GUI.getGridSpace(i).getText().equals(sign) &&
                    GUI.getGridSpace(i + 1).getText().equals(GUI.getGridSpace(i).getText()) &&
                    GUI.getGridSpace(i + 2).getText().equals("") ) {
                return  i + 2;
            }
            if (GUI.getGridSpace(i).getText().equals(sign) &&
                    GUI.getGridSpace(i + 1).getText().equals("") &&
                    GUI.getGridSpace(i + 2).getText().equals(GUI.getGridSpace(i).getText())) {
                return i + 1;
            }
            if (GUI.getGridSpace(i).getText().equals("") &&
                    GUI.getGridSpace(i + 1).getText().equals(sign) &&
                    GUI.getGridSpace(i + 2).getText().equals(GUI.getGridSpace(i + 1).getText())) {
                return i;
            }
        }
        return -1;
    }

    private int tryVertically(String sign) {
        for (int i = 0; i < 3;  i++) {
            if (GUI.getGridSpace(i).getText().equals(sign) &&
                    GUI.getGridSpace(i + 3).getText().equals(GUI.getGridSpace(i).getText()) &&
                    GUI.getGridSpace(i + 6).getText().equals("") ) {
                return  i + 6;
            }
            if (GUI.getGridSpace(i).getText().equals(sign) &&
                    GUI.getGridSpace(i + 3).getText().equals("") &&
                    GUI.getGridSpace(i + 6).getText().equals(GUI.getGridSpace(i).getText())) {
                return i + 3;
            }
            if (GUI.getGridSpace(i).getText().equals("") &&
                    GUI.getGridSpace(i + 3).getText().equals(sign) &&
                    GUI.getGridSpace(i + 6).getText().equals(GUI.getGridSpace(i + 3).getText())) {
                return i;
            }
        }
        return -1;
    }

    private int tryFirstDiagonal(String sign) {
        if (GUI.getGridSpace(0).getText().equals(sign) &&
                GUI.getGridSpace(4).getText().equals(GUI.getGridSpace(0).getText()) &&
                GUI.getGridSpace(8).getText().equals("") ) {
            return  8;
        }
        if (GUI.getGridSpace(0).getText().equals(sign) &&
                GUI.getGridSpace(4).getText().equals("") &&
                GUI.getGridSpace(8).getText().equals(GUI.getGridSpace(0).getText())) {
            return 4;
        }
        if (GUI.getGridSpace(0).getText().equals("") &&
                GUI.getGridSpace(4).getText().equals(sign) &&
                GUI.getGridSpace(8).getText().equals(GUI.getGridSpace(4).getText())) {
            return 0;
        }
        return -1;
    }

    private int trySecondDiagonal(String sign)  {
        if (GUI.getGridSpace(2).getText().equals(sign) &&
                GUI.getGridSpace(4).getText().equals(GUI.getGridSpace(2).getText()) &&
                GUI.getGridSpace(6).getText().equals("") ) {
            return  6;
        }
        if (GUI.getGridSpace(2).getText().equals(sign) &&
                GUI.getGridSpace(4).getText().equals("") &&
                GUI.getGridSpace(6).getText().equals(GUI.getGridSpace(2).getText())) {
            return 4;
        }
        if (GUI.getGridSpace(2).getText().equals("") &&
                GUI.getGridSpace(4).getText().equals(sign) &&
                GUI.getGridSpace(6).getText().equals(GUI.getGridSpace(4).getText())) {
            return 2;
        }
        return -1;
    }
}
