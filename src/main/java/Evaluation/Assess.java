package Evaluation;

import NER.ReadFile;
import java.io.IOException;


/**
 * @deprecated
 * @author ines badji
 */
public class Assess {
        private static ReadFile read = new ReadFile();

    public static void main(String[] args) throws IOException 
    {
        String sentence = read.readFile("resources/inputText/tweets.txt");

    }
}