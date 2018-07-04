package Dictionary;

import Model.Law;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scrapper of the BOE to get all the legislation.
 * https://www.boe.es/legislacion/legislacion.php?campo%5B0%5D=ID_SRC&dato%5B0%5D=1&operador%5B0%5D=and&campo%5B1%5D=NOVIGENTE&dato%5B1%5D=N&operador%5B1%5D=and&campo%5B2%5D=&dato%5B2%5D=Ley&accion=Buscar&checkbox_solo_tit=S&operador%5B2%5D=and&page_hits=999&sort_field%5B0%5D=PESO&sort_order%5B0%5D=desc&sort_field%5B1%5D=ref&sort_order%5B1%5D=asc
 * Aviso legal del BOE: https://www.boe.es/sede_electronica/informacion/aviso_legal.php
 * @author vroddon
 */
public class BOEScrapper {
    
    
    /**
     * 
     */
        public static void main(String[] args) throws IOException {
        }

/**
 * Downloads the legislation from the BOE.
 * Gets the official xml page from the boe uri making this transformation
 * //                    https://www.boe.es/buscar/doc.php?id=BOE-A-1963-13975                    
//                    https://www.boe.es/diario_boe/xml.php?id=BOE-A-1963-13975
 */        
        public static void downloadXML()
        {
            Set<Law> laws = Law.readLawsFile("./resources/other Codes used/boe_out.txt");
            for(Law law : laws)
            {
                if (law==null)
                    continue;
                if (law.title.startsWith("Ley"))
                {
                    try{
                    String urlxml = law.boe.replace("https://www.boe.es/buscar/doc.php?id=", "https://www.boe.es/diario_boe/xml.php?id=");
                    String xml = LynxUtils.getHtmlContent(urlxml);
                    String id = urlxml.replace("https://www.boe.es/diario_boe/xml.php?id=","");
                    try{
                        FileUtils.writeStringToFile(new File("./resources/spain/"+id+".xml"), xml, "UTF-8");
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    }catch(Exception e2)
                    {
                        System.err.println("Error con " + law.boe);
                        e2.printStackTrace();
                    }
                    
                   // break;
                }
            }
                
        }
        
        public static void listDocuments()
        {
            int num=999;
            Set<Law> leyes = new HashSet();
            String query1 ="https://www.boe.es/legislacion/legislacion.php?campo%5B0%5D=ID_SRC&dato%5B0%5D=1&operador%5B0%5D=and&campo%5B1%5D=NOVIGENTE&dato%5B1%5D=N&operador%5B1%5D=and&campo%5B2%5D=&dato%5B2%5D=Ley&accion=Buscar&checkbox_solo_tit=S&operador%5B2%5D=and&page_hits="+num+"&sort_field%5B0%5D=PESO&sort_order%5B0%5D=desc&sort_field%5B1%5D=ref&sort_order%5B1%5D=asc";
            String query = "https://www.boe.es/legislacion/legislacion.php?accion=Mas&id_busqueda=_Y2F1Sk9MMlhNSGRNZ3p2TmJheXRjMmdKREtNZzFoTWJZak5ocjJZQ09tVEY2c0g4WEVJSU9mdm9aTkNVd1AxbEx2VExpd2dhejNxeGFZczVSSjJBRWp3MWJseVovaHpBd2tqT0x2eTNUWVVHcStxdE9FZ2RadmNSZFFJZ3FvTFpuZlRZVUFZWDBCRzdUYzhpSG5EdnNKVWd5UjZNMGd6L29uTU00SlN5YnA0RkJoMG45VTgxY3FEQVJRNld2ZkladTRvdTIxaTRLTTVkaXhwakFXZlVXaGhnK3NBZUtwVE16YlQ3WFdUS2NSSGpxVXFCWGg4SmZUSEFoSkVHeDJPaQ,,-7992-999";
            String html = LynxUtils.getHtmlContent(query);
            Document doc = Jsoup.parse(html);
            doc.setBaseUri("https://www.boe.es/legislacion/");

            Elements elements = doc.getElementsByClass("listadoResult");
            Elements hleyes = elements.first().getElementsByClass("resultado-busqueda");
            int tot = hleyes.size();
            for(Element hley : hleyes)
            {
                Law ley = new Law();
                ley.title=hley.getElementsByClass("documento").text();
                ley.legislator=hley.getElementsByClass("epigrafeDpto").text();
                Elements divs = hley.getElementsByClass("enlacesMas");
                
                Elements links = divs.first().select("a[href]");
                if (links.size()>0)
                    ley.boe = links.get(0).attr("abs:href");
                if (links.size()>2)
                    ley.boe_consolidated = links.get(2).attr("abs:href");
//                 Element test=links.get(0);
//                String attr = test.attr("href");
//                String attr2 = test.attr("abs:href");
                leyes.add(ley);
            }
            try{
            FileWriter fw2 = new FileWriter("resources\\other Codes used\\boe_out9.csv");
            FileWriter fw = new FileWriter("resources\\other Codes used\\boe_out9.txt");
            for(Law ley : leyes)
            {
                String x = ley.toString();
                fw.write(ley.toString()+"\n");
                fw2.write(ley.toCSV()+"\n");
                fw.flush();
                fw2.flush();
            }
            fw.close();
            fw2.close();
            }catch(Exception e)
            {
                
            }
        }
}
