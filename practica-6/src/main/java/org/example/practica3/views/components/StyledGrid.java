package org.example.practica3.views.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

import java.io.Serializable;

public class StyledGrid<T> extends Grid<T> implements Serializable {
    public StyledGrid(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
        addThemeVariants(
                GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
        getStyle()
                .set("border-radius", "var(--lumo-border-radius)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");
    }
}
