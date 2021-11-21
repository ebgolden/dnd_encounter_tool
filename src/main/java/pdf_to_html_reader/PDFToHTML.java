package pdf_to_html_reader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class PDFToHTML {
    public static void main(String[] args) {
        PDDocument pdDoc = null;
        PDFTextStripper pdfStripper;

        String parsedText;
        String fileName = "C://Users/Pieki/Desktop/Hunt for Orange October DnD Campaign/Encounters/6 Poseidon's Shoehorn (Ship)/Poseidon's Shoehorn - Good/Finasaer_male_half-elf - Sailing Master/Finasaer_male_half-elf - Sailing Master.pdf";
        try {
            pdfStripper = new PDFTextStripper();
            pdDoc = PDDocument.load(new File(fileName));
            parsedText = pdfStripper.getText(pdDoc);
            System.out.println(parsedText);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }
}