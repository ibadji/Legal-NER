
import NER.IxaPipe;
import NER.CoreNLP;
import NER.Combine;
import NER.GateNER;
import NER.OpenNLP;
import NER.ReadFile;
import edu.stanford.nlp.ling.tokensregex.parser.ParseException;
import edu.stanford.nlp.ling.tokensregex.parser.TokenSequenceParseException;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author n
 */
public class main {
    private static String ENGLISH = "english";  
    private static String SPANISH = "spanish";
    private static Lock lock = new ReentrantLock(true);
    private static ReadFile read = new ReadFile();

    public static void main(String[] args) throws IOException, ParseException, TokenSequenceParseException, InterruptedException, Exception 
    {
        //text to be annotated
        String text = "resources/inputText/test.txt";
        String sentence = read.readFile(text);
        String Language = SPANISH;
        String Type = "rule";// can be rule, nickname or other for spanish or rule and other for english
        
        OpenNLP open = new OpenNLP();
        open. runIt(Language,"",sentence,Type);

        CoreNLP core = new CoreNLP();
        core.regX(sentence,Language,Type);

 
        IxaPipe exec= new IxaPipe();
        exec.initProperties(Language);        
        exec.findEntities(sentence);
           
        if (Language == "english")
        {
            GateNER gate = new GateNER();
            gate.init(sentence);
        }
  
        Combine combine = new Combine();
        combine.filter(text);
            
       //Assess Outputs
    }
}
