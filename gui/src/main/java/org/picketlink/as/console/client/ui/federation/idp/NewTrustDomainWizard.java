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
package org.picketlink.as.console.client.ui.federation.idp;

import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.picketlink.as.console.client.i18n.PicketLinkUIConstants;
import org.picketlink.as.console.client.shared.subsys.model.TrustDomain;
import org.picketlink.as.console.client.ui.federation.AbstractWizard;
import org.picketlink.as.console.client.ui.federation.FederationPresenter;

/**
 * @author Pedro Igor
 */
public class NewTrustDomainWizard extends AbstractWizard<TrustDomain> {

    private final TrustedDomainTabEditor editor;
    private final PicketLinkUIConstants uiConstants;

    public NewTrustDomainWizard(TrustedDomainTabEditor editor, FederationPresenter presenter, PicketLinkUIConstants uiConstants) {
        super(TrustDomain.class, presenter, new String[] {"identity-provider", "trust-domain"}, "name");
        this.editor = editor;
        this.uiConstants = uiConstants;
    }

    @Override
    protected void doSaveWizard(TrustDomain newTrustedDomain) {
        if (newTrustedDomain != null && !newTrustedDomain.getName().trim().isEmpty()) {
            getPresenter().getFederationManager().onCreateTrustDomain(this.editor.getIdentityProvider(), newTrustedDomain);
            this.editor.getTrustDomainTable().getDataProvider().getList().add(newTrustedDomain);
        }
    }

    @Override
    protected FormItem<?>[] doGetCustomFields() {
        TextBoxItem name = new TextBoxItem("name", uiConstants.common_label_domainName());

        name.setRequired(true);

        return new FormItem<?>[] {name};
    }

    @Override
    protected String doGetTitle() {
        return "Add Trust Domain";
    }

    @Override
    public void edit(TrustDomain object) {

    }
}
