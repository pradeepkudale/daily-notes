package org.pradale.dailynotes.util;

import org.pradale.dailynotes.model.NotesEntryType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FileUtils {

    public static String getNewFileName(NotesEntryType type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddDSSS");
        return String.format("%s-%s%s.%s","Notes", sdf.format(new Date()), new Random().nextInt(9), type.getExtension());
    }
}