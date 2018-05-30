/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NER;
import gate.Gate;
import gate.Document;
import gate.util.GateException;
import gate.Factory;
import gate.creole.SerialAnalyserController;

import java.util.Iterator;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author n
 */

import gate.*;
import gate.creole.ANNIEConstants;
import gate.util.persistence.PersistenceManager;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class GateNER {
    private static ReadFile read = new ReadFile();
    
    public static void main(String[] args) throws Exception {
        String sentence = read.readFile("resources/inputText/test.txt");
        init(sentence);

    }
    public static void init(String text) throws Exception
    {
        Gate.setGateHome(new File("C:\\Program Files (x86)\\GATE_Developer_8.4.1"));
        Gate.init();

        LanguageAnalyser controller = (LanguageAnalyser) PersistenceManager
                .loadObjectFromFile(new File(new File(Gate.getPluginsHome(),
                        ANNIEConstants.PLUGIN_DIR), ANNIEConstants.DEFAULT_FILE));

        Corpus corpus = Factory.newCorpus("corpus");
        Document document = Factory.newDocument(text);
        corpus.add(document); 
        controller.setCorpus(corpus); 
        controller.execute();
        PrintWriter writer = new PrintWriter("output/GateNLP.txt", "UTF-8");   
//        document.getAnnotations().get(new HashSet<>(Arrays.asList("Person", "Organization", "Location")))
//            .forEach(a -> System.err.format("%s - \"%s\" [%d to %d]\n", 
//                    a.getType(), Utils.stringFor(document, a),
//                    a.getStartNode().getOffset(), a.getEndNode().getOffset()));
        document.getAnnotations().get(new HashSet<>(Arrays.asList("Person", "Organization", "Location")))
            .forEach(a -> 
                writer.println(Utils.stringFor(document, a)+":"+ a.getType())        
            );
            
        writer.close();
        //Don't forget to release GATE resources 
        Factory.deleteResource(document); 
        Factory.deleteResource(corpus); 
        Factory.deleteResource(controller);
    
    }
}
  
