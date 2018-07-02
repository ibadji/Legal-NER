package NER;

import com.wantedtech.common.xpresso.x;
import com.wantedtech.common.xpresso.types.*;
import java.util.Scanner;
import org.parboiled.Parboiled;
import org.parboiled.common.StringUtils;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import org.parboiled.support.ParsingResult;

public class Demo {
    public static void main(String[] args) {
        
       //need to rebuild the strign
//        String s = "Retirar el recurso contra esta ley catalana ante el TC es muy fácil, Sr. @sanchezcastejon. No cuesta nada ser simpático. https://twitter.com/bufetalmeida/status/978519262594772992?s=21 …";
//        String lookeup= "ley mordaza";
//        int position = x.String(s).search(lookeup);
//        x.print(s.substring(position, position+lookeup.length()));

        String input = "1+2 hello";
        CalculatorParser parser = Parboiled.createParser(CalculatorParser.class);
        ParsingResult<?> result = new ReportingParseRunner(parser.Expression()).run(input);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        System.out.println(parseTreePrintOut);
    }

}
