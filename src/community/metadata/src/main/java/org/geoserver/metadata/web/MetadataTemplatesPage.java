/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.metadata.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.metadata.web.panel.TemplatesPositionPanel;
import org.geoserver.web.ComponentAuthorizer;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.GeoServerSecuredPage;
import org.geoserver.web.wicket.*;
import org.geotools.util.logging.Logging;

/**
 * Manages the metadata templates. Shows all existing templates,allows to create, edit and delete
 * templates.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataTemplatesPage extends GeoServerSecuredPage {

    private static final Logger LOGGER = Logging.getLogger(MetadataTemplatesPage.class);

    private static final long serialVersionUID = 2273966783474224452L;

    private GeoServerDialog dialog;

    GeoServerTablePanel<MetadataTemplate> templatesPanel;

    public MetadataTemplatesPage() {}

    @Override
    public void onInitialize() {
        super.onInitialize();

        add(dialog = new GeoServerDialog("dialog"));
        dialog.setInitialHeight(150);
        ((ModalWindow) dialog.get("dialog")).showUnloadConfirmation(false);

        add(
                new AjaxLink<Object>("addNew") {
                    private static final long serialVersionUID = 3581476968062788921L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        setResponsePage(new MetadataTemplatePage());
                    }
                });

        // the removal button
        AjaxLink<Object> remove =
                new AjaxLink<Object>("removeSelected") {
                    private static final long serialVersionUID = 3581476968062788921L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        dialog.setTitle(
                                new ParamResourceModel(
                                        "deleteDialog.title", MetadataTemplatesPage.this));
                        dialog.showOkCancel(
                                target,
                                new GeoServerDialog.DialogDelegate() {

                                    private static final long serialVersionUID =
                                            -5552087037163833563L;

                                    @Override
                                    protected Component getContents(String id) {
                                        ParamResourceModel resource =
                                                new ParamResourceModel(
                                                        "deleteDialog.content",
                                                        MetadataTemplatesPage.this);
                                        return new Label(id, resource.getString());
                                    }

                                    @Override
                                    protected boolean onSubmit(
                                            AjaxRequestTarget target, Component contents) {
                                        performDelete(target);
                                        return true;
                                    }
                                });
                    }
                };
        remove.setOutputMarkupId(true);
        remove.setEnabled(false);
        add(remove);

        // the panel
        templatesPanel =
                new GeoServerTablePanel<MetadataTemplate>(
                        "templatesPanel", new MetadataTemplateDataProvider(), true) {

                    private static final long serialVersionUID = -8943273843044917552L;

                    @Override
                    protected void onSelectionUpdate(AjaxRequestTarget target) {
                        remove.setEnabled(templatesPanel.getSelection().size() > 0);
                        target.add(remove);
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    protected Component getComponentForProperty(
                            String id,
                            IModel<MetadataTemplate> itemModel,
                            GeoServerDataProvider.Property<MetadataTemplate> property) {
                        if (property.equals(MetadataTemplateDataProvider.NAME)) {
                            return new SimpleAjaxLink<String>(
                                    id, (IModel<String>) property.getModel(itemModel)) {
                                private static final long serialVersionUID = -9184383036056499856L;

                                @Override
                                protected void onClick(AjaxRequestTarget target) {
                                    IModel<MetadataTemplate> model =
                                            new Model<>(itemModel.getObject());
                                    setResponsePage(new MetadataTemplatePage(model));
                                }
                            };
                        } else if (property.equals(MetadataTemplateDataProvider.PRIORITY)) {
                            return new TemplatesPositionPanel(id, itemModel, this);
                        }
                        return null;
                    }
                };
        templatesPanel.setOutputMarkupId(true);
        add(templatesPanel);
    }

    private void performDelete(AjaxRequestTarget target) {
        MetadataTemplateService service =
                GeoServerApplication.get()
                        .getApplicationContext()
                        .getBean(MetadataTemplateService.class);
        for (MetadataTemplate template : templatesPanel.getSelection()) {

            try {
                service.delete(template);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
                if (template.getLinkedLayers() != null && !template.getLinkedLayers().isEmpty()) {
                    StringBuilder layers = generatateLayerNames(template);
                    StringResourceModel msg =
                            new StringResourceModel("errorIsLinked", templatesPanel)
                                    .setParameters(template.getName(), layers);
                    error(msg.getString());
                    ;
                } else {
                    error(e.getMessage());
                }
                addFeedbackPanels(target);
            }
        }
        target.add(templatesPanel);
    }

    private StringBuilder generatateLayerNames(MetadataTemplate template) {
        StringBuilder layers = new StringBuilder();
        for (String resourceId : template.getLinkedLayers()) {
            if (layers.length() > 0) {
                layers.append(",\n");
            }
            Catalog catalog = GeoServerApplication.get().getGeoServer().getCatalog();
            ResourceInfo resource = catalog.getResource(resourceId, ResourceInfo.class);
            if (resource != null) {
                layers.append(resource.getName());
            } else {
                layers.append(resourceId);
            }
        }
        return layers;
    }

    @Override
    protected ComponentAuthorizer getPageAuthorizer() {
        return ComponentAuthorizer.AUTHENTICATED;
    }
}
