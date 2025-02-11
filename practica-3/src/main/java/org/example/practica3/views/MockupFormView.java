package org.example.practica3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
public class MockupFormView extends VerticalLayout implements BeforeEnterObserver {
    private Long projectId;

    private final MockupService mockupService;
    private final ProjectService projectService;

    private final FormLayout formLayout = new FormLayout();
    private final VerticalLayout headersContainer = new VerticalLayout();

    private final TextField name = new TextField("Name");
    private final TextArea description = new TextArea("Description");
    private final TextField path = new TextField("Path");
    private final Select<String> accessMethod = new Select<>();
    private final Select<String> contentType = new Select<>();
    private final Select<Integer> responseCode = new Select<>();
    private final IntegerField responseTime = new IntegerField();
    private final Select<Integer> expirationTime = new Select<>();
    private final TextArea responseBody = new TextArea("Response body");
    private final Button saveButton = new Button("Add this mockup");
    private final Button addHeaderButton = new Button("Add header");
    private final HorizontalLayout buttonLayout = new HorizontalLayout(addHeaderButton);
    // Falta JWT

    private final Hr separator = new Hr();
    private final Hr separator2 = new Hr();

    private final Binder<Mockup> binder = new Binder<>(Mockup.class);

    public MockupFormView(MockupService mockupService, ProjectService projectService) {
        this.mockupService = mockupService;
        this.projectService = projectService;

        setupComponents();
        var title = new H3("Create a REST mockup");

        formLayout.add(name, description, path, separator,
                accessMethod, contentType, responseCode,
                responseTime, separator2, buttonLayout,
                headersContainer, responseBody, expirationTime, saveButton);

        createBinder();
        setFieldSizes();
        add(title, formLayout);
    }

    private void setupComponents(){
        separator.getStyle().setMarginTop("10px");
        separator2.getStyle().setMarginTop("10px");

        accessMethod.setLabel("Access method");
        contentType.setLabel("Content type");
        responseCode.setLabel("Response code");
        responseTime.setLabel("Response time (seconds)");
        responseTime.setHelperText("If empty, the response will be immediate");
        expirationTime.setLabel("Expiration time");

        path.setPlaceholder("api/v1/example");
        path.setHelperText("Path must not start with / and must not contain double slashes");

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

        Map<Integer, String> expirationOptions = new LinkedHashMap<>();
        expirationOptions.put(1, "1 hour");
        expirationOptions.put(24, "1 day");
        expirationOptions.put(168, "1 week");
        expirationOptions.put(720, "1 month");
        expirationOptions.put(8760, "1 year");
        expirationTime.setItems(expirationOptions.keySet());
        expirationTime.setItemLabelGenerator(expirationOptions::get);
        expirationTime.setValue(1);

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

    private void addNewFields() {
        FlexLayout fieldGroup = new FlexLayout();
        fieldGroup.setJustifyContentMode(JustifyContentMode.CENTER);
        fieldGroup.setAlignItems(Alignment.CENTER);
        fieldGroup.setWidthFull();
        fieldGroup.setFlexWrap(FlexLayout.FlexWrap.WRAP);

        var headerName = new TextField();
        headerName.setPlaceholder("Name");
        headerName.getStyle().setMarginRight("10px");
        headerName.setWidth("calc(40% - 20px)");

        var headerValue = new TextField();
        headerValue.setPlaceholder("Value");
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

    // DATA
    private void saveMockup() {
        List<Header> headers = obtainHeaders();
        Project project = projectService.findByProjectId(projectId).orElse(null);
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
        UI.getCurrent().navigate(ProjectManagementView.class);
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
                .asRequired("Name is required")
                .bind(Mockup::getName, Mockup::setName);
        binder.forField(description).bind(Mockup::getDescription, Mockup::setDescription);
        binder.forField(path)
                .asRequired("Path is required")
                .withValidator(new RegexpValidator("Invalid path", pathRegex))
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
                .asRequired("Response body is required")
                .bind(Mockup::getBody, Mockup::setBody);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var projectIdParam = event.getRouteParameters().get("projectId");
        if (projectIdParam.isEmpty()) {
            event.rerouteTo(MainLayout.class);
            return;
        }

        projectId = Long.parseLong(projectIdParam.get());
    }
}
