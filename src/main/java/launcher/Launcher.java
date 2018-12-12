package launcher;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.Collection;

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
import scanner.Project;
import scanner.Scanner;
import scanner.Tree;
import scanner.Module;


public class Launcher {
	
	private StringBuilder sb = new StringBuilder();
	
	//breadth first
	public void travTree(Tree<Module> t) throws UnsupportedEncodingException {
		sb.append("<ul>");		
		if(t.data.link == null) {
			sb.append("<li>");
			sb.append(t.data.name);
			sb.append("</li>");
		} else {	
			sb.append("<li><a onclick=Funcs.openWindow('");
			sb.append(t.data.link.substring(t.data.link.lastIndexOf("/projects/")));
			sb.append("','");
			sb.append(URLEncoder.encode(t.parent.data.isRoot? t.data.name : t.parent.data.name + ": " + t.data.name, "utf-8"));
			sb.append("','");
			sb.append(t.data.type.replaceAll(" ", "%20"));
			sb.append("','");
			sb.append(t.data.datafromlms.replaceAll(" ", "%20"));
			sb.append("')>");
			sb.append(t.data.name);
			sb.append("</a></li>\n");
		}
		if (t.children != null) {
			for(Tree<Module> child : t.children) {
				travTree(child);
			}
		}
		
		sb.append("</ul>\n");
	}
	
	
	private static Tomcat initTomcat() throws IOException, ServletException {
		File root = FileHelper.GetRootFolder();
		System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
		Tomcat tomcat = new Tomcat();
		Path tempPath = Files.createTempDirectory("tomcat-base-dir");
		tomcat.setBaseDir(tempPath.toString());

		// The port that we should run on can be set into an environment variable
		// Look for that variable and default to 8080 if it isn't there.
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
		// Set execution independent of current thread context classloader
		// (compatibility with exec:java mojo)
		ctx.setParentClassLoader(Launcher.class.getClassLoader());

		System.out.println("configuring app with basedir: " + webContentFolder.getAbsolutePath());

		// Declare an alternative location for your "WEB-INF/classes" dir
		// Servlet 3.0 annotation will work
		File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "classes/WEB-INF");
		WebResourceRoot resources = new StandardRoot(ctx);

		WebResourceSet resourceSet;
		if (additionWebInfClassesFolder.exists()) {
			resourceSet = new DirResourceSet(resources, "/WEB-INF", additionWebInfClassesFolder.getAbsolutePath(), "/");
			System.out.println(
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
      	
   	
    	//get projects from /project folder   	
    	Collection<Project> ps = Scanner.scanForProjects();
    	Launcher l = new Launcher();
    	
    	
    	for(Project p : ps) {
    		Tree<Module> t = p.getModules();    		
    		l.travTree(t);
    	}
    	
    	Tomcat tomcat = initTomcat();
    	
    	ProtectionDomain pd = Launcher.class.getProtectionDomain();
    	String dir = pd.getCodeSource().getLocation().toURI().toString().replace("classes/", "")
    			.replace(new java.io.File(pd.getCodeSource().getLocation().getPath()).getName(), "");
     	String url = "http://localhost:8080/" + "player.htm";
    	
    	dir = dir.replace(System.getProperty("os.name").contains("Windows")? "file:/" : "file:", "");
  
    	
    	String htm = FileHelper.GetResourceAsString("player.htm");
    	
    	htm = htm.replace("<$$generated$$>", l.sb);
    	
    	FileHelper.WriteToFile(htm, java.net.URLDecoder.decode(dir + "player.htm", "UTF-8"));
    	


		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
        tomcat.start();
        tomcat.getServer().await();
    }
}
