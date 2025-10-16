package com.example.examplefeature;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.time.temporal.ChronoUnit; //
import com.example.examplefeature.Task;
import com.example.examplefeature.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void createTask(String description, @Nullable LocalDate dueDate) {
        if ("fail".equals(description)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var task = new Task(description, LocalDateTime.now());
        task.setDueDate(dueDate);
        taskRepository.saveAndFlush(task);
    }

    @Transactional(readOnly = true)
    public List<Task> list(Pageable pageable) {
        return taskRepository.findAllBy(pageable).toList();
    }
    public List<Task> list() {
        return taskRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task não encontrada com id " + id));
    }

    /**
     * Gera o QR Code de uma task e salva num diretório seguro no sistema.
     */
    public void generateQRCodeForTask(Task task, String fileName) throws Exception {
        String data = "Task: " + task.getDescription() +
                "\nDue Date: " + (task.getDueDate() != null ? task.getDueDate() : "Not set") +
                "\nCreated At: " + task.getCreationDate();

        int width = 300;
        int height = 300;

        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);

        // Diretório seguro para salvar QR Codes (dentro do home do usuário)
        Path directory = Paths.get(System.getProperty("user.home"), "qrcodes");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve(fileName);
        MatrixToImageWriter.writeToPath(matrix, "PNG", filePath);

        System.out.println("QR Code gerado em: " + filePath.toAbsolutePath());
    }

    /**
     * Método auxiliar genérico para criar QR Codes de qualquer string
     */
    public void generateQRCodeImage(String data, int width, int height, String fileName) throws Exception {
        Path directory = Paths.get(System.getProperty("user.home"), "qrcodes");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve(fileName);
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(matrix, "PNG", filePath);

        System.out.println("QR Code gerado em: " + filePath.toAbsolutePath());
    }
}
