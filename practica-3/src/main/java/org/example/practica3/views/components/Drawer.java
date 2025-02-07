package org.example.practica3.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import org.example.practica3.views.UserManagementView;
import org.vaadin.lineawesome.LineAwesomeIcon;

@Tag("drawer")
public class Drawer extends SideNav {
    public Drawer() {
        var homeItem = styledSideNavItem(
                "Project Management", ProjectManagementView.class, LineAwesomeIcon.FOLDER_OPEN.create()
        );
        var userManagementItem = styledSideNavItem(
                "User Management", UserManagementView.class, LineAwesomeIcon.USERS_SOLID.create()
        );
        addItem(homeItem, userManagementItem);
    }

    private SideNavItem styledSideNavItem(String label, Class<? extends Component> target, SvgIcon icon) {
        var item = new SideNavItem(label, target, icon);
        item.getStyle().setFontWeight("500");
        return item;
    }
}
