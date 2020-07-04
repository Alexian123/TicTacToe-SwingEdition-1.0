package com.alexianhentiu;

import javax.swing.*;

public class HumanPlayer implements Player {

    private String name;

    public HumanPlayer(String player, JFrame frame) {
        try {
            if (!GUI.isCustomName()) {
                throw new NullPointerException();
            }
            if (player.equals("You") || player.equals("Tu")) {
                this.name = JOptionPane.showInputDialog(frame,
                        (player.equals("You") ? "Enter your name" : "Introdu-ți numele").trim(),
                        (GUI.getCurrentLanguage().equals("English") ? "Input" : "Introducere date"),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                this.name = JOptionPane.showInputDialog(frame,
                        (GUI.getCurrentLanguage().equals("English") ? "Enter " + player + "'s name" :
                                "Introdu numele lui " + player).trim(),
                        (GUI.getCurrentLanguage().equals("English") ? "Input" : "Introducere date"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
            if (this.name.equals("")) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            this.name = player;
        }
   }

   public String getName() {
        return this.name;
    }


    //needed for Player interface implementation

    @Override
    public int tryToTakeWin(String self) {
        // has no use
        return -1;
    }

    @Override
    public int tryToBlockWin(String opponent) {
        // has no use
        return -1;
    }

    @Override
    public int getRandomEmptyPosition() {
        // has no use
        return -1;
    }
}
