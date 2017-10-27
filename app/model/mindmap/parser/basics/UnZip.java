package model.mindmap.parser.basics;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {

  private static final int BUFFER_SIZE = 4096;

  public String unzip(Path zipFilePath) {
    File f = zipFilePath.toFile();
    String destDirectory = f.getParent() + "/" + (f.getName().replace(".", ""));
    File destDir = new File(destDirectory);
    if(!destDir.exists()) {
      destDir.mkdirs();
    }

    // this try - catch MUST be here, don't throw it
    try(ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
      ZipEntry entry = zipIn.getNextEntry();
      while(entry != null) {
        String filePath = destDirectory + File.separator + entry.getName();
        File newFile = new File(filePath);
        if(!newFile.exists()) {
          new File(newFile.getParent()).mkdirs();
        }
        if(!entry.isDirectory()) {
          extractFile(zipIn, filePath);
        } else {
          File dir = new File(filePath);
          dir.mkdirs();
        }
        zipIn.closeEntry();
        entry = zipIn.getNextEntry();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return destDirectory;
  }

  /**
   * Extracts a zip entry (file entry)
   *
   * @param zipIn
   * @param filePath
   * @throws IOException
   */
  private void extractFile(ZipInputStream zipIn, String filePath) {
    try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
      byte[] bytesIn = new byte[BUFFER_SIZE];
      int read = 0;
      while((read = zipIn.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
