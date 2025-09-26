package de.hannisoft.graveplan.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileExporter {

    public void exportResource(File destPath, String resourceName) throws Exception {
        exportResource(destPath, resourceName, null);
    }

    public void exportResource(File destPath, String resourceName, InputStream addContent) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(new File(destPath, resourceName));
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }

            if (addContent != null) {
                while ((readBytes = addContent.read(buffer)) > 0) {
                    resStreamOut.write(buffer, 0, readBytes);
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }

}
