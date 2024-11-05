import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.text.*;
import javax.swing.text.Element;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class RtfToPdfConverter {

    public static void convertStyledTextToPdf(StyledDocument doc, String outputPdfPath, String fontPath) {
        try {
            Document pdfDocument = new Document();
            PdfWriter.getInstance(pdfDocument, new FileOutputStream(outputPdfPath));
            pdfDocument.open();

            // Custom font to support Armenian characters
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // Iterate through the document to capture styles and apply them in PDF
            for (int i = 0; i < doc.getLength(); ) {
                Element element = doc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                String text = doc.getText(i, element.getEndOffset() - i);

                // Determine the font style (bold, italic)
                int style = Font.NORMAL;
                if (StyleConstants.isBold(attrs) && StyleConstants.isItalic(attrs)) {
                    style = Font.BOLDITALIC;
                } else if (StyleConstants.isBold(attrs)) {
                    style = Font.BOLD;
                } else if (StyleConstants.isItalic(attrs)) {
                    style = Font.ITALIC;
                }

                // Set font size and style
                Font font = new Font(baseFont, StyleConstants.getFontSize(attrs), style);

                // Create a Paragraph with the styled text
                Paragraph paragraph = new Paragraph(new Chunk(text, font));

                // Check for paragraph alignment and set it
                int alignment = StyleConstants.getAlignment(attrs);
                switch (alignment) {
                    case StyleConstants.ALIGN_CENTER:
                        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
                        break;
                    case StyleConstants.ALIGN_RIGHT:
                        paragraph.setAlignment(Paragraph.ALIGN_RIGHT);
                        break;
                    default:
                        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
                        break;
                }

                // Add the styled paragraph to the PDF
                pdfDocument.add(paragraph);

                i = element.getEndOffset();
            }

            pdfDocument.close();
            System.out.println("Styled PDF generated successfully at: " + outputPdfPath);

        } catch (IOException | DocumentException | BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static StyledDocument extractStyledTextUsingRTFEditorKit(String rtfFilePath) {
        RTFEditorKit rtfParser = new RTFEditorKit();
        DefaultStyledDocument document = new DefaultStyledDocument();

        try (FileInputStream fis = new FileInputStream(rtfFilePath)) {
            rtfParser.read(fis, document, 0);
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static void main(String[] args) {
        String rtfFilePath = "G:\\DownLoads\\Արայիկ Թեզ Գլուխ 1.RTF";
        String outputPdfPath = "G:\\DownLoads\\Արայիկ Թեզ Գլուխ 1_converted.pdf";
        String fontPath = "G:\\DownLoads\\fonter.am_ghea-grapalat\\GHEAGrpalatReg.otf";  // Armenian-supporting font

        // Extract styled text from RTF
        StyledDocument styledDoc = extractStyledTextUsingRTFEditorKit(rtfFilePath);

        // Convert styled text to PDF
        convertStyledTextToPdf(styledDoc, outputPdfPath, fontPath);
    }
}
