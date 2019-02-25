package launcher;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import Utils.FileHelper;



public class Launcher {
	private static boolean verboseMode = false;
	
	private static Tomcat initTomcat() throws IOException, ServletException {
		File root = FileHelper.GetRootFolder();
		
		System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
		if (!verboseMode) java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.WARNING);
		
		Tomcat tomcat = new Tomcat();
		Path tempPath = Files.createTempDirectory("tomcat-base-dir");
		tomcat.setBaseDir(tempPath.toString());

		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}
		
		
		tomcat.setPort(Integer.valueOf(webPort));

		// File webContentFolder = new File(root.getAbsolutePath(), "src/main/webapp/");
		File webContentFolder = new File(root.getAbsolutePath());
		if (!webContentFolder.exists()) {
			webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
		}

		StandardContext ctx = (StandardContext) tomcat.addWebapp("", webContentFolder.getAbsolutePath());
		ctx.setParentClassLoader(Launcher.class.getClassLoader());

		
		if (verboseMode) System.out.println("Configuring app with basedir: " + webContentFolder.getAbsolutePath());

		// Declare an alternative location for your "WEB-INF/classes" dir
		File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "classes/WEB-INF");
		WebResourceRoot resources = new StandardRoot(ctx);

		WebResourceSet resourceSet;
		if (additionWebInfClassesFolder.exists()) {
			resourceSet = new DirResourceSet(resources, "/WEB-INF", additionWebInfClassesFolder.getAbsolutePath(), "/");
			if (verboseMode) System.out.println(
					"loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
		} else {
			resourceSet = new EmptyResourceSet(resources);
		}
		resources.addPreResources(resourceSet);
		resources.addPreResources(new DirResourceSet(resources, "/WEB-INF", root.getAbsolutePath(), "/"));
		ctx.setResources(resources);
		return tomcat;
	}
	

	
    public static void main(String[] args) throws URISyntaxException, IOException, ServletException, LifecycleException { 
    	
    	for (String arg : args) {
    		if (arg.contains("-V") || arg.contains("-v")) {
    			verboseMode = true;
    		}
    	}
      	
    	Tomcat tomcat = initTomcat();
    	
     	String url = "http://localhost:8080/" + "player.html";

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Please open your browser and go to " + url);
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Please open your browser and go to " + url);
			}
		}
		
		System.out.println("Do not close this window until you finish your work");
		
        tomcat.start();
        tomcat.getServer().await();
    }
}
