package com.nurace11.bookapp.ui.view.config;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;

@Push
@PWA(name = "BookApp", shortName = "BA")
public class AppShellConfiguration implements AppShellConfigurator {
}
