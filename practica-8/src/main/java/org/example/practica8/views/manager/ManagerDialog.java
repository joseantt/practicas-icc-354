package org.example.practica8.views.manager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import org.example.practica8.entities.Manager;
import org.example.practica8.services.ManagerService;
import org.example.practica8.services.UserInfoService;

public class ManagerDialog extends Dialog {

    private final Manager manager;
    private final ManagerService managerService;
    private final UserInfoService userInfoService;
    private final Runnable onSaveCallback;
    private final Binder<Manager> binder;

    private final TextField nameField = new TextField("Name");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");

    public ManagerDialog(Manager manager, ManagerService managerService, Runnable onSaveCallback) {
        this.manager = manager;
        this.managerService = managerService;
        this.userInfoService = null; // Inyectar si es necesario
        this.onSaveCallback = onSaveCallback;

        binder = new BeanValidationBinder<>(Manager.class);

        configureDialog();
        configureForm();
        configureBindings();
        populateForm();
    }

    private void configureDialog() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setWidth("500px");

        String title = manager.getId() == null ? "Add Manager" : "Edit Manager";
        H3 headerText = new H3(title);
        headerText.getStyle().set("margin-top", "0");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                nameField,
                emailField,
                passwordField,
                confirmPasswordField
        );

        formLayout.setColspan(nameField, 2);
        formLayout.setColspan(emailField, 2);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        HorizontalLayout buttonLayout = createButtonLayout();

        layout.add(headerText, formLayout, buttonLayout);
        add(layout);
    }

    private HorizontalLayout createButtonLayout() {
        Button cancelButton = new Button("Cancel", e -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button saveButton = new Button("Save", e -> saveManager());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(VaadinIcon.CHECK.create());

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();
        buttonLayout.getStyle().set("margin-top", "20px");

        return buttonLayout;
    }

    private void configureForm() {
        nameField.setRequired(true);
        emailField.setRequired(true);

        // Solo requerimos contraseña para nuevos gerentes
        boolean isNewManager = manager.getId() == null;
        passwordField.setRequired(isNewManager);
        confirmPasswordField.setRequired(isNewManager);

        if (!isNewManager) {
            passwordField.setHelperText("Leave blank to keep current password");
            confirmPasswordField.setHelperText("Leave blank to keep current password");
        }
    }

    private void configureBindings() {
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(Manager::getName, Manager::setName);

        binder.forField(emailField)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Invalid email address"))
                .withValidator(this::validateEmailUniqueness, "Email already in use")
                .bind(Manager::getEmail, Manager::setEmail);

        // Para la contraseña, usamos un enfoque diferente ya que no está directamente vinculada
        // y necesitamos validación personalizada
    }

    private boolean validateEmailUniqueness(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }

        // Si estamos editando y el email no ha cambiado, es válido
        if (manager.getId() != null && email.equalsIgnoreCase(manager.getEmail())) {
            return true;
        }

        System.out.println("Pase por aqui");
        // Verificar si el email ya está en uso
        //return !userInfoService.existsByEmail(email);
        return true; // Cambiar esto por la lógica real de verificación
    }

    private void populateForm() {
        if (manager.getId() != null) {
            nameField.setValue(manager.getName() != null ? manager.getName() : "");
            emailField.setValue(manager.getEmail() != null ? manager.getEmail() : "");
        }

        binder.readBean(manager);
    }

    private void saveManager() {
        if (!binder.writeBeanIfValid(manager)) {
            return;
        }

        // Validación de contraseña
        if (manager.getId() == null || !passwordField.getValue().isEmpty()) {
            if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show("Passwords do not match")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            manager.setPassword(passwordField.getValue());
        }

        try {
            managerService.save(manager);
            onSaveCallback.run();

            String message = manager.getId() == null ?
                    "Manager created successfully" :
                    "Manager updated successfully";

            Notification.show(message)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            close();
        } catch (Exception e) {
            Notification.show("Error saving manager: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}