package Utils;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;







public class FileHelper {
	
	
	
	public static String GetResourceAsString(String filename) throws IOException {
		return IOUtils.toString(FileHelper.class.getClassLoader().getResourceAsStream(filename));
	}
	
	
	public static void WriteToFile(String content, String filename) throws IOException {
		try (PrintWriter out = new PrintWriter(filename)) {
		    out.println(content);
		}
	}
	
	public static File GetRootFolder() {
        try {
            File root;
            String runningJarPath = FileHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
            
            //System.out.println("runningJarPath: " + runningJarPath);
            int lastIndexOf = runningJarPath.lastIndexOf("/classes/");
            if (lastIndexOf < 0) {           
                root = new File("");
            } else {
                root = new File(runningJarPath.substring(0, lastIndexOf));
            }

            //System.out.println("application resolved root folder: " + root.getAbsolutePath());
            return root;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
