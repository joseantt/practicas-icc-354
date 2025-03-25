package org.example.practica3.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.practica3.utils.constants.Role;
import org.example.practica3.entities.UserInfo;
import org.example.practica3.services.UserInfoService;
import org.example.practica3.views.components.StyledGrid;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@RolesAllowed(Role.ADMIN)
@Route(value = "admin-panel/user-management", layout = MainLayout.class)
@PageTitle("User management | MockupAPP")
public class UserManagementView extends VerticalLayout implements Serializable {
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final Select<String> role = new Select<>();
    private final PasswordField confirmPassword = new PasswordField("Confirm password");
    private final StyledGrid<UserInfo> grid = new StyledGrid<>(UserInfo.class, false);

    private final Binder<UserInfo> binder = new Binder<>(UserInfo.class);
    private final UserInfoService userInfoService;

    public UserManagementView(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;

        var horizontalLayout = new HorizontalLayout();
        var createUserBtn = new Button("Create user", VaadinIcon.PLUS.create());

        var formDialog = createUserDialog();
        createUserBtn.addClickListener(click -> formDialog.open());

        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.add(new H2("All users"), createUserBtn);

        List<Component> fields = Arrays.asList(username, password, confirmPassword, role);
        fields.forEach(field -> field.getElement().getStyle().set("width", "100%"));
        role.setLabel("Role");
        role.setItems("User", "Admin");
        role.setValue("User");

        binder.bindInstanceFields(this);
        setupValidations(binder);

        grid.setColumns("id", "username", "role");
        grid.setItems(userInfoService.findAll());
        grid.setSizeFull();

        grid.addColumn(new ComponentRenderer<>(userInfo -> {
            if(userInfo.getUsername().equals("admin")){
                return null;
            }

            Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            deleteButton.addClickListener(click -> {
                try {
                    userInfoService.deleteById(userInfo.getId());
                    grid.getListDataView().removeItem(userInfo);
                    Notification.show("User deleted successfully",
                            3000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception e) {
                    Notification.show("Error deleting user: ",
                            3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            return deleteButton;
        })).setHeader("Actions");

        setSizeFull();
        add(horizontalLayout, grid);
        expand(grid);
    }

    private Dialog createUserDialog() {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Create new user");

        var mainLayout = new VerticalLayout();
        mainLayout.setSpacing(false);
        mainLayout.setPadding(false);
        mainLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        var formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        formLayout.add(username, password, confirmPassword, role);

        formLayout.setColspan(username, 2);
        formLayout.setColspan(password, 1);
        formLayout.setColspan(confirmPassword, 1);
        formLayout.setColspan(role, 2);

        dialog.getElement().getStyle()
                .set("min-width", "300px")
                .set("max-width", "100%")
                .set("width", "600px");

        var cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener(click -> {
            dialog.close();
            binder.readBean(null);
            role.setValue("User");
        });

        var submitBtn = new Button("Create");
        submitBtn.getStyle().setBackgroundColor("#58bc82").setColor("white");

        submitBtn.addClickListener(click -> {
            UserInfo userInfo = new UserInfo();
            if (binder.writeBeanIfValid(userInfo)) {
                createUser(userInfo, dialog);
            }
        });

        var buttonGroup = new HorizontalLayout();
        buttonGroup.setWidthFull();
        buttonGroup.getStyle().setPaddingTop("10px");
        buttonGroup.setSpacing(true);
        buttonGroup.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonGroup.add(cancelBtn, submitBtn);

        mainLayout.add(formLayout, buttonGroup);
        dialog.add(mainLayout);

        return dialog;
    }

    private void createUser(UserInfo user, Dialog dialog) {
        try {
            user.setRole(role.getValue().toUpperCase());
            userInfoService.save(user);
            Notification.show("User created successfully", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
            grid.getListDataView().addItem(user);
            binder.readBean(null);
            role.setValue("User");
        } catch (Exception e) {
            Notification.show("Error creating user: " + e.getMessage(),
                    3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);

        }
    }

    private void setupValidations(Binder<UserInfo> binder) {
        binder.forField(username)
                .asRequired("Username is required")
                .withValidator(username -> !username.contains(" "), "Username must not contain spaces")
                .withValidator(username -> userInfoService.findByUsername(username) == null, "Username already exists")
                .bind(UserInfo::getUsername, UserInfo::setUsername);

        binder.forField(password)
                .asRequired("Password is required")
                .withValidator(password -> password.length() >= 8,
                        "Password must be at least 8 characters long")
                .bind(UserInfo::getPassword, UserInfo::setPassword);

        password.addValueChangeListener(event -> {
            if (!password.getValue().equals(confirmPassword.getValue())) {
                confirmPassword.setErrorMessage("Passwords do not match");
                confirmPassword.setInvalid(true);
                return;
            }
             confirmPassword.setInvalid(false);
        });

        binder.forField(confirmPassword)
                .asRequired("Password confirmation is required")
                .withValidator(confirmPassword ->
                                confirmPassword.equals(password.getValue()),
                        "Passwords do not match")
                .bind(UserInfo::getPassword, (user, password) -> {});

        binder.forField(role)
                .asRequired("Role is required")
                .bind(UserInfo::getRole, UserInfo::setRole);
    }
}