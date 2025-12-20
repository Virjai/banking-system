package com.capitalbank.controller.customers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

public class PostLoginController extends SelectorComposer<Window> {
    private static final long serialVersionUID = 1L;

	@Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            Executions.sendRedirect("/login.zul?timeout=true");
            return;
        }
        
        auth.getAuthorities().forEach(a -> {
            try {
                if (a.getAuthority().equals("ROLE_ADMIN")) {
                    Executions.sendRedirect("/zul/admin/dashboard.zul");
                } else {
                    Executions.sendRedirect("/zul/user/dashboard.zul");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

