package NER;

import com.wantedtech.common.xpresso.x;
import com.wantedtech.common.xpresso.types.*;

public class Demo {
    public static void main(String[] args) {
        
       //need to rebuild the strign
        String s = "Retirar el recurso contra esta ley catalana ante el TC es muy fácil, Sr. @sanchezcastejon. No cuesta nada ser simpático. https://twitter.com/bufetalmeida/status/978519262594772992?s=21 …";
        String lookeup= "ley mordaza";
        int position = x.String(s).search(lookeup);
        x.print(s.substring(position, position+lookeup.length()));
        

    }
}