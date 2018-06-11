/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NER;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.text.Normalizer;
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

public class Nicknames {
    private static ReadFile read = new ReadFile();
    private Directory indexDir;
    private StandardAnalyzer analyzer;
    private IndexWriterConfig config;
    private String[] list={};
    private static String[] length;
    
        public static void main(String[] args) throws IOException, InvalidTokenOffsetsException {
        String Text = read.readFile("resources/inputText/tweets.txt");
        run(Text);
    
    }
    
    public static void run(String Text) throws IOException, InvalidTokenOffsetsException
    {
        Nicknames m = new Nicknames();
        m.init();
        m.writerEntries(Text);
        //need to check trough the whole excel file
        String term = "ley mordaza";
        length = term.split(" ");
        m.findSilimar(term);
    }
    public void init() throws IOException{
        analyzer = new StandardAnalyzer();
        config = new IndexWriterConfig( analyzer);
        config.setOpenMode(OpenMode.CREATE_OR_APPEND);
        indexDir = new RAMDirectory();
    }

    public void writerEntries(String Text) throws IOException{
        IndexWriter indexWriter = new IndexWriter(indexDir, config);
        indexWriter.commit();
        //txt output need to be (word in text: matched word : entity)
        //lower case,trim
        String modify = Text.toLowerCase();
        //remove accents
        modify = Normalizer.normalize(modify, Normalizer.Form.NFD);
        modify =  modify.replaceAll("[^\\p{ASCII}]", "");    
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
    
    private Document createDocument(String id, String title, String content) {     
        Document doc = new Document();
        doc.add(new TextField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        return doc;
    }

    public void findSilimar(String searchForSimilar) throws IOException, InvalidTokenOffsetsException {
        IndexReader reader = DirectoryReader.open(indexDir);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        
        MoreLikeThis mlt = new MoreLikeThis(reader);
        mlt.setMinTermFreq(0);
        mlt.setMinDocFreq(0);
        mlt.setFieldNames(new String[]{"title", "content"});
        mlt.setAnalyzer(analyzer);

        Reader sReader = new StringReader(searchForSimilar);
        Query query = mlt.like(sReader, null);
        TopDocs topDocs = indexSearcher.search(query,list.length);

        Formatter formatter = new SimpleHTMLFormatter();
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 10);
        highlighter.setTextFragmenter(fragmenter);
        PrintWriter writer = new PrintWriter("output/Nickname.txt", "UTF-8");

        //Iterate over found results
        for ( ScoreDoc scoreDoc : topDocs.scoreDocs )
        {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String title = doc.get("title");
            String Content = doc.get("content");
            
            //Printing - to which document result belongs
            if(scoreDoc.score > 0.5 )
            {
                writer.print(scoreDoc.score +"\t");
                writer.print( title+"\t");
                writer.print(searchForSimilar+"\t");
                //System.out.print("content: "+ Content);

                //Create token stream
                TokenStream stream = TokenSources.getAnyTokenStream(reader, scoreDoc.doc, "content", analyzer);

                //Get highlighted text fragments
                //String frags = highlighter.getBestFragment(stream, Content);
               TextFragment[] frags = highlighter.getBestTextFragments(stream, Content, true, length.length);
                for (TextFragment frag : frags)
                {               
                    String s = frag.toString();
                    s =  s.replaceAll("<B>", "");  
                    s =  s.replaceAll("</B>", ""); 
                    writer.print(s +"\t");
                }
                writer.print("\n");
            }
        }
         reader.close(); 
         writer.close();
    }	
}