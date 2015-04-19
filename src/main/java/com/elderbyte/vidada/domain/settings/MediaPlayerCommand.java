package com.elderbyte.vidada.domain.settings;

/**
 * Represents a Command template which can play a media.
 */
public class MediaPlayerCommand {

    private String command;
    private String playerName;

    public MediaPlayerCommand() { }

    public MediaPlayerCommand(String playerName, String command){
        this.playerName = playerName;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
