package com.example.examplefeature;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.io.ByteArrayOutputStream;

public class PdfDownload {

    public static byte[] gerarPdf(List<Task> tarefas) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Relatório de Tarefas"));
            document.add(new Paragraph(" "));

            for (Task tarefa : tarefas) {
                String linha = String.format("• %s (Data limite: %s)",
                        tarefa.getDescription(),
                        tarefa.getDueDate() != null ? tarefa.getDueDate().toString() : "Sem data");
                document.add(new Paragraph(linha));
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


