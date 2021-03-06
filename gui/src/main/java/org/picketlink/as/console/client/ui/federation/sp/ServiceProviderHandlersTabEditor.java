/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.picketlink.as.console.client.ui.federation.sp;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.picketlink.as.console.client.i18n.PicketLinkUIConstants;
import org.picketlink.as.console.client.i18n.PicketLinkUIMessages;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProviderHandler;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProviderHandlerParameter;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProviderHandlerWrapper;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProviderWrapper;
import org.picketlink.as.console.client.ui.federation.FederationPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 31, 2012
 */
public class ServiceProviderHandlersTabEditor {

    private ServiceProviderHandlerTable handlerTable;
    private ServiceProviderHandlerParameterTable handlerParameterTable;
    private FederationPresenter presenter;
    private ServiceProviderWrapper serviceProvider;
    private ToolButton removeHandlerBtn;
    private ToolButton addHandlerBtn;
    private ToolButton removeHandlerParameterBtn;
    private ToolButton addHandlerParameterBtn;
    private PicketLinkUIConstants uiConstants;
    private PicketLinkUIMessages uiMessages;

    public ServiceProviderHandlersTabEditor(FederationPresenter presenter,
            PicketLinkUIConstants uiConstants, PicketLinkUIMessages uiMessages) {
        this.presenter = presenter;
        this.uiConstants = uiConstants;
        this.uiMessages = uiMessages;
    }
    
    public Widget asWidget() {
        // adds the trust domain section
        VerticalPanel trustDomainsHeader = new VerticalPanel();

        trustDomainsHeader.setStyleName("fill-layout-width");

        trustDomainsHeader.add(new ContentHeaderLabel("Handler"));
        addHandlerActions(trustDomainsHeader);
        addHandlerTable(trustDomainsHeader);
        
        trustDomainsHeader.add(new ContentHeaderLabel("Handler Parameters"));
        addHandlerParameterActions(trustDomainsHeader);
        trustDomainsHeader.add(getHandlerParameterTable().asWidget());

        return trustDomainsHeader;
    }

    private void addHandlerTable(VerticalPanel detailPanel) {
        detailPanel.add(getHandlerTable().asWidget());
    }

    private void addHandlerActions(VerticalPanel trustDomainsHeader) {
        ToolStrip trustDomainTools = new ToolStrip();
        final ServiceProviderHandlersTabEditor editor = this;

        addHandlerBtn = new ToolButton(Console.CONSTANTS.common_label_add());

        addHandlerBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new NewServiceProviderHandlerWizard(editor, presenter).launchWizard();
            }
        });

        trustDomainTools.addToolButtonRight(addHandlerBtn);

        removeHandlerBtn = new ToolButton(Console.CONSTANTS.common_label_delete());

        removeHandlerBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final ServiceProviderHandler removedTrustedDomain = getHandlerTable().getSelectedHandler();
                
                Feedback.confirm(
                        Console.MESSAGES.deleteTitle(uiConstants.common_label_trustDomain()),
                        Console.MESSAGES.deleteConfirm(removedTrustedDomain.getClassName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.getFederationManager().onRemoveServiceProviderHandler(serviceProvider.getServiceProvider(), removedTrustedDomain);
                                    getHandlerTable().getDataProvider().getList().remove(removedTrustedDomain);
                                    getHandlerParameterTable().getDataProvider().getList().clear();
                                }
                            }
                        });
            }
        });

        trustDomainTools.addToolButtonRight(removeHandlerBtn);

        trustDomainTools.setStyleName("fill-layout-width");

        trustDomainsHeader.add(trustDomainTools);

        trustDomainsHeader.add(new ContentDescription(""));
    }

    private void addHandlerParameterActions(VerticalPanel trustDomainsHeader) {
        ToolStrip trustDomainTools = new ToolStrip();
        final ServiceProviderHandlersTabEditor editor = this;
        addHandlerParameterBtn = new ToolButton(Console.CONSTANTS.common_label_add());

        addHandlerParameterBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new NewServiceProviderHandlerParameterWizard(editor, presenter).launchWizard();
            }
        });

        trustDomainTools.addToolButtonRight(addHandlerParameterBtn);

        removeHandlerParameterBtn = new ToolButton(Console.CONSTANTS.common_label_delete());

        removeHandlerParameterBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final ServiceProviderHandlerParameter removedHandlerParameter = getHandlerParameterTable().getSelectedHandlerParameter();
                
                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Handler Parameter"),
                        Console.MESSAGES.deleteConfirm(removedHandlerParameter.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.getFederationManager().onRemoveServiceProviderHandlerParameter(serviceProvider.getServiceProvider(), getHandlerTable().getSelectedHandler(), removedHandlerParameter);
                                    getHandlerParameterTable().getDataProvider().getList().remove(removedHandlerParameter);
                                }
                            }
                        });
            }
        });

        trustDomainTools.addToolButtonRight(removeHandlerParameterBtn);

        trustDomainTools.setStyleName("fill-layout-width");

        trustDomainsHeader.add(trustDomainTools);

        trustDomainsHeader.add(new ContentDescription(""));
    }

    public ServiceProviderHandlerTable getHandlerTable() {
        if (this.handlerTable == null) {
            this.handlerTable = new ServiceProviderHandlerTable();
            this.handlerTable.setParametersTable(this.getHandlerParameterTable());
            this.handlerTable.setPresenter(this.presenter);
            this.handlerTable.setHandlersTabEditor(this);
        }

        return this.handlerTable;
    }

    public ServiceProviderHandlerParameterTable getHandlerParameterTable() {
        if (this.handlerParameterTable == null) {
            this.handlerParameterTable = new ServiceProviderHandlerParameterTable();
        }

        return this.handlerParameterTable;
    }

    private void showRestartDialog() {
        if (Window.confirm("Changes would be applied after a restart. Do you want to do it now ?")) {
            presenter.getDeploymentManager().restartServiceProvider(serviceProvider.getServiceProvider());
        }        
    }
    
    /**
     * @param selectedServiceProvider
     */
    public void setServiceProvider(ServiceProviderWrapper selectedServiceProvider) {
        if (selectedServiceProvider == null) {
            this.addHandlerBtn.setEnabled(false);
            this.removeHandlerBtn.setEnabled(false);
        } else {
            this.addHandlerBtn.setEnabled(true);
            this.removeHandlerBtn.setEnabled(true);
        }
        
        this.serviceProvider = selectedServiceProvider;
        getHandlerTable().setSelectedServiceProvider(this.serviceProvider);
    }

    public void doUpdateSelection(ServiceProviderHandler selectedHandler) {
        List<ServiceProviderHandlerWrapper> handlers = this.serviceProvider.getHandlers();
        ArrayList<ServiceProviderHandlerParameter> parameters = new ArrayList<ServiceProviderHandlerParameter>();
        
        for (ServiceProviderHandlerWrapper handlerWrapper : handlers) {
            if (handlerWrapper.getHandler().getClassName().equals(selectedHandler.getClassName())) {
                for (ServiceProviderHandlerParameter serviceProviderHandlerParameter : handlerWrapper.getParameters()) {
                    parameters.add(serviceProviderHandlerParameter);
                }
            }
        }
        
        getHandlerParameterTable().getDataProvider().setList(parameters);
        enableDisableHandlerParameterActions(true);
    }

    public ServiceProviderWrapper getServiceProvider() {
        return serviceProvider;
    }

    public void enableDisableHandlerParameterActions(boolean enable) {
        this.addHandlerParameterBtn.setEnabled(enable);
        this.removeHandlerParameterBtn.setEnabled(enable);

        if (getHandlerParameterTable().getDataProvider().getList().isEmpty()) {
            this.removeHandlerParameterBtn.setEnabled(false);
        }
    }

}
