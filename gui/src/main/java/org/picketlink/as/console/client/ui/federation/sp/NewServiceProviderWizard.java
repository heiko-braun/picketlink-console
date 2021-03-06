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

import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.picketlink.as.console.client.i18n.PicketLinkUIConstants;
import org.picketlink.as.console.client.shared.subsys.model.GenericFederationEntity;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProvider;
import org.picketlink.as.console.client.ui.federation.AbstractFederationDetailEditor;
import org.picketlink.as.console.client.ui.federation.AbstractFederationWizard;
import org.picketlink.as.console.client.ui.federation.FederationPresenter;
import org.picketlink.as.console.client.ui.federation.Wizard;

import java.util.List;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 30, 2012
 */
public class NewServiceProviderWizard<T extends GenericFederationEntity> extends AbstractFederationWizard<T> implements Wizard<T> {

    private ComboBoxItem nameItem;
    private ComboBoxItem deploymentsItem;
    private ComboBoxItem securityDomainsItem;
    private CheckBoxItem strictPostBinding;
    private PicketLinkUIConstants uiConstants;

    public NewServiceProviderWizard(AbstractFederationDetailEditor<T> editor, Class<T> cls, FederationPresenter presenter, String type,
            PicketLinkUIConstants uiConstants) {
        super(editor, cls, presenter, type, "security-domain", "url", "post-binding", "strict-post-binding", "error-page", "logout-page");
        this.uiConstants = uiConstants;
    }

    @Override
    protected FormItem<?>[] doGetCustomFields() {
        ComboBoxItem nameItem = null;
        
        if (!isDialogue()) {
            this.deploymentsItem = new ComboBoxItem("name", "Name");
            nameItem = this.deploymentsItem;
            updateAliasComboBox(nameItem, this.getPresenter().getAllDeployments());
            nameItem.setEnabled(false);
            nameItem.setRequired(false);
        } else {
            nameItem = getAliasItem();
            nameItem.setRequired(true);
            updateAliasItems();
        }
        
        this.securityDomainsItem =  new ComboBoxItem("securityDomain", "Security Domain");
        
        if (this.getPresenter().getSecurityDomains() != null) {
            String[] securityDomains = new String[this.getPresenter().getSecurityDomains().size()];

            for (int i = 0; i < this.getPresenter().getSecurityDomains().size(); i++) {
                securityDomains[i] = this.getPresenter().getSecurityDomains().get(i).getName();
            }
            
            securityDomainsItem.setValueMap(securityDomains);
        }
        
        strictPostBinding = new CheckBoxItem("strictPostBinding", "Strict Post Binding");
        strictPostBinding.setEnabled(true);
        strictPostBinding.setRequired(false);
        
        TextBoxItem errorPageItem = new TextBoxItem("errorPage", "Error Page");
        errorPageItem.setEnabled(true);
        errorPageItem.setRequired(false);

        TextBoxItem logoutPageItem = new TextBoxItem("logoutPage", "LogOut Page");
        logoutPageItem.setEnabled(true);
        logoutPageItem.setRequired(false);

        FormItem<?>[] formItems = null;

        
        if (!isDialogue()) {
            formItems = new FormItem<?>[] { nameItem, securityDomainsItem,
                    new TextBoxItem("url", uiConstants.common_label_URL(), true),
                    new CheckBoxItem("postBinding", uiConstants.common_label_postBinding()), strictPostBinding, errorPageItem, logoutPageItem};           
        } else {
            formItems = new FormItem<?>[] { nameItem, securityDomainsItem,
                    new TextBoxItem("url", uiConstants.common_label_URL(), false)}; 
        }

        return formItems;
    }

    /**
     * @param result
     */
    public void setServiceProviders(List<ServiceProvider> result) {
        updateAliasItems();
        updateSecurityDomains();
    }

    /**
     * @return
     */
    private ComboBoxItem getAliasItem() {
        if (this.nameItem == null) {
            this.nameItem = new ComboBoxItem("name", "Name");
        }

        return this.nameItem;
    }

    public void updateAliasItems() {
        if (this.deploymentsItem != null) {
            updateAliasComboBox(this.deploymentsItem, this.getPresenter().getAllDeployments());            
        }
        updateAliasComboBox(getAliasItem(), this.getPresenter().getAvailableDeployments());
        updateSecurityDomains();
    }

    private void updateAliasComboBox(ComboBoxItem nameItem, List<DeploymentRecord> deployments) {
        if (getPresenter().getAllDeployments() == null) {
            return;
        }
        
        String[] names = new String[deployments.size()];
        
        for (int i = 0; i < deployments.size(); i++) {
            names[i] = deployments.get(i).getName();
        }

        nameItem.setValueMap(names);
        
        if (!isDialogue()) {
            if (this.getServiceProviderEditor().getCurrentSelection() != null) {
                nameItem.setValue(this.getServiceProviderEditor().getCurrentSelection().getName());
            }
        }
    }

    public ServiceProviderEditor getServiceProviderEditor() {
        return (ServiceProviderEditor) this.getEditor();
    }
    
    private void updateSecurityDomains() {
        if (this.getPresenter().getSecurityDomains() != null && this.securityDomainsItem != null) {
            String[] securityDomains = new String[this.getPresenter().getSecurityDomains().size()];

            for (int i = 0; i < this.getPresenter().getSecurityDomains().size(); i++) {
                securityDomains[i] = this.getPresenter().getSecurityDomains().get(i).getName();
            }
            
            securityDomainsItem.setValueMap(securityDomains);
        }
        
        if (!isDialogue()) {
            if (this.getServiceProviderEditor().getCurrentSelection() != null) {
                securityDomainsItem.setValue(this.getServiceProviderEditor().getCurrentSelection().getSecurityDomain());
            }
        }

    }

}
