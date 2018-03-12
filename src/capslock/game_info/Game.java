/*
    Copyright (C) 2018 RISCassembler

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package capslock.game_info;

import org.json.JSONObject;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class Game {
    protected UUID uuid;
    protected Path exe;
    protected String name;
    protected Instant lastMod;
    protected String desc;
    protected Path panel;
    protected List<Path> imageList = Collections.emptyList();
    protected List<Path> movieList = Collections.emptyList();
    protected int gameID = 0;

    public UUID getUUID() {
        return uuid;
    }

    public Path getExe() {
        return exe;
    }

    public String getName() {
        return name;
    }

    public Instant getLastMod() {
        return lastMod;
    }

    public String getDesc() {
        return desc;
    }

    public Path getPanel() {
        return panel;
    }

    public List<Path> getImageList() {
        return imageList;
    }

    public List<Path> getMovieList() {
        return movieList;
    }

    public int getGameID() {
        return gameID;
    }


    JSONObject toJSON() {
        final JSONObject json = new JSONObject()
                .put("UUID", uuid)
                .put("exe", exe);

        if (name != null && !name.isEmpty()) json.put("name", name);

        if (lastMod != null) json.put("lastMod", lastMod.toString());

        if (desc != null && !desc.isEmpty()) json.put("desc", desc);

        if (panel != null) json.put("panel", panel);

        if (imageList.size() > 0) json.put("imageList", imageList);

        if (movieList.size() > 0) json.put("movieList", movieList);

        if (gameID > 0) json.put("gameID", gameID);

        return json;
    }
}
