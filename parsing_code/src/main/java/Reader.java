import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Reader {

    public String path;
    public String fileName;
    public String data[];


    public Reader(String path, String fileName){
        this.path = path;
        this.fileName = fileName + ".pdf";
    }

    public void read(){
        try  {
            PDDocument document = PDDocument.load(new File(path+fileName));
            document.getClass();

            if (!document.isEncrypted()) {

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);

                data = pdfFileInText.split("\\r?\\n");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getData(){
        return data;
    }



}
