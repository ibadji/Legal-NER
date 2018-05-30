package NER;


import UI.Display;
import edu.stanford.nlp.ling.tokensregex.parser.ParseException;
import edu.stanford.nlp.ling.tokensregex.parser.TokenSequenceParseException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author n
 */
public class Combine {
    private static HashMap<String, Integer> grades = new  HashMap<String, Integer>();   
    private static HashMap<String, Integer> priority = new  HashMap<String, Integer>();
    private static HashMap<String, String> color = new  HashMap<String, String>();


    private static Map<String, List<String>> outputList = new  ConcurrentHashMap<String, List<String>>();
    private static Map<String, List<String>> todelete = new  ConcurrentHashMap<String, List<String>>();

    public static void main(String[] args) throws IOException, ParseException, TokenSequenceParseException 
    {
            filter("resources/inputText/test-english.txt");
    }
    /*
        Give priorities from 1 to 3, 1 being the highest 
        1: regex
        2: Organization, Person, Location
        3:misc or other
    find similarities within the results, the unique ones are added directly the similar ones are checked in their 
    tags the one with highest priority wins. If same priority and same tag the longest one is taken.
        */
        
    public static void init()
    {
        grades.put("OpenNLP", 0);
        grades.put("CoreNLP", 0);
        grades.put("IxaPipe", 0);
        grades.put("Gate", 0);

 
        priority.put("Law_Reference",1);
        priority.put("Abreviation",1);
        priority.put("Governemental_Institution",1);
        priority.put("Location_Spain",1);
        priority.put("Language",1);
        priority.put("Detection_words",1);

        priority.put("PER",3);   
        priority.put("PERS",3);          
        priority.put("PERSON",3);
        priority.put("person",3);
        priority.put("Person",3);

        priority.put("ORG",3);
        priority.put("organization",3);
        priority.put("Organization",3);

        priority.put("LUG",3);
        priority.put("location",3);
        priority.put("Location",3);

        priority.put("LOC",3); 
        priority.put("TITLE",3);       

        priority.put("misc",4);
        priority.put("MISC",4);
        priority.put("other",4);
        priority.put("OTH",4);
        priority.put("OTROS",4);
        
        color.put("Law_Reference","#00FF00");
        color.put("Abreviation","#0000FF");
        color.put("Governemental_Institution","#FF0000");
        color.put("Location_Spain","#01FFFE");
        color.put("Language","#FFA6FE");
        color.put("Detection_words","#FFDB66");
        
        color.put("PER","#BDC6FF");
        color.put("PERS","#BDC6FF");
        color.put("PERSON","#BDC6FF");
        color.put("Person","#BDC6FF");
        color.put("person","#BDC6FF");


        color.put("ORG","#BDD393");
        color.put("organization","#BDD393");
        color.put("Organization","#BDD393");

        color.put("LUG","#9E008E");
        color.put("location","#9E008E");
        color.put("Location","#9E008E");

        color.put("LOC","#9E008E");
        color.put("TITLE","#FF74A3");
        color.put("misc","#01D0FF");
        color.put("MISC","#01D0FF");
        color.put("other","#E56FFE");
        color.put("OTH","#E56FFE");
        color.put("OTROS","#E56FFE");
    }
    public static void filter(String text) throws IOException
    {
        //initialize the priority maps
        init();
        //put the input into a map keeping trackof the source software
        transform("IxaPipe");         
        transform("CoreNLP");
        transform("OpenNLP");
        transform("GateNLP");

        
        SimilarityFilter();

        //filter based on similarity given in initialization step
        Set set = outputList.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         System.out.print("key: "+ mentry.getKey() + "      Value: "+ mentry.getValue());
         System.out.println();
      } 

        //show in text
        showInText(outputList, text);    
    }
    
    public static void SimilarityFilter()
    {
        //remove numbers alone
        Set set = outputList.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
     
           if(isNumber(mentry.getKey().toString()))
           {
                iterator.remove();
           }
      }
                     
        //remove duplicates(similar) based on priority, uniques directly add them others base of lenght and priority
        Set<String> keys = outputList.keySet();
        String[] array = keys.toArray(new String[keys.size()]);
        CalculateSimilarity sim = new CalculateSimilarity();
         
         for(int i = 0; i<array.length;i++)
         {
            for(int j = 0; j<array.length;j++)
            {
                List<String> p1 = outputList.get(array[i]);
                List<String> p2 = outputList.get(array[j]);
                //found a similar dup

                if(array[i].contains(array[j]) && priority.get(p1.get(1).toString().trim()) < priority.get(p2.get(1).toString().trim()))
                {  
                    todelete.put(array[j], p2);                     
                }
                
                else if(sim.calculate(array[i], array[j])> 0.95 && sim.calculate(array[i], array[j])<= 1.0)
                {
                    //System.out.println(array[i]+"  "+ array[j] +"  "+sim.calculate(array[i], array[j]));    
                    //check for priorities 
                    
                    
                    if(priority.get(p1.get(1).toString().trim()) < priority.get(p2.get(1).toString().trim()))
                    {
                        todelete.put(array[j], p2);                     
                    }
                    else if(priority.get(p1.get(1).toString().trim()) > priority.get(p2.get(1).toString().trim()))
                    {
                        todelete.put(array[i], p1);                     
                    }
                    //same priority see depending on length
                    else if(priority.get(p1.get(1).toString().trim()) == priority.get(p2.get(1).toString().trim()))
                    {
                        if(array[i].length() < array[j].length())
                        {
                            todelete.put(array[i], p1); 
                        }
                        else if(array[i].length() > array[j].length())
                        {
                            todelete.put(array[j], p2);  
                        }
                        else if (array[i].length() == array[j].length())
                        {
                            //base on grade need to be changed                          
                        }
                    }
                }
            }
         }  
         
         //Delete from outputList all keys to be deleted 
        outputList.keySet().removeAll(todelete.keySet());     
    }
    public static boolean isNumber(String str) {
        
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }
    public static void showInText(Map<String, List<String>> outputList, String text) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }

            //Map of the Entities detected
            PrintWriter writer = new PrintWriter("output/ListDetectedEntities.txt", "UTF-8");
            Collection<List<String>> values = outputList.values();
            String[] s = values.toString().split("]");
            Set<String> sett = new HashSet<String>();
            for(int i = 0; i < s.length; i++){
              sett.add(s[i]);
            }
            Iterator it = sett.iterator();
            while(it.hasNext()) {
              writer.println(it.next());
            }
            writer.close();
            
            
            JFrame frame = new JFrame("Text");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            Display t = new Display(text);

            frame.add(t);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            JFrame frame1 = new JFrame("Entities and Software");
            frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame1.setLayout(new BorderLayout());
            Display t1 = new Display("output/ListDetectedEntities.txt");

            frame1.add(t1);
            frame1.pack();
            frame1.setLocationRelativeTo(null);
            frame1.setVisible(true);
       
        //compare the entities and color them
        PrintWriter writer2 = new PrintWriter("output/TextEntitiesDetected.txt", "UTF-8");
        Set set = outputList.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();    
         String key = mentry.getKey().toString();
         ArrayList<String> valueK = (ArrayList<String>) mentry.getValue();
         String value = valueK.get(1);
         t.color(Color.decode(color.get(value.trim())), key.trim());
         t1.color(Color.decode(color.get(value.trim())), value.trim());
         Link link = new Link();
         //if(value == "LAW")
            writer2.println( mentry.getKey() + "      "+ mentry.getValue()+"         "+ link.findLink(mentry.getKey().toString()));       
      }
        writer2.close();
        
    }
    
    public static void grading(HashMap CoreNLP, HashMap OpenNLP, HashMap IxaPipe,HashMap unrated)
    {
        Integer total = outputList.size();
    
    }
    
    public static void  transform(String output) throws FileNotFoundException, IOException
    {
        BufferedReader br = null;
        File fr = new File("output/"+output+".txt");
        br =   new BufferedReader(new InputStreamReader(new FileInputStream(fr)));
        String Line;
        while ((Line = br.readLine()) != null) {
            List<String> values = new ArrayList<String>();
            if(output=="CoreNLP")
            {
                String[] result = Line.split(":");
                values.add("CoreNLP");
                values.add(result[1]);
                outputList.put(result[0], values);
            }
            else if(output=="OpenNLP"){
                String[] result = Line.split(":");
                values.add("OpenNLP");
                values.add(result[1].split("\\)")[1]);
                outputList.put(result[0], values);
            }
            else if(output=="IxaPipe"){
                String[] result = Line.split("\t");
                values.add("IxaPipe");
                values.add(result[1]);     
                outputList.put(result[0], values);
            }
            else if(output=="GateNLP"){
                String[] result = Line.split(":");
                values.add("GateNLP");
                values.add(result[1]);     
                outputList.put(result[0], values);
            }
        }
        
//        Set set = outputList.entrySet();
//        Iterator iterator = set.iterator();
//        while(iterator.hasNext()) {
//         Map.Entry mentry = (Map.Entry)iterator.next();
//         System.out.print("key: "+ mentry.getKey() + "      Value: "+ mentry.getValue());
//         System.out.println();
//      }
    }
}