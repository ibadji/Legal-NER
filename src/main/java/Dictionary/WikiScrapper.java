package Dictionary;

import Model.Law;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class uses DBpedia and Wikipedia to get laws and nicknames.
 *
 * @author vroddon
 */
public class WikiScrapper {

    final static String endpoint = "http://es.dbpedia.org/sparql";

    public static void main(String[] args) throws IOException {

//        List<Law> leyes = getLeyes();
        List<Law> leyes = readLeyes();
        System.out.println("total de leyes: " + leyes.size());
        for (Law law : leyes) {
            System.out.println(law.title + "\t"+ law.wiki+ "\t" + law.boe);
        }
    }

    public static List<Law> readLeyes() {
        List<Law> lista = new ArrayList();

        try {
            String archivo = "resources\\other Codes used\\laws.txt";
            File file = new File(archivo);
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (String line; (line = br.readLine()) != null;) {
                String[] split = line.split("\t");
                Law law = new Law();
                law.dbpedia = split[0];
                law.title = split[1];
                law.wiki = split[2];
                if (split.length != 4 && !law.title.contains("España")) {
                    continue;
                }

                String html = LynxUtils.getHtmlContent(law.wiki);

                Document doc = Jsoup.parse(html);
                
                Elements h1 = doc.select("h1");
                Elements ps = doc.getElementsByTag("p");
                Element p = ps.first();
                Elements pxs = doc.select("h2 > p");
                law.resumen=p.text();
                Elements links = doc.select("a[href]"); // a with href
                int tot = links.size();
                int conta=0;
                String sboe="";
                for(int i=0;i<tot;i++)
                {
                    Element link = links.get(i);
                    String slink=link.attr("abs:href");
                    if (slink.contains("http://www.boe.es"))
                    {   
                        conta++;
                        sboe=slink;
                    }
                }
                if (conta==1)
                    law.boe = sboe;
                lista.add(law);
                //Hey Ines, at this point you have in law.resumen the first <p> element in the wikipedia article, where frequently nicknames appear. 
                //maybe you want to parse it somehow to get nicknames.
                
                System.out.println(law.title + "\t"+ law.wiki+ "\t" + law.boe + "\t" + law.resumen);

            }
        } catch (Exception e) {

        }
        return lista;

    }

    public static List<Law> getLeyes() {
        List<Law> lista = new ArrayList();

        String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/>\n"
                + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX dbp: <http://dbpedia.org/resource/>\n"
                + "PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"
                + "SELECT DISTINCT (?uri), ?label, ?wiki, ?x\n"
                + "WHERE {\n"
                + "  ?uri rdfs:label ?label .\n"
                + "  ?uri <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> ?wiki .\n"
                + "optional {\n"
                + "  ?uri ?x <http://es.dbpedia.org/resource/Categoría:Leyes_de_España>\n"
                + "}\n"
                + "  FILTER regex( ?uri, 'http://es.dbpedia.org/resource/Ley_' )\n"
                + "} LIMIT 3000";

        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution qs = results.next();
                String uri = qs.get("?uri").asResource().getURI();
                String label = qs.get("?label").asResource().getURI();
                String wiki = qs.get("?wiki").asResource().getURI();
                Law law = new Law();
                law.uri = uri;
                law.title = label;
                law.wiki = wiki;
                lista.add(law);
            }
        } catch (Exception e) {
        } finally {
            qexec.close();
        }
        if (lista.isEmpty()) {
            return new ArrayList();
        }
        return lista;
    }

    
}
