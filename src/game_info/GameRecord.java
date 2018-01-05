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

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public final class GameRecord implements IGame{
    private final UUID uuid;
    private final Path exe;
    private final Instant lastMod;
    private final Path panel;
    private final List<Path> imageList;
    private final List<Path> movieList;
    private final int gameID;

    private final ReadOnlyStringProperty uuidProperty;
    private final ReadOnlyStringProperty exeProperty;
    private final ReadOnlyStringProperty nameProperty;
    private final ReadOnlyStringProperty lastModProperty;
    private final ReadOnlyStringProperty descProperty;
    private final ReadOnlyStringProperty panelProperty;
    private final ReadOnlyStringProperty imageCountProperty;
    private final ReadOnlyStringProperty movieCountProperty;
    private final ReadOnlyStringProperty gameIDProperty;

    GameRecord(GameInfoBuilder builder){
        uuid = builder.getUUID();
        exe = builder.getExe();
        lastMod = builder.getLastMod();
        panel = builder.getPanel();
        imageList = builder.getImageList();
        movieList = builder.getImageList();
        gameID = builder.getGameID();

        uuidProperty = new SimpleStringProperty(uuid.toString());
        exeProperty = new SimpleStringProperty(exe.getFileName().toString());
        nameProperty = new SimpleStringProperty(builder.getName());
        lastModProperty = new SimpleStringProperty(ZonedDateTime.ofInstant(lastMod, ZoneId.systemDefault()).toString());
        descProperty = new SimpleStringProperty(builder.getDesc());
        panelProperty = new SimpleStringProperty(panel == null ? "" : "exist");
        imageCountProperty = new SimpleStringProperty(Integer.toString(imageList.size()));
        movieCountProperty = new SimpleStringProperty(Integer.toString(movieList.size()));
        gameIDProperty = new SimpleStringProperty(Integer.toString(gameID));
    }

    public final ReadOnlyStringProperty uuidProperty(){return uuidProperty;}
    public final ReadOnlyStringProperty exeProperty(){return exeProperty;}
    public final ReadOnlyStringProperty nameProperty(){return nameProperty;}
    public final ReadOnlyStringProperty descProperty(){return descProperty;}
    public final ReadOnlyStringProperty lastModProperty(){return lastModProperty;}
    public final ReadOnlyStringProperty panelProperty(){return panelProperty;}
    public final ReadOnlyStringProperty imageCountProperty(){return imageCountProperty;}
    public final ReadOnlyStringProperty movieCountProperty(){return movieCountProperty;}
    public final ReadOnlyStringProperty gameIDProperty(){return gameIDProperty;}

    @Override
    public final UUID getUUID(){return  uuid;}

    @Override
    public final Path getExe(){return  exe;}

    @Override
    public final String getName(){return nameProperty.get();}

    @Override
    public final Instant getLastMod(){return  lastMod;}

    @Override
    public final String getDesc(){return descProperty.get();}

    @Override
    public final Path getPanel(){return panel;}

    @Override
    public final List<Path> getImageList(){return imageList;}

    @Override
    public final List<Path> getMovieList(){return movieList;}

    @Override
    public final int getGameID(){return gameID;}
}
