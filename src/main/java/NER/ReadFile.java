/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author n
 */
public class ReadFile {
    public static String readFile(String pathname) throws IOException {
        File file = new File(pathname);
        Locale loc = new Locale("es", "ES");
        Scanner scanner = new Scanner(new FileInputStream(file), "UTF-8");
        scanner.useLocale(loc);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    
    }
    public static String readFile2(String FilePath) throws IOException{
    
        BufferedReader br = null;
        File fr = new File(FilePath);

  
        br =   new BufferedReader(new InputStreamReader(new FileInputStream(fr)));

        String Line;
        StringBuffer buffer=new StringBuffer();

        while ((Line = br.readLine()) != null) {
           
            buffer.append(Line +"\n");
        }

        br.close();
        
        //System.out.println(buffer.toString());
        
        return buffer.toString();


    }
}
