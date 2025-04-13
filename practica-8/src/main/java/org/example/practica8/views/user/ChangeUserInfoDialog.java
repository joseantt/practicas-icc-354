package org.example.practica8.views.user;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.example.practica8.entities.UserInfo;
import org.example.practica8.services.UserInfoService;
import org.example.practica8.views.components.CrudDialogButtons;
import org.springframework.security.core.context.SecurityContextHolder;

public class ChangeUserInfoDialog {
    private final AuthenticationContext authenticationContext;
    private final UserInfoService userInfoService;
    private UserInfo currentUser;

    private final Dialog dialog;
    private final EmailField email;
    private final TextField name;

    private final Binder<UserInfo> binder;

    public ChangeUserInfoDialog(UserInfoService userInfoService, AuthenticationContext authenticationContext){
        this.authenticationContext = authenticationContext;
        this.userInfoService = userInfoService;

        dialog = new Dialog("Account settings");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        binder = new Binder<>(UserInfo.class);
        email = new EmailField("Email address");
        name = new TextField("Name");

        configureBindings();

        FormLayout layout = createFormLayout();
        HorizontalLayout buttonLayout = new CrudDialogButtons(this::saveChanges, dialog);

        dialog.add(layout, buttonLayout);
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();

        formLayout.add(name, email);
        formLayout.setColspan(email, 2);
        formLayout.setColspan(name, 2);

        return formLayout;
    }

    public void open() {
        // This can be optimized 100%, but because of the time constraints, I will leave it like this
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        this.currentUser = userInfoService.findByEmail(currentUserEmail);

        email.setValue(currentUserEmail);
        name.setValue(currentUser.getName() == null ? "" : currentUser.getName());

        binder.readBean(currentUser);
        dialog.open();
    }

    public void saveChanges() {
        String pastEmail = currentUser.getEmail();

        if(!binder.writeBeanIfValid(currentUser)) return;

        userInfoService.save(currentUser);

        if(!pastEmail.equals(currentUser.getEmail())) {
            authenticationContext.logout();
            return;
        }

        Notification.show("User modified successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        dialog.close();
    }

    private void configureBindings() {
        binder.forField(email)
                .asRequired("Email is required")
                .withValidator(
                        value -> value != null && value.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"),
                        "Insert a valid email address")
                .withValidator(this::isEmailAvailable, "Email already in use")
                .bind(UserInfo::getEmail, UserInfo::setEmail);

        binder.forField(name).bind(UserInfo::getName, UserInfo::setName);
    }

    private boolean isEmailAvailable(String email) {
        if (currentUser != null && email.equals(currentUser.getEmail())) {
            return true;
        }

        return !userInfoService.existsByEmail(email);
    }
}
