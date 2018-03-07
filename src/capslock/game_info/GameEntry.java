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

import javafx.scene.image.Image;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class GameEntry implements IGame{
    private final UUID uuid;
    private final Path exe;
    private final String name;
    private final Instant lastMod;
    private final String desc;
    private final Path panel;
    private final List<Path> imageList;
    private final List<Path> movieList;
    private final Integer gameID;

    GameEntry(GameInfoBuilder builder) {
        uuid = builder.getUUID();
        exe = builder.getExe();
        name = builder.getName().orElse(null);
        lastMod = builder.getLastMod().orElse(null);
        desc = builder.getDesc().orElse(null);
        panel = builder.getPanel().orElse(null);
        imageList = builder.getImageList();
        movieList = builder.getMovieList();
        gameID = builder.getGameID().orElse(null);
    }

    @Override
    public final UUID getUUID(){return  uuid;}

    @Override
    public final Path getExe(){return  exe;}

    @Override
    public final Optional<String> getName(){return Optional.ofNullable(name);}

    @Override
    public final Optional<Instant> getLastMod(){return  Optional.ofNullable(lastMod);}

    @Override
    public final Optional<String> getDesc(){return Optional.ofNullable(desc);}

    @Override
    public final Optional<Path> getPanel(){return Optional.ofNullable(panel);}

    @Override
    public final List<Path> getImageList(){return imageList;}

    @Override
    public final List<Path> getMovieList(){return movieList;}

    @Override
    public final Optional<Integer> getGameID(){return Optional.ofNullable(gameID);}

    public final Optional<Image> mapPanelImage(){
        if(panel == null)return Optional.empty();

        try {
            return Optional.of(new Image(panel.toUri().toString()));
        } catch (Exception ex) {
            System.err.println("Unexpected Exception!");
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
