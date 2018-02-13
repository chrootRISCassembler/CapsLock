package file_checker;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FileChecker {
    private String uncheckedPath;
    private Path path = null;
    private Consumer<String> onConvertFailed;
    private Predicate<Path> onInvalidPath;
    private Predicate<Path> onNotExists;
    //private Predicate<Path> onDirectory;
    private Predicate<Path> onCannotRead;
    private Predicate<Path> onCannotWrite;

    {
        onConvertFailed = str -> {
            System.err.println("Failed to convert " + str + " to Path type.");
        };

        onInvalidPath = path -> {
            System.err.println(path + " is invalid path.");
            return false;
        };

        onNotExists = path -> {
            System.err.println("There's no such file; " + path);
            return false;
        };

//        onDirectory = path -> {
//            System.err.println(path + " is directory.");
//            return false;
//        };

        onCannotRead = path -> {
            System.err.println("Can't read " + path);
            System.err.println("Check file permission.");
            return false;
        };

        onCannotWrite = path -> {
            System.err.println("Can't write " + path);
            System.err.println("Check file permission.");
            return false;
        };

    }

    public FileChecker(String path){
        uncheckedPath = path;
    }

    public FileChecker(Path path){
        this.path = path;
    }

    public FileChecker OnConvertFailed(Consumer<String> lambda){
        onConvertFailed = lambda;
        return this;
    }

    public FileChecker OnInvalidPath(Predicate<Path> lambda){
        onInvalidPath = lambda;
        return this;
    }

    public FileChecker OnNotExists(Predicate<Path> lambda){
        onNotExists = lambda;
        return this;
    }

//    public FileChecker OnDirectory(Predicate<Path> lambda){
//        onDirectory = lambda;
//        return this;
//    }

    public FileChecker OnCannotRead(Predicate<Path> lambda){
        onCannotRead = lambda;
        return this;
    }

    public FileChecker OnCannotWrite(Predicate<Path> lambda){
        onCannotWrite = lambda;
        return this;
    }

    public Optional<Path> check(){

        if (path == null){
            try {
                path = Paths.get(uncheckedPath);
            }catch (IllegalArgumentException | FileSystemNotFoundException | SecurityException ex){
                onConvertFailed.accept(uncheckedPath);
                return Optional.empty();
            }
        }

        if (Files.isDirectory(path)) {
            //if(!onDirectory.test(path))return Optional.empty();
            System.err.println(path + " is directory.");
            return Optional.empty();
        }



        if (Files.exists(path)){
            if (!Files.isRegularFile(path)){
                System.err.println(path + " isn't regular file.");
                return Optional.empty();
            }

            if (!Files.isReadable(path)) {
                if(!onCannotRead.test(path))return Optional.empty();
            }

            if (!Files.isWritable(path)) {
                if(!onCannotWrite.test(path))return Optional.empty();
            }
        } else {
            if (!onNotExists.test(path))return Optional.empty();
        }

        return Optional.of(path);
    }
}
