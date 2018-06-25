package NER;

import info.debatty.java.stringsimilarity.*;

//Levenshtein Distance
public class CalculateSimilarity {
    
    public static void main(String[] args)
    {
    }
    
    public static double calculate(String x, String y)
    {
        JaroWinkler jw = new JaroWinkler();
        return jw.similarity(x, y);
    }
       
}
