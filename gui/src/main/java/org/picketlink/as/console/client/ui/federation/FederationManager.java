package org.picketlink.as.console.client.ui.federation;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.subsys.security.model.SecurityDomain;
import org.picketlink.as.console.client.i18n.PicketLinkUIConstants;
import org.picketlink.as.console.client.shared.subsys.model.FederationStore;
import org.picketlink.as.console.client.shared.subsys.model.FederationWrapper;
import org.picketlink.as.console.client.shared.subsys.model.IdentityProvider;
import org.picketlink.as.console.client.shared.subsys.model.IdentityProviderHandler;
import org.picketlink.as.console.client.shared.subsys.model.IdentityProviderHandlerParameter;
import org.picketlink.as.console.client.shared.subsys.model.Key;
import org.picketlink.as.console.client.shared.subsys.model.KeyStore;
import org.picketlink.as.console.client.shared.subsys.model.SAMLConfiguration;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProvider;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProviderHandler;
import org.picketlink.as.console.client.shared.subsys.model.ServiceProviderHandlerParameter;
import org.picketlink.as.console.client.shared.subsys.model.TrustDomain;
import org.picketlink.as.console.client.ui.federation.idp.AddIdentityProviderEvent;
import org.picketlink.as.console.client.ui.federation.idp.RemoveIdentityProviderEvent;
import org.picketlink.as.console.client.ui.federation.sp.AddServiceProviderEvent;
import org.picketlink.as.console.client.ui.federation.sp.RemoveServiceProviderEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FederationManager {

    private final FederationStore federationStore;
    private final DeploymentManager deploymentManager;
    private final EventBus eventBus;
    private final PicketLinkUIConstants uiConstants;

    private FederationPresenter presenter;

    private Map<String, FederationWrapper> federations = new HashMap<String, FederationWrapper>();

    @Inject
    public FederationManager(FederationStore federationStore, DeploymentManager deploymentManager, EventBus eventBus,
            PicketLinkUIConstants uiConstants) {
        this.federationStore = federationStore;
        this.deploymentManager = deploymentManager;
        this.eventBus = eventBus;
        this.uiConstants = uiConstants;
    }

    public void onCreateKeyStore(KeyStore keyStore, final SimpleCallback<Boolean> simpleCallback) {
        this.federationStore.createKeyStore(presenter.getCurrentFederation(), keyStore,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        if (result.getUnderlying()) {
                            loadAllFederations();
                            Console.info(Console.MESSAGES.added(uiConstants.common_label_key_store()));
                            simpleCallback.onSuccess(true);
                        } else {
                            Console.error(Console.MESSAGES.addingFailed(uiConstants.common_label_key_store()));
                            simpleCallback.onSuccess(false);
                        }
                    }
                });
    }

    public void onUpdateKeyStore(KeyStore updatedEntity, final Map<String, Object> changedValues) {
        if (changedValues.size() > 0) {
            this.federationStore.updateKeyStore(presenter.getCurrentFederation(), updatedEntity, changedValues,
                    new SimpleCallback<ResponseWrapper<Boolean>>() {
                        @Override
                        public void onSuccess(ResponseWrapper<Boolean> response) {
                            if (response.getUnderlying()) {
                                loadAllFederations();
                                Console.info(Console.MESSAGES.saved(uiConstants
                                    .common_label_key_store()));
                            } else {
                                Console.error(Console.MESSAGES.saveFailed(uiConstants
                                    .common_label_key_store()));
                            }
                        }

                    });
        }
    }

    /**
     * <p>
     * Removes the selected keystore instance from the subsystem.
     * </p>
     * 
     * @param keyStore
     */
    public void onRemoveKeyStore(KeyStore keyStore) {
        this.federationStore.deleteKeyStore(presenter.getCurrentFederation(), keyStore, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                loadAllFederations();
                if (success) {
                    Console.info(Console.MESSAGES.deleted(uiConstants.common_label_key_store()));
                } else {
                    Console.error(Console.MESSAGES.deletionFailed(uiConstants
                            .common_label_key_store()));
                }
            }
        });
    }

    public void onCreateTrustDomain(IdentityProvider identityProvider, final TrustDomain trustDomain) {
        this.federationStore.createTrustDomain(this.presenter.getCurrentFederation(), identityProvider, trustDomain,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        if (result.getUnderlying()) {
                            loadAllFederations();
                            Console.info(Console.MESSAGES.added(uiConstants
                                    .common_label_trustDomain() + " ")
                                    + trustDomain.getName());
                        } else
                            Console.error(
                                    Console.MESSAGES.addingFailed(uiConstants
                                            .common_label_trustDomain() + " "), result.getResponse().toString());
                    }
                });
    }

    public void onRemoveTrustDomain(IdentityProvider identityProvider, final TrustDomain trustDomain) {
        this.federationStore.deleteTrustDomain(presenter.getCurrentFederation(), identityProvider, trustDomain,
            new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    loadAllFederations();
                    if (success) {
                        Console.info(Console.MESSAGES.deleted(uiConstants
                            .common_label_trustDomain() + " ")
                            + trustDomain.getName());
                    } else {
                        Console.error(Console.MESSAGES.deletionFailed(uiConstants
                            .common_label_trustDomain() + " ")
                            + trustDomain.getName());
                    }
                }
            });
    }

    /**
     * @param serviceProvider
     * @param changedValues
     */
    public void onUpdateServiceProvider(final ServiceProvider serviceProvider, Map<String, Object> changedValues) {
        if (changedValues.size() > 0) {
            this.federationStore.updateServiceProvider(presenter.getCurrentFederation(), serviceProvider, changedValues,
                    new SimpleCallback<ResponseWrapper<Boolean>>() {
                        @Override
                        public void onSuccess(ResponseWrapper<Boolean> response) {
                            if (response.getUnderlying()) {
                                loadAllFederations();
                                Console.info(Console.MESSAGES.saved(uiConstants
                                        .common_label_serviceProvider() + " " + serviceProvider.getName()));
                            } else {
                                Console.error(
                                        Console.MESSAGES.saveFailed(uiConstants
                                                .common_label_serviceProvider() + " ")
                                                + serviceProvider.getName(), response.getResponse().toString());
                            }
                        }

                    });
        }
    }

    /**
     * @param serviceProvider
     */
    public void onCreateServiceProvider(final ServiceProvider serviceProvider) {
        this.federationStore.createServiceProvider(this.presenter.getCurrentFederation(), serviceProvider,
                new SimpleCallback<ResponseWrapper<Boolean>>() {
                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        if (result.getUnderlying()) {
                            loadAllFederations();
                            Console.info(Console.MESSAGES.added(uiConstants
                                    .common_label_serviceProvider() + " ")
                                    + serviceProvider.getName());
                        } else
                            Console.error(Console.MESSAGES.addingFailed(uiConstants
                                    .common_label_serviceProvider() + " " + serviceProvider.getName()), result.getResponse()
                                    .toString());
                    }
                });
        this.eventBus.fireEvent(new AddServiceProviderEvent(serviceProvider));
    }

    /**
     * @param serviceProvider
     */
    public void onRemoveServiceProvider(final ServiceProvider serviceProvider) {
        this.deploymentManager.undeployDeployment(serviceProvider, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                federationStore.deleteServiceProvider(presenter.getCurrentFederation(), serviceProvider,
                    new SimpleCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean success) {
                            loadAllFederations();
                            if (success) {
                                Console.info(Console.MESSAGES.deleted(uiConstants
                                    .common_label_serviceProvider() + " ")
                                    + serviceProvider.getName());
                            } else {
                                Console.error(Console.MESSAGES.deletionFailed(uiConstants
                                    .common_label_serviceProvider() + " ")
                                    + serviceProvider.getName());
                            }
                        }
                    });
                eventBus.fireEvent(new RemoveServiceProviderEvent(serviceProvider));
            }
        });
    }

    public void onCreateIdentityProvider(final IdentityProvider identityProvider) {
        this.federationStore.createIdentityProvider(this.presenter.getCurrentFederation(), identityProvider,
                new SimpleCallback<ResponseWrapper<Boolean>>() {
                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        if (result.getUnderlying()) {
                            String url = identityProvider.getUrl();

                            if (url.indexOf("://") != -1) {
                                TrustDomain defaultTrustedDomain = presenter.getBeanFactory().trustDomain().as();
                                String host = url.substring(url.indexOf("://") + 3);

                                if (host.indexOf(":") != -1) {
                                    host = host.substring(0, host.indexOf(":"));
                                } else if (host.indexOf("/") != -1) {
                                    host = host.substring(0, host.indexOf("/"));
                                }

                                defaultTrustedDomain.setName(host);

                                onCreateTrustDomain(identityProvider, defaultTrustedDomain);
                            }

                            loadAllFederations();
                            Console.info(Console.MESSAGES.added(uiConstants
                                .common_label_identityProvider() + " ")
                                + identityProvider.getName());
                        } else
                            Console.error(Console.MESSAGES.addingFailed(uiConstants
                                    .common_label_identityProvider() + " " + identityProvider.getName()), result.getResponse()
                                    .toString());
                    }
                });
        this.eventBus.fireEvent(new AddIdentityProviderEvent(identityProvider));
    }

    /**
     * @param identityProvider
     */
    public void onRemoveIdentityProvider(final IdentityProvider identityProvider) {
        if (identityProvider.isExternal()) {
            federationStore.deleteIdentityProvider(presenter.getCurrentFederation(), identityProvider,
                new SimpleCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        loadAllFederations();
                        if (success) {
                            Console.info(Console.MESSAGES.deleted(uiConstants
                                .common_label_identityProvider() + " ")
                                + identityProvider.getName());
                        } else {
                            Console.error(Console.MESSAGES.deletionFailed(uiConstants
                                .common_label_identityProvider() + " ")
                                + identityProvider.getName());
                        }
                    }
                });
            eventBus.fireEvent(new RemoveIdentityProviderEvent(identityProvider));
        } else {
            this.deploymentManager.undeployDeployment(identityProvider, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    federationStore.deleteIdentityProvider(presenter.getCurrentFederation(), identityProvider,
                        new SimpleCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean success) {
                                loadAllFederations();
                                if (success) {
                                    Console.info(Console.MESSAGES.deleted(uiConstants
                                        .common_label_identityProvider() + " ")
                                        + identityProvider.getName());
                                } else {
                                    Console.error(Console.MESSAGES.deletionFailed(uiConstants
                                        .common_label_identityProvider() + " ")
                                        + identityProvider.getName());
                                }
                            }
                        });
                    eventBus.fireEvent(new RemoveIdentityProviderEvent(identityProvider));
                }
            });
        }
    }

    /**
     * <p>
     * Updates an identity provider instance fiven a federation.
     * </p>
     * 
     * @param changedValues
     */
    public void onUpdateIdentityProvider(final IdentityProvider identityProvider, final Map<String, Object> changedValues) {
        if (changedValues.size() > 0) {
            this.federationStore.updateIdentityProvider(presenter.getCurrentFederation(), identityProvider, changedValues,
                    new SimpleCallback<ResponseWrapper<Boolean>>() {
                        @Override
                        public void onSuccess(ResponseWrapper<Boolean> response) {
                            if (response.getUnderlying()) {
                                loadAllFederations();
                                Console.info(Console.MESSAGES.saved(uiConstants
                                        .common_label_identityProvider() + " " + identityProvider.getName()));
                            } else {
                                Console.error(
                                        Console.MESSAGES.saveFailed(uiConstants
                                                .common_label_identityProvider() + " ")
                                                + identityProvider.getName(), response.getResponse().toString());
                            }
                        }

                    });
        }
    }

    public void setPresenter(FederationPresenter federationPresenter) {
        this.presenter = federationPresenter;
    }

    public void loadAllFederations() {
        this.federationStore.loadConfiguration(new SimpleCallback<Map<String, FederationWrapper>>() {
            @Override
            public void onSuccess(Map<String, FederationWrapper> result) {
                if (result.isEmpty()) {
                    return;
                }

                federations = result;
                presenter.loadDeployments();
            }
        });
    }

    public Map<String, FederationWrapper> getFederations() {
        return this.federations;
    }

    public void loadAllSecurityDomains(final FederationPresenter federationPresenter) {
        this.federationStore.loadSecurityDomains(new SimpleCallback<List<SecurityDomain>>() {
            @Override
            public void onSuccess(List<SecurityDomain> result) {
                if (result.isEmpty()) {
                    return;
                }
                federationPresenter.onLoadSecurityDomains(result);
            }
        });
    }

    public void onCreateIdentityProviderHandler(IdentityProvider identityProvider, final IdentityProviderHandler newHandler) {
        this.federationStore.createIdentityProviderHandler(this.presenter.getCurrentFederation(), identityProvider, newHandler,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        loadAllFederations();
                        if (result.getUnderlying()) {
                            Console.info(Console.MESSAGES.added("Handler " + newHandler.getClassName()));
                        } else
                            Console.error(
                                    Console.MESSAGES.addingFailed("Handler " + newHandler.getClassName()), result.getResponse().toString());
                    }
                });
    }

    /**
     * @param identityProvider
     * @param removedTrustedDomain
     */
    public void onRemoveIdentityProviderHandler(IdentityProvider identityProvider, final IdentityProviderHandler removedTrustedDomain) {
        this.federationStore.deleteIdentityProviderHandler(presenter.getCurrentFederation(), identityProvider, removedTrustedDomain,
                new SimpleCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        loadAllFederations();
                        if (success) {
                            Console.info(Console.MESSAGES.deleted("Handler " + removedTrustedDomain.getClassName()));
                        } else {
                            Console.error(Console.MESSAGES.deletionFailed("Handler " + removedTrustedDomain.getClassName()));
                        }
                    }
                });
    }

    public void onCreateIdentityProviderHandlerParameter(IdentityProvider identityProvider,IdentityProviderHandler handler,
            final IdentityProviderHandlerParameter newHandlerParameter) {
        this.federationStore.createIdentityProviderHandlerParameter(this.presenter.getCurrentFederation(), identityProvider, handler, newHandlerParameter,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        loadAllFederations();
                        if (result.getUnderlying()) {
                            Console.info(Console.MESSAGES.added("Handler Parameter " + newHandlerParameter.getName()));
                        } else
                            Console.error(
                                    Console.MESSAGES.addingFailed("Handler Parameter " + newHandlerParameter.getName()), result.getResponse().toString());
                    }
                });
    }

    public void onRemoveIdentityProviderHandlerParameter(IdentityProvider identityProvider,IdentityProviderHandler handler,
            final IdentityProviderHandlerParameter removedHandlerParameter) {
        this.federationStore.deleteIdentityProviderHandlerParameter(presenter.getCurrentFederation(), identityProvider, handler, removedHandlerParameter,
                new SimpleCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        loadAllFederations();
                        if (success) {
                            Console.info(Console.MESSAGES.deleted("Handler Parameter" + removedHandlerParameter.getName()));
                        } else {
                            Console.error(Console.MESSAGES.deletionFailed("Handler Parameter" + removedHandlerParameter.getName()));
                        }
                    }
                });
    }

    public void onCreateServiceProviderHandler(ServiceProvider serviceProvider, final ServiceProviderHandler newTrustedDomain) {
        this.federationStore.createServiceProviderHandler(this.presenter.getCurrentFederation(), serviceProvider, newTrustedDomain,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        loadAllFederations();
                        if (result.getUnderlying()) {
                            Console.info(Console.MESSAGES.added("Handler " + newTrustedDomain.getClassName()));
                        } else
                            Console.error(
                                    Console.MESSAGES.addingFailed("Handler " + newTrustedDomain.getClassName()), result.getResponse().toString());
                    }
                });
    }

    public void onRemoveServiceProviderHandler(ServiceProvider serviceProvider, final ServiceProviderHandler removedTrustedDomain) {
        this.federationStore.deleteServiceProviderHandler(presenter.getCurrentFederation(), serviceProvider, removedTrustedDomain,
                new SimpleCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        loadAllFederations();
                        if (success) {
                            Console.info(Console.MESSAGES.deleted("Handler " + removedTrustedDomain.getClassName()));
                        } else {
                            Console.error(Console.MESSAGES.deletionFailed("Handler " + removedTrustedDomain.getClassName()));
                        }
                    }
                });
    }

    public void onCreateServiceProviderHandlerParameter(ServiceProvider serviceProvider,
            ServiceProviderHandler selectedHandler, final ServiceProviderHandlerParameter newHandlerParameter) {
        this.federationStore.createServiceProviderHandlerParameter(this.presenter.getCurrentFederation(), serviceProvider, selectedHandler, newHandlerParameter,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        loadAllFederations();
                        if (result.getUnderlying()) {
                            Console.info(Console.MESSAGES.added("Handler Parameter " + newHandlerParameter.getName()));
                        } else
                            Console.error(
                                    Console.MESSAGES.addingFailed("Handler Parameter " + newHandlerParameter.getName()), result.getResponse().toString());
                    }
                });
    }

    public void onRemoveServiceProviderHandlerParameter(ServiceProvider serviceProvider,
            ServiceProviderHandler selectedHandler, final ServiceProviderHandlerParameter removedHandlerParameter) {
        this.federationStore.deleteServiceProviderHandlerParameter(presenter.getCurrentFederation(), serviceProvider, selectedHandler, removedHandlerParameter,
                new SimpleCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        loadAllFederations();
                        if (success) {
                            Console.info(Console.MESSAGES.deleted("Handler Parameter" + removedHandlerParameter.getName()));
                        } else {
                            Console.error(Console.MESSAGES.deletionFailed("Handler Parameter" + removedHandlerParameter.getName()));
                        }
                    }
                });        
    }

    public void onCreateSAMLConfiguration(SAMLConfiguration updatedEntity) {
        this.federationStore.createSAMLConfiguration(presenter.getCurrentFederation(), updatedEntity,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        if (result.getUnderlying()) {
                            Console.info(Console.MESSAGES.added("SAML Configuration"));
                        } else
                            Console.error(Console.MESSAGES.addingFailed("SAML Configuration"));
                    }
                });
    }

    public void onRemoveKeyStore(SAMLConfiguration samlConfig) {
        this.federationStore.deleteSAMLConfiguration(presenter.getCurrentFederation(), samlConfig,
                new SimpleCallback<ResponseWrapper<Boolean>>() {

                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> result) {
                        if (result.getUnderlying()) {
                            Console.info(Console.MESSAGES.deleted("SAML Configuration"));
                        } else
                            Console.error(Console.MESSAGES.deletionFailed("SAML Configuration"));
                    }
                });
    }

    public void onUpdateSAMLConfiguration(SAMLConfiguration updatedEntity, final Map<String, Object> changedValues) {
        if (changedValues.size() > 0) {
            this.federationStore.updateSAMLConfiguration(presenter.getCurrentFederation(), updatedEntity, changedValues,
                new SimpleCallback<ResponseWrapper<Boolean>>() {
                    @Override
                    public void onSuccess(ResponseWrapper<Boolean> response) {
                        if (response.getUnderlying())
                            Console.info(Console.MESSAGES.saved(uiConstants
                                .common_label_key_store()));
                        else
                            Console.error(Console.MESSAGES.saveFailed(uiConstants
                                .common_label_key_store()));
                    }

                });
        }
    }

    public void onCreateKey(FederationWrapper federation, final Key newKey) {
        this.federationStore.createKey(this.presenter.getCurrentFederation(), newKey,
            new SimpleCallback<ResponseWrapper<Boolean>>() {

                @Override
                public void onSuccess(ResponseWrapper<Boolean> result) {
                    if (result.getUnderlying()) {
                        loadAllFederations();
                        Console.info(Console.MESSAGES.added(uiConstants
                            .common_label_key() + " ")
                            + newKey.getName());
                    } else
                        Console.error(
                            Console.MESSAGES.addingFailed(uiConstants
                                .common_label_key() + " "), result.getResponse().toString());
                }
            });
    }

    public void onRemoveKey(FederationWrapper federation, final Key removedKey) {
        this.federationStore.deleteKey(presenter.getCurrentFederation(), removedKey,
            new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    loadAllFederations();
                    if (success) {
                        Console.info(Console.MESSAGES.deleted(uiConstants
                            .common_label_key() + " ")
                            + removedKey.getName());
                    } else {
                        Console.error(Console.MESSAGES.deletionFailed(uiConstants
                            .common_label_key() + " ")
                            + removedKey.getName());
                    }
                }
            });
    }
}
