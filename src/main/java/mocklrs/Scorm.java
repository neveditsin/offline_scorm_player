package mocklrs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import db.DbUtils;;

@Path("scorm")
public class Scorm {
	
	@GET
	@Path("report")
	@Produces("text/csv") 
	public Response report() throws SQLException, IOException  {		
		File file = File.createTempFile("report", ".tmp");
		file.deleteOnExit();
		
		try(BufferedWriter out = new BufferedWriter(new FileWriter(file))){
			List<HashMap<String, Object>> db = DbUtils.getScormReport();
			for(HashMap<String, Object> entry : db) {
				if(entry.get("API_CALL").equals("Terminate")) {
					JSONObject cmi = new JSONObject(entry.get("CMI").toString());		
					out.append(StringEscapeUtils.escapeCsv(entry.get("DT").toString()));
					out.append(",");
					out.append(StringEscapeUtils.escapeCsv(entry.get("WNAME").toString()));
					out.append(",");
					out.append(StringEscapeUtils.escapeCsv(entry.get("MODULE").toString()));
					out.append(",");
					out.append(parse13(cmi));
					out.append("\r\n");
				}		
				
			}
		}	
		
        
        ResponseBuilder response = Response.ok((Object) file);  
        response.header("Content-Disposition","attachment; filename=\"report\"");  
        return response.build();  
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void process(String data) throws UnsupportedEncodingException, JSONException, SQLException {
		JSONObject sc = new JSONObject(data);
		String ver = sc.get("ver").toString();
		String type = sc.get("api_call").toString();
		String path = sc.get("module").toString();
		String scmi = sc.get("cmi").toString();
		String wname = URLDecoder.decode(sc.get("wname").toString(), "UTF-8");
		
		DbUtils.putScormData(new Date(), ver, type, path, wname, scmi);		

	}


	
	private String parse13(JSONObject cmi) {
		try {
			StringBuilder csv = new StringBuilder();
			csv.append(StringEscapeUtils.escapeCsv(cmi.get("session_time").toString()));
			csv.append(",");
			csv.append(StringEscapeUtils.escapeCsv(cmi.get("completion_status").toString()));
			csv.append(",");
			if (cmi.has("score")) {
				JSONObject score = new JSONObject(cmi.get("score").toString());
				String raw = score.get("raw").toString();
				String max = score.get("max").toString();
				csv.append(StringEscapeUtils.escapeCsv(raw));
				csv.append(",");
				csv.append(StringEscapeUtils.escapeCsv(max));
			} else {
				csv.append(",");
			}

			return csv.toString();
		} catch (Exception e) {
			return "";
		}
	}
}