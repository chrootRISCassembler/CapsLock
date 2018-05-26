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

package capslock.capslock.os_absorbing;

import methg.commonlib.trivial_logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * JInputのネイティブのバイナリをロードできるようにFatjarから取り出す.
 */
public final class NativeBinaryExtractor {
    private NativeBinaryExtractor(){
        assert false : "DO NOT create instance of NativeBinaryExtractor";
    }

    public static void extractBinaries(){
        final var osName = System.getProperty("os.name").toLowerCase();
        final List<String> needFiles = new ArrayList<>();

        if(osName.contains("windows")){

            needFiles.add("jinput-dx8.dll");
            needFiles.add("jinput-dx8_64.dll");
            needFiles.add("jinput-raw.dll");
            needFiles.add("jinput-raw_64.dll");
            needFiles.add("jinput-wintab.dll");

        }else if(osName.contains("mac os")){
            needFiles.add("libjinput-osx.jnilib");
        }else {
            needFiles.add("libjinput-linux.so");
            needFiles.add("libjinput-linux64.so");
        }

        for (final var file : needFiles){
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(file);
            if(inputStream == null){
                Logger.INST.critical("InputStream of " + file + " is null");
                continue;
            }
            try {
                Files.copy(inputStream, Paths.get("./" + file));
            }catch (FileAlreadyExistsException ex){
                Logger.INST.debug(file + " already exists ; canceling copy");
            }catch (IOException ex) {
                Logger.INST.critical("Failed to extract binaries of JInput").logException(ex);
            }
        }
    }
}
