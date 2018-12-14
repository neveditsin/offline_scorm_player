package mocklrs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collection;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scanner.Module;
import scanner.Project;
import scanner.Scanner;
import scanner.Tree;


@Path("scanner")
public class ScannerRest {
	static Collection<Project> ps;
	static {
		try {
			ps = Scanner.scanForProjects();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	JSONArray ptoc = new JSONArray();
	private void travTree(Tree<Module> t, int lev) throws UnsupportedEncodingException {
		JSONObject toc = new JSONObject();
		toc.put("level", lev);
		if(t.data.link != null) {	
			//link
			toc.put("link", t.data.link.substring(t.data.link.lastIndexOf("/projects/")));
			//type
			toc.put("type", t.data.type.replaceAll(" ", "%20"));
			//data from lms
			toc.put("data_from_lms", t.data.datafromlms.replaceAll(" ", "%20"));
			//name
			toc.put("name", t.data.name);
			//wname
			toc.put("wname", URLEncoder.encode(t.parent.data.isRoot? t.data.name : t.parent.data.name + ": " + t.data.name, "utf-8"));
			ptoc.put(toc);
		}		
		
		if (t.children != null) {
			for(Tree<Module> child : t.children) {
				travTree(child, lev+1);
			}
		}
	}
	
	
	@GET
	@Path("module")
	@Produces(MediaType.APPLICATION_JSON)
	public String process(@QueryParam("hash") String project_hash) throws JSONException, UnsupportedEncodingException {    	
 	    //ps.stream().forEach(p -> System.out.println(p.getHash()));
		Project pr = ps.stream().filter(p -> Integer.toString(p.getHash()).equals(project_hash)).findFirst().get();
		Tree<Module> t = pr.getModules();    		
		ptoc = new JSONArray();
		travTree(t,0);
		return ptoc.toString();
	}

	
	@GET
	@Path("projects")
	@Produces(MediaType.APPLICATION_JSON)
	public String getProjects() throws JSONException {
		JSONArray projs = new JSONArray();
    	for(Project p : ps) {
    		JSONObject project = new JSONObject();
    		project.put("name", p.getName());
    		project.put("hash", p.getHash());
    		project.put("type", p.getType());
    		projs.put(project);
    	}
    	
		return projs.toString();
	}

}