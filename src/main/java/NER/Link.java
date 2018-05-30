package NER;


import java.io.IOException;
import org.jsoup.Jsoup;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author n
 */
public class Link {
    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
	public static String findLink(String searchTerm) throws IOException {
		int num = 1;
		
		String searchURL = GOOGLE_SEARCH_URL + "?q="+searchTerm+"&num="+num;
		//without proper User-Agent, we will get 403 error
		org.jsoup.nodes.Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
		
		//below will print HTML data, save it to a file and open in browser to compare
		//System.out.println(doc.html());
		
		//If google search results HTML change the <h3 class="r" to <h3 class="r1"
		//we need to change below accordingly
		org.jsoup.select.Elements results = doc.select("h3.r > a");
                String finalResult = "";
		for (org.jsoup.nodes.Element result : results) {
			String linkHref = result.attr("href");
			String linkText = result.text();
			//System.out.println("Text::" + linkText + ", URL::" + linkHref.substring(6, linkHref.indexOf("&")));
                        finalResult = linkHref.substring(6, linkHref.indexOf("&"));

		}
            return finalResult;
	}
}
