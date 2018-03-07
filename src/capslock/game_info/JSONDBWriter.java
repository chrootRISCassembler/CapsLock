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

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONDBWriter {
    private JSONArray games = new JSONArray();
    private final Path filePath;

    public JSONDBWriter(Path filePath) throws IllegalArgumentException {

        if(!Files.isWritable(filePath)){
            if(Files.isRegularFile(filePath)){
                throw new IllegalArgumentException(filePath + " is a regular file. But cannot write.");
            }

            if(Files.isDirectory(filePath)){
                throw new IllegalArgumentException(filePath + " is a directory.");
            }

            if(!Files.exists(filePath)){
                throw new IllegalArgumentException("There is no such file : " + filePath);
            }
        }
        this.filePath = filePath;
    }

    public JSONDBWriter add(GameRecord record){
        games.put(record.getJSON());
        return this;
    }

    public void flush() throws IOException{
        try(final BufferedWriter writer = Files.newBufferedWriter(filePath)){
            games.write(writer);
        } catch (IOException ex) {
            System.err.println("Failed to write on " + filePath);
            throw ex;
        }
    }
}
