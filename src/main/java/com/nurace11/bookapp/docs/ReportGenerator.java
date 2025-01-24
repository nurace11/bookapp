package com.nurace11.bookapp.docs;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.nurace11.bookapp.model.ReportModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

@Component
public class ReportGenerator {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(new Locale("ru"));

    public byte[] generateReport(ReportModel model, LocalDate dateFrom, LocalDate dateTo) {
        WriterProperties writerProperties = new WriterProperties()
                .setFullCompressionMode(true)
                .setPdfVersion(PdfVersion.PDF_2_0)
                .setCompressionLevel(CompressionConstants.BEST_COMPRESSION);

        System.out.println(model);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(out, writerProperties.setFullCompressionMode(true).setPdfVersion(PdfVersion.PDF_2_0));
             Document document = new Document(new PdfDocument(writer))) {

            PdfFont font = PdfFontFactory.createFont("/assets/fonts/arial.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            document.setFont(font);
            document.setMargins(20, 20, 20, 30);

            document.add(new Paragraph("Отчет BookApp по книгам").setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Даты публикации книг: с ")
                    .add(new Text(dateTimeFormatter.format(dateFrom)).simulateBold())
                    .add(" по ")
                    .add(new Text(dateTimeFormatter.format(dateTo)).simulateBold()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Книги").setTextAlignment(TextAlignment.CENTER));
            Table booksTable = new Table(new float[4]).useAllAvailableWidth();
            booksTable.addCell(createHeaderCell("Идентификационный номер"));
            booksTable.addCell(createHeaderCell("Название"));
            booksTable.addCell(createHeaderCell("Дата публикации"));
            booksTable.addCell(createHeaderCell("Дата создания записи"));
            for (var book : model.getBooks()) {
                booksTable.addCell(createTextCell(book.getBookId()));
                booksTable.addCell(createTextCell(book.getBookName()));
                if (Objects.nonNull(book.getPublishDate())) {
                    booksTable.addCell(createTextCell(book.getPublishDate().toString()));
                } else {
                    booksTable.addCell(new Cell());
                }
                if (Objects.nonNull(book.getCreatedDate())) {
                    booksTable.addCell(createTextCell(book.getCreatedDate().toString()));
                } else {
                    booksTable.addCell(new Cell());
                }
            }
            document.add(booksTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Авторы").setTextAlignment(TextAlignment.CENTER));
            Table authorsTable = new Table(new float[4]).useAllAvailableWidth();
            authorsTable.addCell(createHeaderCell("Идентификационный номер"));
            authorsTable.addCell(createHeaderCell("Имя"));
            authorsTable.addCell(createHeaderCell("Фамилия"));
            authorsTable.addCell(createHeaderCell("Дата создания записи"));
            for(var author : model.getAuthors()) {
                authorsTable.addCell(createTextCell(author.getAuthorId()));
                authorsTable.addCell(createTextCell(author.getFirstName()));
                authorsTable.addCell(createTextCell(author.getLastName()));
                if (Objects.nonNull(author.getCreatedDate())) {
                    authorsTable.addCell(createTextCell(author.getCreatedDate().toString()));
                } else {
                    authorsTable.addCell(new Cell());
                }
            }
            document.add(authorsTable);
            document.add(new Paragraph("\n"));

            PdfDocument pdfDocument = document.getPdfDocument();
            pdfDocument.setFlushUnusedObjects(true);
            pdfDocument.removeAllHandlers();
            pdfDocument.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Cell createTextCell(String text) {
        if (StringUtils.isNotEmpty(text)) {
            Cell cell = new Cell();
            cell.add(new Paragraph(text));
            return cell;
        }
        return new Cell();
    }

    private Cell createHeaderCell(String text) {
        if (StringUtils.isNotEmpty(text)) {
            Cell cell = new Cell();
            cell.add(new Paragraph(text).simulateBold());
            return cell;
        }
        return new Cell();
    }

    //    public byte[] generateReport() {
    ////
    ////        WriterProperties writerProperties = new WriterProperties()
    ////                .setFullCompressionMode(true)
    ////                .setPdfVersion(PdfVersion.PDF_2_0)
    ////                .setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
    ////
    ////        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
    ////             PdfWriter writer = new PdfWriter(out, writerProperties.setFullCompressionMode(true).setPdfVersion(PdfVersion.PDF_2_0));
    ////             Document document = new Document(new PdfDocument(writer))){
    ////            PdfFont font = PdfFontFactory.createFont("/assets/fonts/arial.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
    ////            document.setFont(font);
    ////            document.setMargins(20, 20, 20, 30);
    ////
    ////            document.add(new Paragraph("Отчет BookApp по книгам").setTextAlignment(TextAlignment.CENTER));
    ////            document.add(new Paragraph("Даты публикации книг: с ")
    ////                    .add(new Text(LocalDate.now().toString()).simulateBold())
    ////                    .add(" по ")
    ////                    .add(new Text(LocalDate.now().plusDays(3).toString()).simulateBold()));
    ////
    ////            PdfDocument pdfDocument = document.getPdfDocument();
    ////            pdfDocument.setFlushUnusedObjects(true);
    ////            pdfDocument.removeAllHandlers();
    ////            pdfDocument.close();
    ////            return out.toByteArray();
    ////        } catch (IOException e) {
    ////            throw new RuntimeException(e);
    ////        }
    ////    }
}
