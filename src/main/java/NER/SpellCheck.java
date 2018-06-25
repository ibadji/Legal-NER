package NER;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.RAMDirectory;



public class SpellCheck {
    
  public static void main(String[] args) {
      Set<String> j = new HashSet<String>();
      j.add("luceni");
      j.add("lucene");
      j.add("lucenee");
      String[] result = suggestEndpointOptions(j, "lucene");
      

  }
      public static String[] suggestEndpointOptions(Set<String> names, String unknownOption) {
        // each option must be on a separate line in a String
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append(name);
            sb.append("\n");
        }
        StringReader reader = new StringReader(sb.toString());

        try {
            PlainTextDictionary words = new PlainTextDictionary(reader);

            // use in-memory lucene spell checker to make the suggestions
            RAMDirectory dir = new RAMDirectory();
            SpellChecker checker = new SpellChecker(dir);
            checker.indexDictionary(words, new IndexWriterConfig(new KeywordAnalyzer()), false);
            int maxSuggestions=5;

            return checker.suggestSimilar(unknownOption, maxSuggestions);
        } catch (Exception e) {
            // ignore
        }

        return null;
    }
}