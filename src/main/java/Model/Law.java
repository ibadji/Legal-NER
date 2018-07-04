package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;

/**
 * Act in Spain.
 *
 * @author vroddon
 */
public class Law {

    //Official title of the law
    public String title = "";

    //URI of the consolidated document
    public String boe_consolidated = "";

    //epigrafeDpto
    public String legislator = "";

    public String uri = "";

    //Wikipedia page
    public String wiki = "";

    //DBpedia page
    public String dbpedia = "";

    public String boe = "";
    public String resumen = "";

    //Other names and abbreviations
    public Set<String> nicknames = new HashSet();

    
    
    public static Law jsonToLaw(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Law ley = mapper.readValue(json, Law.class);
            return ley;
        } catch (Exception e) {
            return null;
        }
    }

    public String toCSV()
    {
        String s ="";
        s+= title +"\t" + this.boe +"\t" + this.legislator +"\t" + boe_consolidated ;
        return s;
    }
    
    public String toString() {
        String s = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            s = mapper.writeValueAsString(this);
        } catch (Exception e) {

        }
        return s;
    }
}
