package org.example.practica3.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import org.example.practica3.utils.constants.Role;
import org.example.practica3.views.ProjectManagementView;
import org.example.practica3.views.UserManagementView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.Serializable;

@Tag("drawer")
public class Drawer extends SideNav implements Serializable {
    public Drawer() {
        var homeItem = styledSideNavItem(
                "Projects", ProjectManagementView.class, LineAwesomeIcon.FOLDER_OPEN.create()
        );
        var userManagementItem = styledSideNavItem(
                "User Management", UserManagementView.class, LineAwesomeIcon.USERS_SOLID.create()
        );

        addItem(homeItem);

        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get().getAuthority();
        if(role.equals("ROLE_" + Role.ADMIN)) {
            addItem(userManagementItem);
        }
    }

    private SideNavItem styledSideNavItem(String label, Class<? extends Component> target, SvgIcon icon) {
        var item = new SideNavItem(label, target, icon);
        item.getStyle().setFontWeight("500");
        return item;
    }
}
