/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NER;

import com.wantedtech.common.xpresso.x;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


/**
 *
 * @author ines badji
 */


public class Nicknames {
    private static ReadFile read = new ReadFile();
    private static Directory indexDir;
    private StandardAnalyzer analyzer;
    private IndexWriterConfig config;
    private String[] list={};
    private static String[] length;
    private static PrintWriter writer ;
    private static final int DEFAULT_PHRASE_SLOP = 10; 
    private IndexWriter indexWriter;


    public static void main(String[] args) throws IOException, InvalidTokenOffsetsException, ParseException 
    {
        String Text = read.readFile("resources/inputText/tweets.txt");
        run(Text);        
    
    }
    
    public void init() throws IOException
    {
        analyzer = new StandardAnalyzer();
        config = new IndexWriterConfig( analyzer);
        config.setOpenMode(OpenMode.CREATE_OR_APPEND);
        indexDir = new RAMDirectory();
        indexWriter = new IndexWriter(indexDir, config);
    }
    
    public static void run(String Text) throws IOException, InvalidTokenOffsetsException, ParseException
    {
        Nicknames nickname = new Nicknames();
        nickname.init();
        nickname.writerEntries(Text);
        //need to check trough the whole excel file
        String[] runthrough = read.readFile("resources/RegXRules/nicknames.txt").split("\n");
        writer = new PrintWriter("output/Nicknames.txt", "UTF-8");
        for(String line:runthrough)
        {
            String term = line.trim();
            length = term.split(" ");
            nickname.findSilimar(term);
        }
        indexDir.close();
        writer.close();
    }

        public void findSilimar(String searchForSimilar) throws IOException, InvalidTokenOffsetsException, ParseException 
    {
        FindApache(searchForSimilar);
        FindXpresso(searchForSimilar,list);
    }

    public void writerEntries(String Text) throws IOException
    {

        indexWriter.commit();
        //txt output need to be (word in text: matched word : entity)
        //lower case,trim
        String modify = Text;//.toLowerCase();
        //remove accents
//        modify = Normalizer.normalize(modify, Normalizer.Form.NFD);
//        modify =  modify.replaceAll("[^\\p{ASCII}]", "");    
        list = modify.split("\n");

        int n = 1;
        for (String line:list)
        {
            Document doc1 = createDocument(n+"","Tweet"+n, line);
            indexWriter.addDocument(doc1);
            n++;
        }                  

        indexWriter.commit();
        indexWriter.forceMerge(100, true);
        indexWriter.close();
    }
    
    private Document createDocument(String id, String title, String content) 
    {     
        Document doc = new Document();
        doc.add(new TextField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));  
        doc.add(new TextField("content", content, Field.Store.YES));
        return doc;
    }	
    
    public void FindXpresso(String searchForSimilar,String[] list)
    {
        for (String line:list)
        {
                        String s = line;
            String lookeup= searchForSimilar;
            int position = x.String(s).search(lookeup);
            if (position > -1 && position+lookeup.length()< line.length())
            {
                CalculateSimilarity sim = new CalculateSimilarity();
                String found = s.substring(position, position+lookeup.length());
                if(sim.calculate(searchForSimilar,found)>=0.90)
                    //x.print("1:::"+searchForSimilar+":::"+found);
                    writer.println("1:::Tweetx:::"+searchForSimilar+":::"+found);
            }
                
        } 
    
    }
    public void FindApache(String searchForSimilar) throws IOException, InvalidTokenOffsetsException
    {
        IndexReader reader = DirectoryReader.open(indexDir);
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        MoreLikeThis mlt = new MoreLikeThis(reader);
        mlt.setMinTermFreq(0);
        mlt.setMinDocFreq(0);
        mlt.setFieldNames(new String[]{"title", "content"});
        mlt.setAnalyzer(analyzer);
        
        Reader sReader = new StringReader(searchForSimilar);
        
        Query query = mlt.like("content", sReader);
        TopDocs topDocs = indexSearcher.search(query,10);

        Formatter formatter = new SimpleHTMLFormatter();
        QueryScorer scorer = new QueryScorer(query);
        scorer.setExpandMultiTermQuery(true);
        
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 10);
        highlighter.setTextFragmenter(fragmenter);

        //Iterate over found results
        for ( ScoreDoc scoreDoc : topDocs.scoreDocs )
        {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String title = doc.get("title");
            String Content = doc.get("content");
            
            //Printing - to which document result belongs
            if(scoreDoc.score >= 0.8)
            {
                writer.print(scoreDoc.score +":::");
                writer.print( title+":::");
                writer.print(searchForSimilar+":::");
                //System.out.print("content: "+ Content);

                TokenStream stream = TokenSources.getAnyTokenStream(reader, scoreDoc.doc, "content", analyzer);
                TextFragment[] frags = highlighter.getBestTextFragments(stream, Content, true, 10);
                for (TextFragment frag : frags)
                {         
                   // System.out.print(frag);
                    String highlithed = frag.toString();
                    highlithed =  highlithed.replaceAll("<B>", "");  
                    highlithed =  highlithed.replaceAll("</B>", ""); 
                    writer.print(highlithed);
                }
                System.out.println();
                writer.print("\n");
            }
            else if (scoreDoc.score < 0.80 & scoreDoc.score > 0.1)
            {
                //make it go trough second similarity and check again then print in text
                
                CalculateSimilarity sim = new CalculateSimilarity();
                TokenStream stream = TokenSources.getAnyTokenStream(reader, scoreDoc.doc, "content", analyzer);
                TextFragment[] frags = highlighter.getBestTextFragments(stream, Content, true, length.length);
                for (TextFragment frag : frags)
                {               
                    String highlithed = frag.toString();
                    highlithed =  highlithed.replaceAll("<B>", "");  
                    highlithed =  highlithed.replaceAll("</B>", ""); 
                    if(sim.calculate(searchForSimilar,highlithed)>=0.75)
                    {
                        writer.print(scoreDoc.score +":::");
                        writer.print( title+":::");
                        writer.print(searchForSimilar+":::");
                        writer.print(highlithed);
                        writer.print("\n");                       
                    }
                    //System.out.println(sim.calculate(searchForSimilar,s)+"      "+searchForSimilar+"       "+s);
                }   
            }
        }
         reader.close(); 
    
    }
}
