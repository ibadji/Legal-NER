package NER;


import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.parser.ParseException;
import edu.stanford.nlp.ling.tokensregex.parser.TokenSequenceParseException;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.util.Triple;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author n
 */
public class CoreNLP {
    private static ArrayList<String> al = new ArrayList<String>();
    private static String CorePOSModel;
    private static String CoreNERModel;
    private static ReadFile read = new ReadFile();

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, TokenSequenceParseException, ClassNotFoundException {
        String sentence = read.readFile("resources/inputText/test.txt");
        regX(sentence,"spanish","rule");
        
        //XMLinline tagginwith no regx
        //doTagging(getModel("resources/spanish.ancora.distsim.s512.crf.ser.gz"), "resources/test.txt");
        
        //training
        // trainAndWrite("D:\\Test\\stanford-ner-2018-02-27\\ner-model.ser.gz","D:\\Test\\stanford-ner-2018-02-27\\austen.prop","C:/Users/n/Desktop/docs to annotate/output1.txt");     

    }
 
    public static void regX(String text, String language, String type) throws IOException, ParseException, TokenSequenceParseException 
    {
        String rulesFile = "resources/RegXRules/regx-CoreNLP-"+type+"-"+language+".rules";
        Properties props = StringUtils.argsToProperties(new String[]{"-props", "resources/Libraries and Properties/CoreNLP/"+language+".properties"});
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner,regexner,entitymentions");
        //props.setProperty("regexner.caseInsensitive", "true");
        props.put("regexner.mapping", rulesFile);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        //regxPrint(text, ".", pipeline);
        regxPrint(text, "", pipeline);
        
        // add elements to al, including duplicates
        HashSet<String> hs = new HashSet<String>();
        hs.addAll(al);
        al.clear();
        al.addAll(hs);
        PrintWriter writer = new PrintWriter("output/CoreNLP.txt", "UTF-8");
        for (String object: al) {
            //System.out.println(object);
            writer.println(object);

        }
        writer.close();
    }
    public static void regxPrint(String text, String s,StanfordCoreNLP pipeline)
    {
        String text2=text.replaceAll(Pattern.quote(s), "");
        Annotation annotation = new Annotation(text2);
        pipeline.annotate(annotation);
        List<CoreMap> multiWordsExp = annotation.get(MentionsAnnotation.class);
        for (CoreMap multiWord : multiWordsExp) {
            String custNERClass = multiWord.get(NamedEntityTagAnnotation.class);
            al.add(multiWord +" : " +custNERClass);
        }
    }
    public static void namedEntityRecognition(String Sentence, String modelPOS) {

        String text = Sentence;
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit,pos,lemma, ner, entitymentions");
        props.setProperty("pos.model",CorePOSModel );
        props.setProperty("ner.model", CoreNERModel);

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);      
        List<CoreMap> multiWordsExp = document.get(CoreAnnotations.MentionsAnnotation.class);
        for (CoreMap multiWord : multiWordsExp) {
            String custNERClass = multiWord.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            System.out.println(multiWord +" : " +custNERClass);
        }
    }
    public static void doTagging(CRFClassifier model, String input) throws IOException 
    {
       model.classifyAndWriteAnswers(input);
       String st="";
       String[] text = read.readFile(input).split("\n");//.toLowerCase();    
//       File file = new File(input);     
//       BufferedReader br = new BufferedReader(new FileReader(file));
//       while((st=br.readLine())!=null){
//          System.out.println( model.classifyWithInlineXML(st));
//      }
       
       int j = 0;
      for (String str : text) {
        j++;
        List<Triple<String,Integer,Integer>> triples = model.classifyToCharacterOffsets(str);
        for (Triple<String,Integer,Integer> trip : triples) {
          System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n",
                  trip.first(), trip.second(), trip.third, j);
        }
}
    }  
        public static void trainAndWrite(String modelOutPath, String prop, String trainingFilepath) 
    {
        Properties props = StringUtils.propFileToProperties(prop);
        props.setProperty("serializeTo", modelOutPath);

        //if input use that, else use from properties file.
        if (trainingFilepath != null) {
            props.setProperty("trainFile", trainingFilepath);
        }
        SeqClassifierFlags flags = new SeqClassifierFlags(props);
        CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
        crf.train();

        crf.serializeClassifier(modelOutPath);
    }
    
    public static CRFClassifier getModel(String modelPath) 
    {
        return CRFClassifier.getClassifierNoExceptions(modelPath);
    }

}
