/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.metadata.web;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.impl.ComplexMetadataMapImpl;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.ComponentAuthorizer;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.GeoServerBasePage;
import org.geotools.util.logging.Logging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetadataTemplatePage extends GeoServerBasePage {

    private static final Logger LOGGER = Logging.getLogger(MetadataTemplatePage.class);

    private static final long serialVersionUID = 2273966783474224452L;

    private final Model<MetadataTemplate> metadataTemplateModel;

    private final boolean isNew;


    public MetadataTemplatePage() {
        this(new Model<MetadataTemplate>(new MetadataTemplate()));
        metadataTemplateModel.getObject().setMetadata(new ComplexMetadataMapImpl(new MetadataMap()));
    }

    public MetadataTemplatePage(Model<MetadataTemplate> metadataTemplateModel) {
        this.metadataTemplateModel = metadataTemplateModel;
        isNew = metadataTemplateModel.getObject().getName() == null;
    }

    public void onInitialize() {
        super.onInitialize();
        IModel<ComplexMetadataMap> metadataModel = new Model<ComplexMetadataMap>(metadataTemplateModel.getObject().getMetadata());

        Form<?> form = new Form<Object>("form");


        AjaxSubmitLink saveButton = createSaveButton();
        saveButton.setOutputMarkupId(true);
        form.add(saveButton);
        form.add(createCancelButton());


        TextField<String> nameField = createNameField(form, saveButton);
        nameField.setEnabled(isNew);
        form.add(nameField);

        TextField<String> desicription = new TextField<String>("description",
                new PropertyModel<String>(metadataTemplateModel, "description"));
        form.add(desicription);

        MetadataPanel metadataTemplatePanel = new MetadataPanel("metadataTemplatePanel", metadataModel);
        form.add(metadataTemplatePanel);

        this.add(form);
        metadataTemplatePanel.setGeonetworkPanelVisible(false);
    }

    protected ComponentAuthorizer getPageAuthorizer() {
        return ComponentAuthorizer.AUTHENTICATED;
    }


    private TextField<String> createNameField(final Form<?> form, final AjaxSubmitLink saveButton) {
        return new TextField<String>("name", new PropertyModel<String>(metadataTemplateModel, "name")) {
            private static final long serialVersionUID = -3736209422699508894L;

            @Override
            public boolean isRequired() {
                return form.findSubmittingButton() == saveButton;
            }
        };
    }

    private AjaxSubmitLink createSaveButton() {
        /*TODO gans het gedoe met updaten van gelinkte metadata*/
        return new AjaxSubmitLink("save") {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                MetadataTemplateService service =
                        GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
                try {
                    if (isNew) {
                        service.save(metadataTemplateModel.getObject());
                    } else {
                        service.update(metadataTemplateModel.getObject());
                    }
                    setResponsePage(new MetadataTemplatesPage());
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                    Throwable rootCause = ExceptionUtils.getRootCause(e);
                    String message = rootCause == null ? e.getLocalizedMessage() :
                            rootCause.getLocalizedMessage();
                    if (message != null) {
                        form.error(message);
                    }
                    target.add(getFeedbackPanel());
                }
            }
        };
    }

    private AjaxLink<Object> createCancelButton() {
        return new AjaxLink<Object>("cancel") {
            private static final long serialVersionUID = -6892944747517089296L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new MetadataTemplatesPage());
            }
        };
    }

}
