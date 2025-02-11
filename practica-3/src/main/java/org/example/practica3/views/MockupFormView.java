package org.example.practica3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

import java.util.*;
import java.util.stream.Collectors;

@PermitAll
@Route(value = "project-management/:projectId/create-mockup", layout = MainLayout.class)
@PageTitle("Create a Mockup | MockupAPP")
public class MockupFormView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {
    private Project project;

    private final MockupService mockupService;
    private final ProjectService projectService;

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

        // Inicializar componentes
        initializeComponents();
        buttonLayout = new HorizontalLayout(addHeaderButton);

        setupComponents();
        createBinder();
        setFieldSizes();

        // Selector de idioma
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
        title = new H3(getTranslation("mockup.form.title"));
        name = new TextField(getTranslation("mockup.form.name"));
        description = new TextArea(getTranslation("mockup.form.description"));
        path = new TextField(getTranslation("mockup.form.path"));
        accessMethod = new Select<>();
        contentType = new Select<>();
        responseCode = new Select<>();
        responseTime = new IntegerField();
        expirationTime = new Select<>();
        responseBody = new TextArea(getTranslation("mockup.form.response.body"));
        saveButton = new Button(getTranslation("mockup.form.save"));
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
        headersContainer.add(fieldGroup);
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
        Mockup mockup = new Mockup();
        if(!binder.writeBeanIfValid(mockup) || project == null) {
            return;
        }

        mockup.setProject(project);
        mockup.setHeaders(headers);
        mockup.setResponseTimeInSecs(responseTime.isEmpty() ? 0 : responseTime.getValue());

        for(Header header : headers) {
            header.setMockup(mockup);
        }
        mockupService.save(mockup);

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("success", Collections.singletonList(""));
        UI.getCurrent().navigate(ProjectManagementView.class, new QueryParameters(queryParams));
    }

    private List<Header> obtainHeaders() {
        List<Header> headers = new ArrayList<>();
        headersContainer.getChildren()
                .map(FlexLayout.class::cast)
                .map(headerGroup -> {
                    var headerName = (TextField) headerGroup.getChildren().toArray()[0];
                    var headerValue = (TextField) headerGroup.getChildren().toArray()[1];
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

    @Override
    public void localeChange(LocaleChangeEvent event) {
        // Actualizar todas las etiquetas y textos cuando cambie el idioma
        title.setText(getTranslation("mockup.form.title"));
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

        saveButton.setText(getTranslation("mockup.form.save"));
        addHeaderButton.setText(getTranslation("mockup.form.add.header"));

        // Actualizar las opciones de tiempo de expiración
        setupExpirationTimes();

        // Re-validar el binder para actualizar los mensajes de error
        binder.validate();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var projectIdParam = event.getRouteParameters().get("projectId");

        if (projectIdParam.isEmpty())  {
            UI.getCurrent().navigate(MainLayout.class);
            event.rerouteTo(MainLayout.class);
            return;
        }
        Optional<Project> project = projectService.findByProjectId(Long.parseLong(projectIdParam.get()));
        if(project.isEmpty()) {
            UI.getCurrent().navigate(MainLayout.class);
            event.rerouteTo(MainLayout.class);
            return;
        }

        this.project = project.get();
    }
}