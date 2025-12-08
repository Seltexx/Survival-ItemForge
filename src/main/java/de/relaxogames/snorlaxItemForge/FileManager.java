package de.relaxogames.snorlaxItemForge;

import java.io.File;

public class FileManager {

    public static File datafolder;

    public static void initialize(){
        datafolder = ItemForge.getForge().getDataFolder();


    }

}
