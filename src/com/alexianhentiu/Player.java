package com.alexianhentiu;

public interface Player {
    String getName();
    int tryToTakeWin(String self);
    int tryToBlockWin(String opponent);
    int getRandomEmptyPosition();
}
