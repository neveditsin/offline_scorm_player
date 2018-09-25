package scanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;


import org.w3c.dom.*;
import org.xml.sax.SAXException;



import javax.xml.parsers.*;

public abstract class Project {
	protected Path manifestPath;
	protected Element root;
	protected Tree<Module> t;
	protected Project(Path manifestPath, Element root){
		this.manifestPath = manifestPath;
		this.root = root;
	};
	
	static Project getProject(Path manifestPath) throws FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		
		DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new FileInputStream(manifestPath.toString()));
		
		if(manifestPath.getFileName().toString().equals("imsmanifest.xml")) {
			return new Scorm(manifestPath, doc.getDocumentElement());
		}
		else {
			return new TinCan(manifestPath, doc.getDocumentElement());
		}			
		
	}
	
	public abstract String getType();
	public Tree<Module> getModules(){
		return t;
	}
	public Path getPath(){
		return manifestPath;
	}



}
