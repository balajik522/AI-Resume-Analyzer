package com.balaji.atsanalyzer.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    public String extractText(File imageFile) {

        try {
            Tesseract tesseract = new Tesseract();

            // Path to tessdata folder
            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");

            // Language
            tesseract.setLanguage("eng");

            return tesseract.doOCR(imageFile);

        } catch (TesseractException e) {
            e.printStackTrace();
            return "OCR Error: " + e.getMessage();
        }
    }
}