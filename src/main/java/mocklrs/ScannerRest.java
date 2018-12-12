package mocklrs;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;




import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@Path("scanner")
public class ScannerRest {
	
	@GET
	@Path("module")
	@Produces(MediaType.APPLICATION_JSON)
	public String process() throws UnsupportedEncodingException, JSONException, SQLException {
		 JSONObject json = new JSONObject();
		 JSONArray array=new JSONArray();
		    array.put("1");
		    array.put("2");
		    json.put("friends", array);	
		    return json.toString();

	}


}