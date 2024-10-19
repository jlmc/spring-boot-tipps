package io.github.jlmc.poc.pdf;

import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.jlmc.poc.pdf.PdfMerger.mergePDFs;
import static io.github.jlmc.poc.pdf.PdfMerger.saveBytesToFile;

/**
 *         <dependency>
 *             <groupId>org.apache.pdfbox</groupId>
 *             <artifactId>pdfbox</artifactId>
 *             <version>3.0.3</version>
 *             <scope>test</scope>
 *         </dependency>
 */
public class PdfMergeTest {

    @Test
    void mergingPdfs() throws IOException {

        Resource resource1 = new ClassPathResource("pdfs/file-1.pdf");
        Resource resource2 = new ClassPathResource("pdfs/file-2.pdf");

        byte[] contentAsByteArray1 = resource1.getContentAsByteArray();
        byte[] contentAsByteArray2 = resource2.getContentAsByteArray();

        byte[] mergedPdfBytes = mergePDFs(contentAsByteArray1, contentAsByteArray2);

        saveBytesToFile(mergedPdfBytes, Paths.get("merged_output.pdf"));

        new File("merged_output.pdf").delete();
    }

}
