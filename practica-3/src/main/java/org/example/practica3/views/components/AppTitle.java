package org.example.practica3.views.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

@Tag("app-title")
public class AppTitle extends Div {
    public AppTitle(String size) {
        var title = new Span("Mockup");
        title.getStyle()
                .set("color", "#419d78")
                .set("font-size", size)
                .set("font-weight", "bold")
                .setMargin("0");
        var title2 = new Span("APP");
        title2.getStyle()
                .set("color", "#2d3047")
                .set("font-size", size)
                .set("font-weight", "bold")
                .setMargin("0");
        add(title, title2);
        getStyle()
                .set("padding", "10px")
                .set("display", "inline-block")
                .set("border-radius", "5px")
                .setBackgroundColor("#e8ebf7");
    }
}
