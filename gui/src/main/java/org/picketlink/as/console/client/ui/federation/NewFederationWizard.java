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

package org.picketlink.as.console.client.ui.federation;

import org.picketlink.as.console.client.shared.subsys.model.Federation;

import com.google.gwt.user.client.ui.DeckPanel;

/**
 * <p>
 * A wizard to be used when creating a new federation configuration.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 19, 2012
 */
public class NewFederationWizard extends AbstractWizardView<FederationPresenter, Federation> {

    public NewFederationWizard(FederationPresenter presenter) {
        super("Federation", presenter);
    }

    /* (non-Javadoc)
     * @see org.picketlink.as.console.client.ui.federation.AbstractWizardView#doAddSteps(com.google.gwt.user.client.ui.DeckPanel)
     */
    @Override
    protected void doAddSteps(DeckPanel deck) {
        deck.add(new NewFederationWizardStep1(this).asWidget());
    }

    /**
     * <p>
     * Callback method called when the user wants to save a new federation instance.
     * </p>
     * 
     * @param newFederation
     */
    public void onSave(Federation newFederation) {
        this.getPresenter().onCreateFederation(newFederation);
    }
}
