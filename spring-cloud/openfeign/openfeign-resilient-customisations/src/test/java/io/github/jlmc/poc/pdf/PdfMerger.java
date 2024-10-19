package io.github.jlmc.poc.pdf;

import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PdfMerger {

    public static byte[] mergePDFs(byte[]... pdfByteArrays) throws IOException {
        // PDFMergerUtility to merge the PDFs
        PDFMergerUtility mergerUtility = new PDFMergerUtility();

        // ByteArrayOutputStream to hold the merged PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Add all PDFs (as ByteArrayInputStream) to the merger utility
            for (byte[] pdfBytes : pdfByteArrays) {
                //ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
                //mergerUtility.addSource(new RandomAccessReadBufferedFile());

                mergerUtility.addSource(new RandomAccessReadBuffer(pdfBytes));
            }

            // Set the destination stream
            mergerUtility.setDestinationStream(outputStream);

            // Merge the PDFs
            mergerUtility.mergeDocuments(null);

            // Return the merged PDF as a byte array
            return outputStream.toByteArray();
        }
    }

    public static void saveBytesToFile(byte[] data, Path filePath) throws IOException {
        // Write the byte array to the specified file
        Files.write(filePath, data);
    }
}
