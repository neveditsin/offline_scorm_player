package scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import launcher.Launcher;

public class Scanner {

	private static final String PROJECTS_DIR = "/projects";
	
	private static Collection<Path> getManifests(String basePath) throws IOException{
		return Files.walk(Paths.get(basePath + PROJECTS_DIR)).filter(Files::isRegularFile)
				.filter(f -> f.getFileName()
						.toString()
						.matches("^(tincan|imsmanifest).xml$"))
				.collect(Collectors.toList());
	}
	
	public static Collection<Project> scanForProjects() throws IOException, URISyntaxException{
		System.out.println(System.getProperty("os.name"));
		ProtectionDomain d = Launcher.class.getProtectionDomain();
		Collection<Path> manifests = getManifests(java.net.URLDecoder.decode(d.getCodeSource().
    			getLocation().toURI().
    			toString().
    			replace("classes/", "").
    			replace(System.getProperty("os.name").contains("Windows")? "file:/" : "file:" , "").
    			replace(new java.io.File(d.getCodeSource().getLocation().getPath()).getName(), ""), "UTF-8"));
		
		return manifests.stream().map(path -> {
			try {
				return Project.getProject(path);
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
				return null;
			}
		}).collect(Collectors.toList());
	}
	
}
