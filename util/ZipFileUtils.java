package util;

import javax.swing.*;
import java.io.*;
import java.util.zip.*;

public class ZipFileUtils {
    private File zipFile;
    public ZipFileUtils(File file) throws IOException {
        zipFile = file;
    }
    public void zipFiles(File[] files) throws IOException {
        ZipOutputStream zop = new ZipOutputStream(new FileOutputStream(zipFile));
        for(File file : files) {
            FileInputStream inputStream = new FileInputStream(file);
            zop.putNextEntry(new ZipEntry(file.getName()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (reader.ready()) {
                zop.write(reader.read());
            }
            zop.closeEntry();
            reader.close();
        }
        zop.close();
    }
}
