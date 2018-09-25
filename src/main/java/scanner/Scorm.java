package scanner;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


class Scorm extends Project {
	private final String version;

	protected Scorm(Path manifestPath, Element root) {
		super(manifestPath, root);
		
		t = new Tree<Module>(new Module(manifestPath.getParent().toString(), null, true));
		Map<String, String> hrefs = new HashMap<>();

		NodeList ver = root.getElementsByTagName("schemaversion");
		version = ver.item(0).getTextContent();
		
		NodeList res = root.getElementsByTagName("resource");
		for (int i = 0; i < res.getLength(); i++) {
			Element r = (Element) res.item(i);
			if (r.getAttribute("type").equals("webcontent")) {
				String hrid = r.getAttribute("identifier");
				String href = r.getAttribute("href");
				if (org.apache.commons.lang.StringUtils.isNotBlank(href))
					hrefs.put(hrid, manifestPath.getParent().toUri() + href);
			}
		}
		
		
		NodeList orgz = root.getElementsByTagName("organization");		
		
				
		for (int i = 0; i < orgz.getLength(); i++) {
			Element o = (Element) orgz.item(i);
			try {
				addModule(o, t, hrefs);
			} catch (Exception e) {
				System.err.println("Module cannot be added: " + e);
			}
			
			
		}		

	}
	
	
	private void addModule(Element item, Tree<Module> module, Map<String, String> hrefs) {
		final String item_title = filterChildren(item.getChildNodes(), "title").get(0).getTextContent();
		final String ref = item.getAttribute("identifierref");
		final String params = item.getAttribute("parameters");
		
		final List<Node> dfl = filterChildren(item.getChildNodes(), "datafromlms");
		
		final String datafromlms = dfl.size() > 0? dfl.get(0).getTextContent() : "";
				
			
		final List<Node> children = filterChildren(item.getChildNodes(), "item");		

		if (children.size() != 0) { //has children	
			final Tree<Module> cm = module.addChild(new Module(item_title, StringUtils.isNotBlank(ref)? hrefs.get(ref) + params : null, datafromlms, getType()));
			for (Node n : children) {
				addModule((Element)n, cm, hrefs);
			}
		} else {
			module.addChild(new Module(item_title, StringUtils.isNotBlank(ref)? hrefs.get(ref) + params : null, datafromlms, getType()));
		}	
	}
	
	private List<Node> filterChildren(NodeList nl, String name) {
		final List<Node> filtered = new ArrayList<Node>();
		for (int i = 0; i < nl.getLength(); i++) {
			final String nodename = nl.item(i).getLocalName() != null? nl.item(i).getLocalName() : nl.item(i).getNodeName();
			if (nodename.equals(name)) {
				filtered.add(nl.item(i));
			}
		}
		return filtered;
	}

	@Override
	public String getType() {
		return "SCORM " + version;
	}
	
	

}
