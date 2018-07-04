package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

    String id="";
    
    //Other names and abbreviations
    public Set<String> nicknames = new HashSet();

    public static Set<Law> readLawsFile(String filename) {
        Set<Law> laws = new HashSet();
        try {
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String line="";

            while ((line = br.readLine()) != null) {
                try {
//                    System.out.println(line);
                    Law law = jsonToLaw(line);
                    laws.add(law);
                } catch (Exception e) {

                }
            }
            br.close();
        } catch (Exception e) {

        }

        return laws;
    }

    public static Law jsonToLaw(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Law ley = mapper.readValue(json, Law.class);
            return ley;
        } catch (Exception e) {
            return null;
        }
    }

    public String toCSV() {
        String s = "";
        s += title + "\t" + this.boe + "\t" + this.legislator + "\t" + boe_consolidated;
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
