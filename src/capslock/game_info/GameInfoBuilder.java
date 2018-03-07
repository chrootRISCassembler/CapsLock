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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

public final class GameInfoBuilder implements IGame {
    private UUID uuid;
    private Path exe;
    private String name;
    private Instant lastMod;
    private String desc;
    private Path panel;
    private List<Path> imageList = new ArrayList<>();
    private List<Path> movieList = new ArrayList<>();
    private Integer gameID;

    private Map<String, Boolean> validityFlagMap = new HashMap<>(9);

    {
        validityFlagMap.put("uuid", false);
        validityFlagMap.put("exe", false);
        validityFlagMap.put("name", false);
        validityFlagMap.put("lastMod", false);
        validityFlagMap.put("desc", false);
        validityFlagMap.put("panel", false);
        validityFlagMap.put("imageList", false);
        validityFlagMap.put("movieList", false);
        validityFlagMap.put("gameID", false);
    }

    public GameInfoBuilder() {
        uuid = UUID.randomUUID();
        exe = null;
        name = null;
        lastMod = null;
        desc = null;
        panel = null;
        gameID = null;
    }

    public GameInfoBuilder(JSONObject record) {
        try {
            uuid = UUID.fromString(record.getString("UUID"));
            validityFlagMap.replace("uuid", true);
        }catch (JSONException ex){
            if(record.has("UUID")) {
                System.err.println("There is \"UUID\" key, but wrong value. Fix the JSON file.");
            }else {
                System.err.println("There is no \"UUID\" field. This field is necessary. Fix the JSON file.");
            }
            ex.printStackTrace();
        }catch (IllegalArgumentException ex){
            System.err.println("There is \"UUID\" key, but wrong value. This is not a UUID.");
            ex.printStackTrace();
        }

        ExeField: try {
            final String UncheckedVal = record.getString("exe");
            if(UncheckedVal.isEmpty()){
                System.err.println("\"exe\" field is empty String. Fix the JSON file.");
                break ExeField;
            }

            exe = Paths.get(UncheckedVal);

            if (Files.isExecutable(exe)) {
                validityFlagMap.replace("exe", true);
                break ExeField;
            }

            if(Files.isRegularFile(exe)){
                System.out.println(exe + " is a regular file. But cannot read.");
                System.out.println("Check the permission or the owner of the file.");
            }

            if(Files.isDirectory(exe)){
                System.out.println(exe + " is a directory.");
                System.out.println("You specified the wrong path.");
            }

            if(!Files.exists(exe)){
                System.out.println("There is no such file : " + exe);
                System.out.println("You specified the wrong path.");
            }

        }catch (JSONException ex){
            if(record.has("exe")) {
                System.err.println("There is \"exe\" key, but wrong value. Fix the JSON file.");
            }else {
                System.err.println("There is no \"exe\" key. \"exe\" field is necessary. Fix the JSON file.");
            }
            ex.printStackTrace();
        }catch (InvalidPathException ex){
            System.err.println("There is \"exe\" key, but wrong value. Failed to convert String into Path.");
            ex.printStackTrace();
        }

        try {
            name = record.getString("name");
        }catch (JSONException ex){
            if(record.has("name")) {
                System.err.println("There is \"name\" key, but wrong value. Fix the JSON file.");
                ex.printStackTrace();
            }
        }

        try {
            lastMod = Instant.parse(record.getString("lastMod"));
        }catch (JSONException ex){
            if(record.has("lastMod")) {
                System.err.println("There is \"lastMod\" key, but wrong value. Fix the JSON file.");
                ex.printStackTrace();
            }
        }

        try {
            desc = record.getString("desc");
        }catch (JSONException ex){
            if(record.has("desc")) {
                System.err.println("There is \"desc\" key, but wrong value. Fix the JSON file.");
                ex.printStackTrace();
            }
        }

        try {
            panel = Paths.get(record.getString("panel"));
        }catch (JSONException ex){
            if(record.has("panel")) {
                System.err.println("There is \"panel\" key, but wrong value. Fix the JSON file.");
                ex.printStackTrace();
            }
        }

        try {
            for (Object unchecked : record.getJSONArray("imageList")){
                System.out.println(unchecked.getClass());
                System.out.println(unchecked);

                final Path path = Paths.get((String) unchecked);

                if(Files.isReadable(path)){
                    imageList.add(path);
                }else{
                    System.err.println("Cannot read " + path);

                    if(!Files.isRegularFile(path)){
                        System.out.println(path + " is not a regular file.");
                    }

                    if(Files.isDirectory(path)){
                        System.out.println(path + " is a directory.");
                        System.out.println("You specified the wrong path.");
                    }

                    if(!Files.exists(path)){
                        System.out.println("There is no such file : " + path);
                        System.out.println("You specified the wrong path.");
                    }
                }
            }
        }catch (JSONException ex){
            if(record.has("imageList")) {
                System.err.println("There is \"imageList\" key, but wrong value. Fix the JSON file.");
                ex.printStackTrace();
            }
        }



        try {
            for (Object unchecked : record.getJSONArray("movieList")){
                System.out.println(unchecked.getClass());
                System.out.println(unchecked);

                final Path path = Paths.get((String) unchecked);

                if(Files.isReadable(path)){
                    movieList.add(path);
                }else{
                    System.err.println("Cannot read " + path);

                    if(!Files.isRegularFile(path)){
                        System.out.println(path + " is not a regular file.");
                    }

                    if(Files.isDirectory(path)){
                        System.out.println(path + " is a directory.");
                        System.out.println("You specified the wrong path.");
                    }

                    if(!Files.exists(path)){
                        System.out.println("There is no such file : " + path);
                        System.out.println("You specified the wrong path.");
                    }
                }
            }
        }catch (JSONException ex){
            if(record.has("movieList")) {
                System.err.println("There is \"movieList\" key, but wrong value. Fix the JSON file.");
                ex.printStackTrace();
            }
        }



        try {
            gameID = record.getInt("gameID");
            if(Integer.signum(gameID) != 1){
                System.err.println("\"gameID\" field must be natural number and unique. But the value is " + gameID + '.');
            }else {
                validityFlagMap.replace("gameID", true);
            }
        }catch (JSONException ex){
            if(record.has("gameID")) {
                System.err.println("There is \"gameID\" key, but wrong value. Fix the JSON file.");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public final UUID getUUID() { return uuid; }

    @Override
    public final Path getExe() { return exe; }

    @Override
    public final Optional<String> getName() { return Optional.ofNullable(name); }

    @Override
    public final Optional<Instant> getLastMod() { return Optional.ofNullable(lastMod); }

    @Override
    public final Optional<String> getDesc() { return Optional.ofNullable(desc); }

    @Override
    public final Optional<Path> getPanel() { return Optional.ofNullable(panel); }

    @Override
    public final List<Path> getImageList() { return imageList; }

    @Override
    public final List<Path> getMovieList() { return movieList; }

    @Override
    public final Optional<Integer> getGameID() { return Optional.ofNullable(gameID); }

    public final GameInfoBuilder setUUID(UUID uuid) {
        if(uuid == null)throw new IllegalArgumentException("DO NOT pass null as a argument.");
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

    public final boolean isFilled(){
        return  !validityFlagMap.containsValue(false);
    }

    public final boolean canBuild(){
        return validityFlagMap.get("uuid") & validityFlagMap.get("exe");
    }

    public final GameInfoBuilder setGameID(Integer gameID) {
        this.gameID = gameID;
        return this;
    }

    public final GameEntry buildGameEntry() {
        return new GameEntry(this);
    }

    public final GameRecord buildGameRecord() {
        if(exe == null)throw new IllegalStateException("exe is null. Call setExe(Path exe) method before you call this method.");
        return new GameRecord(this);
    }
}