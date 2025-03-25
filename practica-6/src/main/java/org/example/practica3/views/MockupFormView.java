package org.example.practica3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.*;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import jakarta.annotation.security.PermitAll;
import org.example.practica3.entities.Header;
import org.example.practica3.entities.Mockup;
import org.example.practica3.entities.Project;
import org.example.practica3.services.MockupService;
import org.example.practica3.services.ProjectService;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.practica3.utils.Validations.userCanEnter;

@PermitAll
@Route(value = "project-management/:projectId/mockup/:mockupId?", layout = MainLayout.class)
@PageTitle("Mockup Form | MockupAPP")
public class MockupFormView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver, Serializable {
    private Project project;
    private Mockup mockup;
    private boolean isEditMode = false;

    private final transient MockupService mockupService;
    private final transient ProjectService projectService;

    private final FormLayout formLayout = new FormLayout();
    private final VerticalLayout headersContainer = new VerticalLayout();

    private H3 title;
    private TextField name;
    private TextArea description;
    private TextField path;
    private Select<String> accessMethod;
    private Select<String> contentType;
    private Select<Integer> responseCode;
    private IntegerField responseTime;
    private Select<Integer> expirationTime;
    private TextArea responseBody;
    private Button saveButton;
    private Button addHeaderButton;
    private final HorizontalLayout buttonLayout;

    private final Hr separator = new Hr();
    private final Hr separator2 = new Hr();

    private final Binder<Mockup> binder = new Binder<>(Mockup.class);

    public MockupFormView(MockupService mockupService, ProjectService projectService) {
        this.mockupService = mockupService;
        this.projectService = projectService;

        initializeComponents();
        buttonLayout = new HorizontalLayout(addHeaderButton);

        setupComponents();
        createBinder();
        setFieldSizes();

        // Language selector
        Select<String> languageSelect = new Select<>();
        languageSelect.setLabel(getTranslation("mockup.language"));
        languageSelect.setItems("en", "es");
        languageSelect.setValue(UI.getCurrent().getLocale().getLanguage());
        languageSelect.addValueChangeListener(event -> {
            UI.getCurrent().setLocale(new Locale(event.getValue()));
        });

        formLayout.add(name, description, path, separator,
                accessMethod, contentType, responseCode,
                responseTime, separator2, buttonLayout,
                headersContainer, responseBody, expirationTime, saveButton);

        add(languageSelect, title, formLayout);
    }

    private void initializeComponents() {
        title = new H3();
        name = new TextField(getTranslation("mockup.form.name"));
        description = new TextArea(getTranslation("mockup.form.description"));
        path = new TextField(getTranslation("mockup.form.path"));
        accessMethod = new Select<>();
        contentType = new Select<>();
        responseCode = new Select<>();
        responseTime = new IntegerField();
        expirationTime = new Select<>();
        responseBody = new TextArea(getTranslation("mockup.form.response.body"));
        saveButton = new Button();
        addHeaderButton = new Button(getTranslation("mockup.form.add.header"));
    }

    private void setupComponents() {
        separator.getStyle().setMarginTop("10px");
        separator2.getStyle().setMarginTop("10px");

        accessMethod.setLabel("Access method");
        contentType.setLabel("Content type");
        responseCode.setLabel("Response code");
        responseTime.setLabel("Response time (seconds)");
        responseTime.setHelperText("If empty, the response will be immediate");
        expirationTime.setLabel("Expiration time");

        path.setPlaceholder(getTranslation("mockup.form.path.placeholder"));
        path.setHelperText(getTranslation("mockup.form.path.helper"));

        accessMethod.setItems("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        accessMethod.setValue("GET");

        contentType.setItems("application/json", "application/xml", "text/xml", "text/json", "text/plain");
        contentType.setValue("application/json");

        responseCode.setItems(Arrays.stream(HttpStatus.values())
                .map(HttpStatus::value)
                .collect(Collectors.toList()));
        responseCode.setItemLabelGenerator(statusCode ->
                statusCode + " - " + HttpStatus.resolve(statusCode).getReasonPhrase());
        responseCode.setValue(HttpStatus.OK.value());

        setupExpirationTimes();

        saveButton.setIcon(VaadinIcon.CHECK_CIRCLE.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        saveButton.addClickListener(e -> saveMockup());

        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        headersContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        headersContainer.setSpacing(false);
        headersContainer.setPadding(false);
        addHeaderButton.getStyle().setMarginTop("10px").setMarginBottom("10px");
        addHeaderButton.setIcon(VaadinIcon.PLUS.create());
        addHeaderButton.addClickListener(e -> addNewFields());

        // Disable expiration time in edit mode
        expirationTime.setEnabled(!isEditMode);

        Checkbox requireJwtCheck = new Checkbox("Requiere autenticación JWT");
        requireJwtCheck.setValue(mockup != null && mockup.isRequiresJwt());

        formLayout.add(requireJwtCheck);

        // Actualizar el binder
        binder.forField(requireJwtCheck)
                .bind(Mockup::isRequiresJwt, Mockup::setRequiresJwt);
    }

    private void setupExpirationTimes() {
        Map<Integer, String> expirationOptions = new LinkedHashMap<>();
        expirationOptions.put(1, getTranslation("mockup.form.expiration.1hour"));
        expirationOptions.put(24, getTranslation("mockup.form.expiration.1day"));
        expirationOptions.put(168, getTranslation("mockup.form.expiration.1week"));
        expirationOptions.put(720, getTranslation("mockup.form.expiration.1month"));
        expirationOptions.put(8760, getTranslation("mockup.form.expiration.1year"));
        expirationTime.setItems(expirationOptions.keySet());
        expirationTime.setItemLabelGenerator(expirationOptions::get);
        expirationTime.setValue(1);
    }

    private void addNewFields() {
        FlexLayout fieldGroup = createHeaderFields();
        headersContainer.add(fieldGroup);
    }

    private FlexLayout createHeaderFields() {
        FlexLayout fieldGroup = new FlexLayout();
        fieldGroup.setJustifyContentMode(JustifyContentMode.CENTER);
        fieldGroup.setAlignItems(Alignment.CENTER);
        fieldGroup.setWidthFull();
        fieldGroup.setFlexWrap(FlexLayout.FlexWrap.WRAP);

        var headerName = new TextField();
        headerName.setPlaceholder(getTranslation("mockup.form.header.name.placeholder"));
        headerName.getStyle().setMarginRight("10px");
        headerName.setWidth("calc(40% - 20px)");

        var headerValue = new TextField();
        headerValue.setPlaceholder(getTranslation("mockup.form.header.value.placeholder"));
        headerValue.getStyle().setMarginRight("10px");
        headerValue.setWidth("calc(40% - 20px)");

        Button removeButton = new Button(VaadinIcon.TRASH.create());
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_ICON);
        removeButton.addClickListener(e -> headersContainer.remove(fieldGroup));

        fieldGroup.add(headerName, headerValue, removeButton);
        return fieldGroup;
    }

    private void setFieldSizes() {
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        formLayout.setColspan(name, 2);
        formLayout.setColspan(description, 2);
        formLayout.setColspan(path, 2);
        formLayout.setColspan(responseBody, 2);
        description.setHeight("100px");
        responseBody.setHeight("200px");
        formLayout.setColspan(separator, 2);
        formLayout.setColspan(separator2, 2);
        formLayout.setColspan(headersContainer, 2);
        formLayout.setColspan(buttonLayout, 2);
    }

    private void saveMockup() {
        List<Header> headers = obtainHeaders();

        // If in edit mode, use existing mockup
        if (isEditMode && mockup == null) {
            return;
        }

        // If in create mode, create new mockup
        if (!isEditMode) {
            mockup = new Mockup();
        }

        if(!binder.writeBeanIfValid(mockup) || project == null) {
            return;
        }

        mockup.setProject(project);
        mockup.setHeaders(headers);
        mockup.setResponseTimeInSecs(responseTime.isEmpty() ? 0 : responseTime.getValue());
        mockup.setExpirationTime(LocalDateTime.now().plusHours(expirationTime.getValue()));

        // In edit mode, keep the original expiration time
        if (!isEditMode) {
            mockup.setExpirationTimeInHours(expirationTime.getValue());
        }

        for(Header header : headers) {
            header.setMockup(mockup);
        }

        try {
            if (isEditMode) {
                mockupService.updateMockup(mockup.getId(), mockup);
                Notification.show(getTranslation("mockup.form.update.success"),
                                3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                mockupService.save(mockup);
                Notification.show(getTranslation("mockup.form.create.success"),
                                3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }

            // Navigate back to mockup list
            UI.getCurrent().navigate(
                    MockupListView.class,
                    new RouteParameters("projectId", String.valueOf(project.getId()))
            );
        } catch (Exception e) {
            Notification.show(
                    getTranslation("mockup.form.error") + ": " + e.getMessage(),
                    3000,
                    Notification.Position.TOP_CENTER
            ).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private List<Header> obtainHeaders() {
        List<Header> headers = new ArrayList<>();
        headersContainer.getChildren()
                .map(FlexLayout.class::cast)
                .map(headerGroup -> {
                    var headerName = (TextField) headerGroup.getComponentAt(0);
                    var headerValue = (TextField) headerGroup.getComponentAt(1);
                    return new String[]{headerName.getValue(), headerValue.getValue()};
                })
                .forEach(pair -> {
                    if (pair[0].isBlank() || pair[1].isBlank()) {
                        return;
                    }
                    var header = Header.builder()
                            .key(pair[0])
                            .value(pair[1])
                            .build();
                    headers.add(header);
                });
        return headers;
    }

    private void createBinder() {
        String pathRegex = "^(?!\\/|.*\\/\\/)(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%(?:[0-9A-Fa-f]{2})|\\/(?!\\/))*$";

        binder.forField(name)
                .asRequired(getTranslation("mockup.form.name.required"))
                .withValidator(name -> !name.isBlank(), getTranslation("mockup.form.name.required"))
                .bind(Mockup::getName, Mockup::setName);
        binder.forField(description)
                .bind(Mockup::getDescription, Mockup::setDescription);
        binder.forField(path)
                .asRequired(getTranslation("mockup.form.path.required"))
                .withValidator(new RegexpValidator(
                        getTranslation("mockup.form.path.invalid"),
                        pathRegex))
                .bind(Mockup::getPath, Mockup::setPath);
        binder.forField(accessMethod)
                .asRequired()
                .bind(Mockup::getAccessMethod, Mockup::setAccessMethod);
        binder.forField(contentType)
                .asRequired()
                .bind(Mockup::getContentType, Mockup::setContentType);
        binder.forField(responseCode)
                .asRequired()
                .bind(Mockup::getResponseCode, Mockup::setResponseCode);

        binder.forField(expirationTime)
                .asRequired()
                .bind(Mockup::getExpirationTimeInHours, Mockup::setExpirationTimeInHours);

        binder.forField(responseBody)
                .asRequired(getTranslation("mockup.form.response.body.required"))
                .withValidator(body -> !body.isBlank(), getTranslation("mockup.form.response.body.required"))
                .bind(Mockup::getBody, Mockup::setBody);
    }

    private void loadExistingMockup() {
        // Load form data
        binder.readBean(mockup);

        // Load existing headers
        headersContainer.removeAll();
        mockup.getHeaders().forEach(header -> {
            FlexLayout headerLayout = createHeaderFields();
            TextField headerName = (TextField) headerLayout.getComponentAt(0);
            TextField headerValue = (TextField) headerLayout.getComponentAt(1);
            headerName.setValue(header.getKey());
            headerValue.setValue(header.getValue());
            headersContainer.add(headerLayout);
        });

        // Set other fields not in binder
        responseTime.setValue(mockup.getResponseTimeInSecs());
        if (isEditMode) {
            expirationTime.setValue(mockup.getExpirationTimeInHours());
            expirationTime.setEnabled(false);
        }
    }

    private void updateUIForMode() {
        if (isEditMode) {
            title.setText(getTranslation("mockup.form.title.edit"));
            saveButton.setText(getTranslation("mockup.form.save.edit"));
        } else {
            title.setText(getTranslation("mockup.form.title.create"));
            saveButton.setText(getTranslation("mockup.form.save.create"));
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        // Update all labels and texts when language changes
        updateUIForMode(); // Update title and save button text based on mode
        name.setLabel(getTranslation("mockup.form.name"));
        description.setLabel(getTranslation("mockup.form.description"));
        path.setLabel(getTranslation("mockup.form.path"));
        path.setPlaceholder(getTranslation("mockup.form.path.placeholder"));
        path.setHelperText(getTranslation("mockup.form.path.helper"));

        accessMethod.setLabel(getTranslation("mockup.form.access.method"));
        contentType.setLabel(getTranslation("mockup.form.content.type"));
        responseCode.setLabel(getTranslation("mockup.form.response.code"));
        responseTime.setLabel(getTranslation("mockup.form.response.time"));
        responseTime.setHelperText(getTranslation("mockup.form.response.time.helper"));
        expirationTime.setLabel(getTranslation("mockup.form.expiration.time"));
        responseBody.setLabel(getTranslation("mockup.form.response.body"));

        addHeaderButton.setText(getTranslation("mockup.form.add.header"));

        // Update expiration time options
        setupExpirationTimes();

        // Re-validate the binder to update error messages
        binder.validate();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var projectIdParam = event.getRouteParameters().get("projectId");
        var mockupIdParam = event.getRouteParameters().get("mockupId");

        if (projectIdParam.isEmpty()) {
            event.rerouteTo(MainLayout.class);
            return;
        }
        Optional<Project> projectOpt = projectService.findByProjectId(Long.parseLong(projectIdParam.get()));
        if (projectOpt.isEmpty()) {
            event.rerouteTo(MainLayout.class);
            return;
        }
        this.project = projectOpt.get();

        if(!userCanEnter(project)){
            event.rerouteTo(MainLayout.class);
            UI.getCurrent().navigate(MainLayout.class);
            return;
        }

        // Determine if we're in edit mode
        if (mockupIdParam.isPresent()) {
            isEditMode = true;
            Optional<Mockup> mockupOpt = mockupService.getMockupById(Long.parseLong(mockupIdParam.get()));
            if (mockupOpt.isEmpty()) {
                event.rerouteTo(MainLayout.class);
                return;
            }

            // Load the existing mockup
            this.mockup = mockupOpt.get();
            loadExistingMockup();
        }
        // Update UI elements based on mode
        updateUIForMode();
    }
}