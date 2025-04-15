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
import org.example.practica8.constants.Role;
import org.example.practica8.entities.UserInfo;
import org.example.practica8.services.UserInfoService;

public class ManagerDialog extends Dialog {

    private final UserInfo user;
    private final UserInfoService userInfoService;
    private final Runnable onSaveCallback;
    private final Binder<UserInfo> binder;

    private final TextField nameField = new TextField("Name");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");

    public ManagerDialog(UserInfo user, UserInfoService userInfoService, Runnable onSaveCallback) {
        this.user = user;
        this.userInfoService = userInfoService;
        this.onSaveCallback = onSaveCallback;

        binder = new BeanValidationBinder<>(UserInfo.class);

        configureDialog();
        configureForm();
        configureBindings();
        populateForm();
    }

    private void configureDialog() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setWidth("500px");

        String title = user.getId() == null ? "Add Manager" : "Edit Manager";
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

        Button saveButton = new Button("Save", e -> saveUser());
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

        // Only require password for new users
        boolean isNewUser = user.getId() == null;
        passwordField.setRequired(isNewUser);
        confirmPasswordField.setRequired(isNewUser);

        if (!isNewUser) {
            passwordField.setHelperText("Leave blank to keep current password");
            confirmPasswordField.setHelperText("Leave blank to keep current password");
        }
    }

    private void configureBindings() {
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(UserInfo::getName, UserInfo::setName);

        binder.forField(emailField)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Invalid email address"))
                .withValidator(this::validateEmailUniqueness, "Email already in use")
                .bind(UserInfo::getEmail, UserInfo::setEmail);

        // Password is handled separately in saveUser method
    }

    private boolean validateEmailUniqueness(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }

        // If we're editing and the email hasn't changed, it's valid
        if (user.getId() != null && email.equalsIgnoreCase(user.getEmail())) {
            return true;
        }

        // Check if email is already in use
        return !userInfoService.existsByEmail(email);
    }

    private void populateForm() {
        if (user.getId() != null) {
            nameField.setValue(user.getName() != null ? user.getName() : "");
            emailField.setValue(user.getEmail() != null ? user.getEmail() : "");
        }

        binder.readBean(user);
    }

    private void saveUser() {
        if (!binder.writeBeanIfValid(user)) {
            return;
        }

        // Set role to MANAGER for new users
        if (user.getId() == null) {
            user.setRole(Role.MANAGER);
        }

        // Password validation
        if (user.getId() == null || !passwordField.getValue().isEmpty()) {
            if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show("Passwords do not match")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            user.setPassword(passwordField.getValue());
        }

        try {
            userInfoService.save(user);
            onSaveCallback.run();

            String message = user.getId() == null ?
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