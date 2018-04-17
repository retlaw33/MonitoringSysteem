import Service.NodeService;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Start {
    public static void main (String[] arg) throws IOException, ParseException {
        System.out.println("Start");
        NodeService service = new NodeService();
    }
}
