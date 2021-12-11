package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

public class PDFToText {
    public String getTextFromPDF(String fileName) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = PDDocument.load(new File(fileName));
        String text = pdfStripper.getText(pdDoc);
        pdDoc.close();
        return text;
    }
}