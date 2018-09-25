package scanner;

public class Module {
	public final String name;
	public final String link;
	public final String datafromlms;
	public final boolean isRoot;
	public final String type;

	public Module(String name, String link) {
		this.name = name;
		this.link = link;
		this.datafromlms = "";
		this.isRoot = false;
		this.type = "";
	}
	
	public Module(String name, String link, boolean isRoot) {
		this.name = name;
		this.link = link;
		this.datafromlms = "";
		this.isRoot = isRoot;
		this.type = "";
	}
	
	public Module(String name, String link, String datafromlms) {
		this.name = name;
		this.link = link;
		this.isRoot = false;
		this.datafromlms = datafromlms;
		this.type = "";
	}

	public Module(String name, String link, String datafromlms, String type) {
		this.name = name;
		this.link = link;
		this.isRoot = false;
		this.datafromlms = datafromlms;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Module [name=" + name + ", link=" + link + "]";
	}
	


}