package NER;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
/**
 *
 * @author pcalleja
 */
public class OpenNLP {
    
    
    private static String TokenizerModel;
    private static String NEModel;
    private static ReadFile read = new ReadFile();
    
    public static void main (String [] args) throws IOException{       
        String sentence = read.readFile("resources/inputText/test.txt");
        runIt("spanish","",sentence);
    }
    public static void runIt(String language, String regXLaw,String sentences) throws IOException
    {
        TokenizerModel="resources/Libraries and Properties/OpenNLP/en-token.bin";
        String [] tokens= tokenize(sentences);
        PrintWriter writer = new PrintWriter("output/OpenNLP.txt", "UTF-8");
        String l = "";
        if (language.equals("english")) l="en";
        else if (language.equals("spanish")) l="es";
        
        NEModel="resources/Libraries and Properties/OpenNLP/"+l+"-ner-location.bin";
        Span [] entities= namedEntityRecognition( tokens, writer);
        NEModel="resources/Libraries and Properties/OpenNLP/"+l+"-ner-person.bin";
        entities= namedEntityRecognition( tokens, writer);
        NEModel="resources/Libraries and Properties/OpenNLP/"+l+"-ner-organization.bin";
        entities= namedEntityRecognition( tokens, writer);   
        
        Map<String, Pattern[]> regexMap = new HashMap<>();
        regexMap.put("Law_Reference", readFileRegx("resources/RegXRules/regx-OpenNLP-"+language+".txt"));

            RegexNameFinder finder = new RegexNameFinder(regexMap);
            Span[] result = finder.find(tokens);

            // nameSpans contain all the possible entities detected
            for(Span s: result){
                for(int index=s.getStart();index<s.getEnd();index++){ // text
                    if (tokens[index+1].compareTo(",")== 0)
                        writer.print(tokens[index]);
                    else
                    {
                        writer.print(tokens[index] +" " );
                    }
                }
                writer.print("  :  ");
                writer.print(s.toString()); // type
                writer.println();
            }
            writer.close();     
    
    }
    
    public static String[] tokenize(String Sentence) throws IOException{
   
        InputStream modelIn = null;
        String [] tokensOut = null;

        modelIn = new FileInputStream(TokenizerModel);
        // Model
        TokenizerModel model = new TokenizerModel(modelIn);

        // Tokenizer
        TokenizerME tokenizer = new TokenizerME(model);

        // Get Tokens
        String tokens[] = tokenizer.tokenize(Sentence);

        tokensOut = tokens;    
        return tokensOut;
    }
    
    
    public static Span[] namedEntityRecognition(String [] tokens, PrintWriter writer)throws IOException {
        
        // load the model from file
        InputStream is = new FileInputStream(NEModel);
 
        //  NE model
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
 
        // feed the model to name finder class
        NameFinderME nameFinder = new NameFinderME(model);
 
        // Get entities
        Span[] nameSpans = nameFinder.find(tokens);

        // nameSpans contain all the possible entities detected
        for(Span s: nameSpans){         
            for(int index=s.getStart();index<s.getEnd();index++){ // text
                writer.print(tokens[index]+" ");
            }
            writer.print("  :  ");
            writer.print(s.toString()); // type
            writer.println();
        }
        return nameSpans;
    }
    
    public static Pattern[] readFileRegx(String FilePath) throws IOException{
    
        BufferedReader br = null;
        File fr = new File(FilePath);
        ArrayList<Pattern> patterns = new ArrayList<Pattern>();

  
        br =   new BufferedReader(new InputStreamReader(new FileInputStream(fr), "UTF8"));

        String Line;

        while ((Line = br.readLine()) != null) {
           
            patterns.add(Pattern.compile(Line,Pattern.CASE_INSENSITIVE));
        }

        br.close();
        return patterns.toArray(new Pattern[patterns.size()]);
    }   
}
