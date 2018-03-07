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

package capslock.game_info;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IGame {
    UUID getUUID();
    Path getExe();
    Optional<String> getName();
    Optional<Instant> getLastMod();
    Optional<String> getDesc();
    Optional<Path> getPanel();
    List<Path> getImageList();
    List<Path> getMovieList();
    Optional<Integer> getGameID();
}
