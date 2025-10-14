package com.example.examplefeature.controller;

import com.example.examplefeature.Task;
import com.example.examplefeature.TaskService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    private final TaskService taskService;

    // Construtor para injeção de dependência do TaskService
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Endpoint para listar todas as Tasks
    @GetMapping("/tasks")
    public String listTasks(Model model) {
        // Limite arbitrário de 100 Tasks, pode ajustar
        model.addAttribute("tasks", taskService.list(PageRequest.ofSize(100)));
        return "tasks"; // nome do template HTML
    }

    // Endpoint para gerar QR code de uma Task específica
    @PostMapping("/tasks/{id}/qrcode")
    public String generateTaskQRCode(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        // Salva o QR code dentro da pasta static
        String filePath = "src/main/resources/static/task_" + id + "_qrcode.png";
        try {
            taskService.generateQRCodeForTask(task, filePath);
            model.addAttribute("qrCodePath", "/task_" + id + "_qrcode.png");
            model.addAttribute("message", "QR code gerado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("message", "Erro ao gerar QR code: " + e.getMessage());
        }
        // Recarrega a lista de Tasks para continuar exibindo a tabela
        model.addAttribute("tasks", taskService.list(PageRequest.ofSize(100)));
        return "tasks"; // retorna para a mesma página
    }
}
