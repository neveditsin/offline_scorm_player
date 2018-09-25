package scanner;

import java.nio.file.Path;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class TinCan extends Project {

	protected TinCan(Path manifestPath, Element root) {
		super(manifestPath, root);
		t = new Tree<Module>(new Module(manifestPath.getParent().toString(), null, true));
		
		NodeList activities = root.getElementsByTagName("activity");
		for (int i = 0; i < activities.getLength(); i++) {
			Element o = (Element) activities.item(i);
			NodeList names = o.getElementsByTagName("name");


			String name = names.item(0).getTextContent();

			
			NodeList launches = o.getElementsByTagName("launch");

			String launch = launches.item(0).getTextContent();


			t.addChild(new Module(name, manifestPath.getParent().toUri() + launch + "?endpoint=http%3A%2F%2Flocalhost%3A8080%2Flrs%2F&auth=Basic%20dGVzdDp0ZXN0&actor=%7B\"mbox\"%3A\"test%40test.com\"%2C\"name\"%3A\"test%20test\"%7D"));
			

		}
	}

	@Override
	public String getType() {
		return "TinCan";
	}

}
