package NER;

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
 * @author ines badji
 */
public class OpenNLP {
    
    
    private static String TokenizerModel;
    private static String NEModel;
    private static ReadFile read = new ReadFile();
    private static PrintWriter writer;
    
    public static void main (String [] args) throws IOException{       
        String sentence = read.readFile("resources\\inputText\\Annotation\\other\\4.txt");
        runIt("spanish","",sentence,"rule");

    }
 
    public static void runIt(String language, String regXLaw,String sentences,String Type) throws IOException
    {
        TokenizerModel="resources/Libraries and Properties/OpenNLP/en-token.bin";
        writer = new PrintWriter("output/OpenNLP.txt", "UTF-8");
        String [] tokens= tokenize(sentences);
        String Language = "";
        if (language.equals("english")) Language ="en";
        else if (language.equals("spanish")) Language ="es";
        
        NEModel="resources/Libraries and Properties/OpenNLP/"+Language+"-ner-location.bin";
        Span [] entities= namedEntityRecognition( tokens, writer);
        NEModel="resources/Libraries and Properties/OpenNLP/"+Language+"-ner-person.bin";
        entities= namedEntityRecognition( tokens, writer);
        NEModel="resources/Libraries and Properties/OpenNLP/"+Language+"-ner-organization.bin";
        entities= namedEntityRecognition( tokens, writer);   
        
        Map<String, Pattern[]> regexMap = new HashMap<>();
        //regexMap.put("Legal_reference", readFileRegx("resources/RegXRules/regx-OpenNLP-"+Type+"-"+language+".txt"));
        //spanish patterns
        if (Type == "rule" && language == "spanish"){
            SpanishRule(regexMap);
        }
        else if (Type == "rule" && language == "english"){
            EnglishRule(regexMap);
        } 
        RegexNameFinder finder = new RegexNameFinder(regexMap);
        Span[] result = finder.find(tokens);

            // nameSpans contain all the possible entities detected
            for(Span s: result){
                for(int index=s.getStart();index<s.getEnd();index++){ // text
//                    if (tokens[index+1].compareTo(",")== 0)
//                    {
//                        writer.print(tokens[index]);
//                    }
//                    else
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
        public static void EnglishRule(Map<String, Pattern[]> regexMap)
    {
        Pattern[] treatie = {
            Pattern.compile("[0-9]{5,7}[a-zA-Z]"),
            Pattern.compile("[0-9]{5,7}[a-zA-Z]/[a-zA-Z]{0,3}"),
            Pattern.compile("[0-9]{5,7}[a-zA-Z][0-9]{3,7}")
        };
        Pattern[] Agreement = {Pattern.compile("[0-9]{5,7}[a-zA-Z][0-9]{3,7}([0-9]{3,7})")};
        Pattern[] Judgment = {Pattern.compile("EU:[a-zA-Z]:[0-9]{4}:[0-9]{0,4}")};        
        Pattern[] RegDirDec = {
            Pattern.compile("[No][0-9]{4}/[0-9]{0,4}/EC"),
            Pattern.compile("[0-9]{4}/[0-9]{0,4}/EC"),   
            Pattern.compile("(EC) [No][0-9]{4}/[0-9]{0,4}"),
            Pattern.compile("(EC) [0-9]{4}/[0-9]{0,4}") , 
        };
        Pattern[] CaseLaw = {
            Pattern.compile("Case [a-zA-Z]-[0-9]{0,4}/[0-9]{0,4}"),
            Pattern.compile("Case [a-zA-Z]-[0-9]{0,4}/[0-9]{0,4} [a-zA-Z]"),
            Pattern.compile("Case [a-zA-Z] [0-9]{0,4}/[0-9]{4}-[0-9]{0,4}"),
            Pattern.compile("Case [a-zA-Z] [0-9]{0,4}/[0-9]{4}-[0-9]{0,4}"),
            Pattern.compile("Case No. [a-zA-Z]{0,4}.[0-9]{0,4}"),

        };
        Pattern[] OfficialJ = {
            Pattern.compile("OJ [a-zA-Z] [0-9]{0,4}"),
            Pattern.compile("OJ [0-9]{1,4}"),
            Pattern.compile("OJ [0-9]{4} [a-zA-Z] [0-9]{0,4}"),
            Pattern.compile("OJ [0-9]{4},  [a-zA-Z] [0-9]{0,4}"),
            Pattern.compile("[0-9]{4}/[a-zA-Z] [0-9]{0,4}/[0-9]{0,4}"),
            Pattern.compile("[a-zA-Z]:[0-9]{4}:[0-9]{0,4}:[0-9]{0,4}"),
            Pattern.compile("[a-zA-Z]{1,4} [0-9]{0,4}/[0-9]{0,4} Official Journal"),
            Pattern.compile("Official Journal of the (.*?) [a-zA-Z]{1,4} [0-9]{1,4}/[0-9]{1,4} "),
            Pattern.compile("[a-zA-Z] [0-9]{1,4}/[0-9]{1,4} (en|EN) Official Journal"),

        };
        Pattern[] Directive = {
            Pattern.compile("(Directive|DIRECTIVE) [0-9]{1,4}/[0-9]{1,4}/[a-zA-Z]{1,4}"),
            Pattern.compile("(Directive|DIRECTIVE) \\((.*?)\\) [0-9]{1,4}/[0-9]{1,4}"),
            Pattern.compile("(Directive|DIRECTIVE) [0-9]{1,4}/[0-9]{1,4}/[a-zA-Z]{1,4}")

        };
        Pattern[] Article = {
            Pattern.compile("(Articles|Article) [0-9]{0,4}, [0-9]{0,4} (or|and) [0-9]{0,4} "),     
            Pattern.compile("Article [0-9]{0,4}.[0-9]{0,4}.[0-9]{0,4}"),
            Pattern.compile("Article [a-zA-Z]{0,4}.[0-9]{0,4}"),
            Pattern.compile("Articles [0-9]{0,4} and [0-9]{0,4}-[0-9]{0,4}"),
            Pattern.compile("Articles [0-9]{0,4}\\((.*?)\\) and [0-9]{0,4}"),
            Pattern.compile("(Articles|Article) [0-9]{0,4}(.*?) (and|or) [0-9]{0,4}"),
            Pattern.compile("Article [0-9]{0,4}\\((.*?)\\) and [0-9]{0,4}\\((.*?)\\)"),
            Pattern.compile("Article [0-9]{0,4}\\((.*?)\\)"),
            Pattern.compile("Article [0-9]{0,4}"),
        };
        Pattern[] Regulation = {
            Pattern.compile("Regulation \\((.*?)\\) No [0-9]{0,5}/[0-9]{0,5}"),
            Pattern.compile("Regulation \\((.*?)\\) No [0-9]{0,5}/(\\s?)[0-9]{0,5}"),
            Pattern.compile("Regulation \\((.*?)\\) No. [0-9]{0,5}/[0-9]{0,5}"),
            Pattern.compile("Regulation \\((.*?)\\) No. [0-9]{0,5}/[0-9]{0,5} \\((.*?)\\)"),
                        Pattern.compile("Regulation No [0-9]{0,5}/[0-9]{0,5}"),
            Pattern.compile("Regulation [0-9]{054}/[0-9]{0,5}"),
        };
        Pattern[] Decision = {
            Pattern.compile("Decision [a-zA-Z]{0,4}\\((.*?)\\) [0-9]{0,5}"),
            Pattern.compile("Decision [0-9]{1,4}/[0-9]{1,4}/[a-zA-Z]{1,4}")

        };
        Pattern[] Law = {
            Pattern.compile("Law No [0-9]{0,4}/[0-9]{0,4}"),
        };
 
        regexMap.put("Treatie",treatie);
        regexMap.put("Agreement",Agreement);
        regexMap.put("Judgment",Judgment);
        regexMap.put("RegDirDec",RegDirDec);
        regexMap.put("CaseLaw",CaseLaw);
        regexMap.put("OfficialJ",OfficialJ);
        regexMap.put("Directive",Directive);
        regexMap.put("Article",Article);
        regexMap.put("Regulation",Regulation);
        regexMap.put("Decision",Decision);
        regexMap.put("Law",Law);



    }
    public static void SpanishRule(Map<String, Pattern[]> regexMap)
    {
        Pattern[] articulo = {
            Pattern.compile("(artículo|art)(.*?)[0-9]{1,5}(.)[0-9]{0,5}"),
            Pattern.compile("(artículo|art)(.*?) [0-9]{1,5}.[a-zA-Z]{1}"),
            Pattern.compile("(artículo|art)(.*?) [0-9]{1,5}.[0-9]{1,5}.[a-zA-Z]{1}"),
            Pattern.compile("(art|artículo|artículos|ART)(.*?) [0-9]{1,5} y [0-9]{1,5}(.)[0-9]{1,5}"),
            Pattern.compile("(art|artículo|artículos|ART)(.*?) [0-9]{1,5} y [0-9]{1,5}(.)[0-9]{1,5}"),       
            Pattern.compile("artículos\\s[0-9]{1,5}.[0-9]{1,5}.[0-9]{1,5}[0-9]{1,5},\\s[0-9]{1,5}\\s[a-zA-Z]\\s[0-9]{1,5}"),
            Pattern.compile("ARTS(.*?) [0-9]{1,5}, [0-9]{1,5} y [0-9]{1,5}"),
            Pattern.compile("artículos [0-9]{1,5}.[0-9]{1,5}, [0-9]{1,5}.[0-9]{1,5}, [0-9]{1,5}.[0-9]{1,5} y [0-9]{1,5}.[0-9]{1,5}"),
            Pattern.compile("(arts|ARTS)(.*?) [0-9]{1,5}.[0-9]{1,5}.[0-9]{1,5}. Y [0-9]{1,5}.[0-9]{1,5}"),            
            Pattern.compile("artículos [0-9]{1,5}.[0-9]{1,5}.[0-9]{0,5}. y [0-9]{1,5}.[0-9]{1,5}.[0-9]{0,5}."),
            Pattern.compile("artículos [0-9]{1,5}.[0-9]{1,5}.[0-9]{0,5}."),           
            Pattern.compile("(artículo|art)(.*?)[0-9]{1,5}"),
            


        };

        Pattern[] Constitution = {
            Pattern.compile("CE\\w[0-9]{4}"),
            Pattern.compile("CE-[0-9]{1,7}-[0-9]{1,5}")
        };     
        Pattern[] LeyOrganica = {
            Pattern.compile("Ley [0-9]{1,5}/[0-9]{1,5}"),
            Pattern.compile("Ley [0-9]{1,5} [0-9]{1,5}/[0-9]{1,5}"),

            Pattern.compile("(LRJSP|RDL) [0-9]{1,5}/[0-9]{4}"),
            Pattern.compile("n° [0-9]{1,5}/[0-9]{4}"),
        };
        Pattern[] directiva = {Pattern.compile("(Dir.|Directiva|dir.) [0-9]{1,5}.[0-9]{1,5}")};
        Pattern[] recurso = {
            Pattern.compile("(recurso|Recurso) (.*?) [0-9]{1,5}.[0-9]{1,5}")

        };
        Pattern[] reglamento = {
            Pattern.compile("(R|Reglamento) \\((.*?)\\) [0-9]{1,5}/[0-9]{4}"),
            Pattern.compile("(R|Reglamento) \\((.*?)\\) [0-9]{1,5}/[0-9]{4}"),
            Pattern.compile("Reglamentos \\((.*?)\\) nº [0-9]{1,5}/[0-9]{4} y nº [0-9]{1,5}/[0-9]{4}"),
        };
        Pattern[] decreto = {
            Pattern.compile("(Dec|Decreto|dec) no [0-9]{4}/[0-9]{1,5}/UE"),
            Pattern.compile("(Dec|Decreto|dec) [0-9]{4}/[0-9]{1,5}/UE"),
            Pattern.compile("(Dec|Decreto|dec|RD) [0-9]{1,5}/[0-9]{1,5}")

        };
        Pattern[] sentencia = {
            Pattern.compile("(STEDH|STJUE|RUAM,|RUAM) de [0-9]{2}"),
            Pattern.compile("(STS|STC|Tribunal) [0-9]{1,5}/[0-9]{4} de [0-9]{2}"),
            Pattern.compile("(sentencia|Sentencias|Sentencia) [0-9]{1,5}/[0-9]{1,5}"),
            Pattern.compile("(sentencia|Sentencias|Sentencia) de [0-9]{1,5}/[0-9]{1,5}"),
            Pattern.compile("(sentencia|Sentencias|Sentencia) de [0-9]{2}"),
            Pattern.compile("(sentencia|Sentencias|Sentencia) de (\\w+) [0-9]{1,5}/[0-9]{1,5}"),
            Pattern.compile("(sentencia|Sentencias|Sentencia) (.*?) [0-9]{1,5}/[0-9]{1,5}") 
        };
        Pattern[] orden = {
            Pattern.compile("(O.|O|Orden) PRA/[0-9]{1,5}/[0-9]{4}"),
             Pattern.compile("(ORDEN|Orden) [0-9]{1,5}/[0-9]{4}"),

            
        };
       Pattern[] dictamen = {
            Pattern.compile("(Dict|Dictamen) CES")
        };
       Pattern[] ordinario = {
            Pattern.compile("(Apelación|ordinario) (.*?) [0-9]{1,5}/[0-9]{0,5}")
        };
       Pattern[] Resolution = {
            Pattern.compile("Resolución (.*?) de [0-9]{1,5} (.*?) [0-9]{0,5}")
        };
       
        regexMap.put("Articulo",articulo);
        regexMap.put("Constitution",Constitution);
        regexMap.put("LeyOrganica",LeyOrganica);
        regexMap.put("Directiva",directiva);
        regexMap.put("Recurso",recurso);
        regexMap.put("Reglamento",reglamento);
        regexMap.put("Decreto",decreto);
        regexMap.put("Sentencia",sentencia);
        regexMap.put("Orden",orden);
        regexMap.put("Dictamen",dictamen);
        regexMap.put("Ordinario",ordinario);
        regexMap.put("Resolution",Resolution);

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
