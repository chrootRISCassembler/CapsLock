/*
    This file is part of CapsLock. CapsLock is a simple game launcher.
    Copyright (C) 2018 RISCassembler.

    CapsLock is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

package game_info;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class GameInfoBuilder {
    private UUID uuid;
    private Path exe;
    private String name;
    private Instant lastMod;
    private String desc;
    private Path panel;
    private List<Path> imageList;
    private List<Path> movieList;
    private int gameID;

    UUID getUUID() {
        return uuid;
    }

    Path getExe() {
        return exe;
    }

    String getName() {
        return name;
    }

    Instant getLastMod() {
        return lastMod;
    }

    String getDesc() {
        return desc;
    }

    Path getPanel() {
        return panel;
    }

    List<Path> getImageList() {
        return imageList;
    }

    List<Path> getMovieList() {
        return movieList;
    }

    int getGameID() {
        return gameID;
    }

    public final GameInfoBuilder setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public final GameInfoBuilder setExe(Path exe) {
        this.exe = exe;
        return this;
    }

    public final GameInfoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public final GameInfoBuilder setLastMod(Instant lastMod) {
        this.lastMod = lastMod;
        return this;
    }

    public final GameInfoBuilder setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public final GameInfoBuilder setPanel(Path panel) {
        this.panel = panel;
        return this;
    }

    public final GameInfoBuilder setImageList(List<Path> imageList) {
        this.imageList = imageList;
        return this;
    }

    public final GameInfoBuilder setMovieList(List<Path> movieList) {
        this.movieList = movieList;
        return this;
    }

    public final GameInfoBuilder setGameID(int gameID) {
        this.gameID = gameID;
        return this;
    }

    public final GameEntry buildGameEntry() {
        return new GameEntry(this);
    }

    public final GameRecord buildGameRecord() {
        return new GameRecord(this);
    }
}
