
import NER.IxaPipe;
import NER.CoreNLP;
import NER.Combine;
import NER.GateNER;
import NER.Nicknames;
import NER.OpenNLP;
import NER.ReadFile;
import edu.stanford.nlp.ling.tokensregex.parser.ParseException;
import edu.stanford.nlp.ling.tokensregex.parser.TokenSequenceParseException;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Entry point to run the software.
 * @ines badji
 */
public class main {
    private static String ENGLISH = "english";  
    private static String SPANISH = "spanish";
    private static Lock lock = new ReentrantLock(true);
    private static ReadFile read = new ReadFile();

    /**
     * Modify at will the Language, the text (input file) and the type (ES: "rule", "nickname", "other", EN: "rule", "other")
     */
    public static void main(String[] args) throws IOException, ParseException, TokenSequenceParseException, InterruptedException, Exception 
    {
        //text to be annotated
        String text = "resources\\inputText\\Annotation\\eu\\EU court case\\10.txt";
        String sentence = read.readFile(text);
        String Language = ENGLISH;
        String Type = "rule";// can be rule, nickname or other for spanish or rule and other for english
        
        if(Type.equals("nickname"))
        {
            Nicknames nickname = new Nicknames();
            nickname.run(sentence);
        }
        OpenNLP open = new OpenNLP();
        open.runIt(Language,"",sentence,Type);

        CoreNLP core = new CoreNLP();
        core.regX(sentence,Language,Type);

 
        IxaPipe exec= new IxaPipe();
        exec.initProperties(Language);        
        exec.findEntities(sentence);
           
        if (Language.equals("english"))
        {
            GateNER gate = new GateNER();
            gate.init(sentence);
        }
  
        Combine combine = new Combine();
        combine.filter(Language,text,Type);
            
       //Assess Outputs
    }
}
