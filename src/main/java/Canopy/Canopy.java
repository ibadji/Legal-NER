/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Canopy;
import Canopy.URL;
import Canopy.TreeNode;
import Canopy.ParseError;
/**
 *
 * @author n
 */
public class Canopy {

    public static void main(String[] args) throws ParseError {
        TreeNode tree = URL.parse("http://example.com/search?q=hello#page=1");

        for (TreeNode node : tree.elements) {
            System.out.print(node.text);
        }

        /*  prints:

            0, http
            4, ://
            7, example.com
            18, /search
            25, ?q=hello
            33, #page=1       */
    }


    
}
