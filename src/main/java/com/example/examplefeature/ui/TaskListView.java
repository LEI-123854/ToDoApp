package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.EmailService;
import com.example.examplefeature.Task;
import com.example.examplefeature.TaskService;
import com.example.examplefeature.PdfDownload;
import com.example.QRCodeGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.server.StreamResource;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
public class TaskListView extends Main {

    private final TaskService taskService;
    private final TextField description;
    private final DatePicker dueDate;
    private final Button createBtn;
    private final Button pdfBtn;
    private final Button emailBtn; //email
    private final Grid<Task> taskGrid;

    public TaskListView(TaskService taskService) {
        this.taskService = taskService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        pdfBtn = new Button("Download PDF", event -> downloadPdf());
        pdfBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        //email
        emailBtn = new Button("Send by Email", event -> openEmailDialog());
        emailBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(ZoneId.systemDefault());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate())
                        .map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate()))
                .setHeader("Creation Date");

        taskGrid.addComponentColumn(task -> {
            Button qrBtn = new Button("Gerar QR Code", click -> generateQRCode(task));
            qrBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return qrBtn;
        }).setHeader("QR Code");

        taskGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN, LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL);

        add(new ViewToolbar("Task List",
                ViewToolbar.group(description, dueDate, createBtn, pdfBtn, emailBtn)));
        add(taskGrid);
    }

    private void createTask() {
        taskService.createTask(description.getValue(), dueDate.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void downloadPdf() {
        List<Task> tarefas = taskService.list();
        byte[] pdfBytes = PdfDownload.gerarPdf(tarefas);

        if (pdfBytes != null) {
            StreamResource resource = new StreamResource("relatorio.pdf",
                    () -> new ByteArrayInputStream(pdfBytes));
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.getStyle().set("display", "none");
            add(downloadLink);
            downloadLink.getElement().executeJs("this.click()");
            getElement().executeJs("setTimeout(() => $0.remove(), 100)", downloadLink.getElement());
        } else {
            Notification.show("Erro ao gerar o PDF", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void generateQRCode(Task task) {
        try {
            String data = "Task: " + task.getDescription() + "\nDue: " +
                    (task.getDueDate() != null ? task.getDueDate() : "N/A");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            QRCodeGenerator.generateQRCodeStream(data, 200, 200, outputStream);

            StreamResource resource = new StreamResource("task_" + task.getId() + ".png",
                    () -> new ByteArrayInputStream(outputStream.toByteArray()));

            Anchor downloadLink = new Anchor(resource, "Abrir QR");
            downloadLink.setTarget("_blank");

            Notification notification = new Notification(downloadLink);
            notification.setPosition(Notification.Position.BOTTOM_END);
            notification.setDuration(5000);
            notification.open();

        } catch (Exception e) {
            Notification.show("Erro ao gerar QR Code: " + e.getMessage(), 5000,
                            Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    //Email
    private void openEmailDialog() {
        Dialog dialog = new Dialog();
        TextField recipientField = new TextField("To:");
        recipientField.setWidth("300px");

        Button sendBtn = new Button("Enviar", e -> {
            String recipient = recipientField.getValue();
            if (recipient == null || recipient.isEmpty()) {
                Notification.show("Insira um email válido", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            EmailService.sendTaskListEmail(recipient, taskService.list());
            Notification.show("Email sent!", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(new H2("Send Task List by Email"), recipientField, sendBtn);
        dialog.open();
    }
}
