package dev.felnull.imp.util;

import dev.felnull.imp.IamMusicPlayer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class IMPPaths {
    public static Path getNaiveLibraryFolder(String lavaVersion) {
        return Paths.get("music_files").resolve(lavaVersion);
    }

    public static Path getTmpFolder() {
        return Paths.get("temp_files");
    }
}
