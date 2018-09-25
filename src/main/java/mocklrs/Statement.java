package mocklrs;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


@Path("statements")
public class Statement {
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void process(String data) {    	
        System.out.println(data.toString());
    }
}