package org.example.practica8.views.profile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.practica8.constants.Role;
import org.example.practica8.entities.Manager;
import org.example.practica8.entities.UserDetailsImpl;
import org.example.practica8.entities.UserInfo;
import org.example.practica8.services.ManagerService;
import org.example.practica8.services.UserInfoService;
import org.example.practica8.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile Settings | Time Grid")
@RolesAllowed({Role.ADMIN, Role.MANAGER})
public class ProfileView extends VerticalLayout {

    private final UserInfoService userInfoService;
    private final ManagerService managerService;

    private final TextField nameField = new TextField("Name");
    private final EmailField emailField = new EmailField("Email");

    private final PasswordField currentPasswordField = new PasswordField("Current Password");
    private final PasswordField newPasswordField = new PasswordField("New Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm New Password");

    private final Binder<Manager> binder = new BeanValidationBinder<>(Manager.class);

    private VerticalLayout profileContent;
    private VerticalLayout passwordContent;

    private Manager currentManager;

    public ProfileView(UserInfoService userInfoService, ManagerService managerService) {
        this.userInfoService = userInfoService;
        this.managerService = managerService;

        setSizeFull();
        setPadding(true);

        loadCurrentUser();
        configureBindings();

        add(
                createHeader(),
                createTabs()
        );
    }

    private void loadCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserInfo userInfo = userDetails.getUserInfo();

        // Verificar si el usuario es un gerente
        if (userInfo instanceof Manager) {
            currentManager = (Manager) userInfo;
        } else if (Role.MANAGER.equals(userInfo.getRole())) {
            // Si el userInfo tiene role MANAGER pero no es de tipo Manager, cargarlo
            managerService.findById(userInfo.getId())
                    .ifPresent(manager -> currentManager = manager);
        } else {
            // Para usuarios admin, crear un Manager temporal
            currentManager = new Manager();
            currentManager.setId(userInfo.getId());
            currentManager.setEmail(userInfo.getEmail());
            currentManager.setPassword(userInfo.getPassword());
            currentManager.setRole(userInfo.getRole());
        }

        populateForm();
    }

    private HorizontalLayout createHeader() {
        H2 heading = new H2("Profile Settings");
        heading.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(heading);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        return header;
    }

    private Tabs createTabs() {
        Tab profileTab = new Tab("Profile Information");
        Tab passwordTab = new Tab("Change Password");

        Tabs tabs = new Tabs(profileTab, passwordTab);
        tabs.setWidthFull();

        profileContent = createProfileForm();
        passwordContent = createPasswordForm();

        // Mostrar inicialmente el formulario de perfil
        add(profileContent);

        tabs.addSelectedChangeListener(event -> {
            removeAll();
            add(createHeader(), tabs);

            if (event.getSelectedTab().equals(profileTab)) {
                add(profileContent);
            } else {
                add(passwordContent);
            }
        });

        return tabs;
    }

    private VerticalLayout createProfileForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(
                nameField,
                emailField
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        Button saveButton = new Button("Save Changes", e -> saveProfile());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(formLayout, saveButton);
        layout.setAlignItems(Alignment.START);
        layout.setPadding(false);
        return layout;
    }

    private VerticalLayout createPasswordForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(
                currentPasswordField,
                newPasswordField,
                confirmPasswordField
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        currentPasswordField.setRequired(true);
        newPasswordField.setRequired(true);
        confirmPasswordField.setRequired(true);

        Button changePasswordButton = new Button("Change Password", e -> changePassword());
        changePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(formLayout, changePasswordButton);
        layout.setAlignItems(Alignment.START);
        layout.setPadding(false);
        return layout;
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
    }

    private boolean validateEmailUniqueness(String email) {
        if (email == null || email.isEmpty() || email.equals(currentManager.getEmail())) {
            return true;
        }

        return !userInfoService.existsByEmail(email);
    }

    private void populateForm() {
        if (currentManager != null) {
            nameField.setValue(currentManager.getName() != null ? currentManager.getName() : "");
            emailField.setValue(currentManager.getEmail() != null ? currentManager.getEmail() : "");

            binder.readBean(currentManager);
        }
    }

    private void saveProfile() {
        if (!binder.writeBeanIfValid(currentManager)) {
            Notification.show("Please correct the errors in the form")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            managerService.updateProfile(
                    currentManager,
                    nameField.getValue(),
                    emailField.getValue()
            );

            Notification.show("Profile updated successfully")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error updating profile: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void changePassword() {
        if (currentPasswordField.isEmpty() || newPasswordField.isEmpty() || confirmPasswordField.isEmpty()) {
            Notification.show("All password fields are required")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!newPasswordField.getValue().equals(confirmPasswordField.getValue())) {
            Notification.show("New passwords do not match")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Aquí debería haber una validación del password actual
        // pero esa lógica no está implementada en los servicios compartidos

        try {
            managerService.updatePassword(currentManager, newPasswordField.getValue());

            // Limpiar campos de contraseña
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            Notification.show("Password changed successfully")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error changing password: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}