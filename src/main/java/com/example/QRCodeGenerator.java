package com.example;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QRCodeGenerator {

    /**
     * Gera um QR Code e salva como arquivo PNG no disco.
     * Útil se você quiser manter os arquivos físicos.
     */
    public static void generateQRCodeImage(String data, int width, int height, String fileName) throws Exception {
        // Caminho dentro da pasta 'static/qrcodes' do projeto Spring Boot
        Path directory = Paths.get("src/main/resources/static/qrcodes");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve(fileName);
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(matrix, "PNG", filePath);
    }

    /**
     * Gera um QR Code diretamente em memória e escreve no OutputStream.
     * Útil para usar com StreamResource no Vaadin, sem salvar arquivo físico.
     */
    public static void generateQRCodeStream(String data, int width, int height, OutputStream outputStream) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
    }
}
