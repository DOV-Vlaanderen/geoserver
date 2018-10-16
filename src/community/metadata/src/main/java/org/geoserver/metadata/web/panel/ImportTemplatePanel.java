/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataGeonetworkConfiguration;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.web.MetadataTemplateDataProvider;
import org.geoserver.metadata.web.MetadataTemplatePage;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.web.wicket.ParamResourceModel;
import org.geoserver.web.wicket.SimpleAjaxLink;
import org.geotools.util.logging.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class ImportTemplatePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(ImportTemplatePanel.class);

    private List<MetadataGeonetworkConfiguration> geonetworks = new ArrayList<>();

    private GeoServerTablePanel<MetadataTemplate> templatesPanel;

    private ImportTemplateDataProvider linkedTemplatesDataProvider;

    private MetadataTemplate selected;

    public ImportTemplatePanel(String id, IModel<ComplexMetadataMap> metadataModel) {
        super(id, metadataModel);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        linkedTemplatesDataProvider =
                new ImportTemplateDataProvider((IModel<ComplexMetadataMap>) getDefaultModel());

        add(new FeedbackPanel("feedback").setOutputMarkupId(true));

        Form<Object> form = new Form<>("form");

        //link action and dropdown
        DropDownChoice<MetadataTemplate> dropDown = createTemplatesDropDown();
        dropDown.setOutputMarkupId(true);
        form.add(dropDown);
        AjaxSubmitLink importAction = createImportAction(dropDown);
        form.add(importAction);
        //unlink button
        AjaxLink<Object> remove = createUnlinkAction(dropDown);
        remove.setOutputMarkupId(true);
        remove.setEnabled(false);
        form.add(remove);

        //the panel
        templatesPanel = createTemplateTable(remove);
        templatesPanel.setFilterVisible(false);
        templatesPanel.setFilterable(false);
        templatesPanel.getTopPager().setVisible(false);
        templatesPanel.getBottomPager().setVisible(false);
        templatesPanel.setSelectable(false);
        templatesPanel.setSortable(false);
        templatesPanel.setOutputMarkupId(true);

        form.add(templatesPanel);


        add(form);

    }


    public FeedbackPanel getFeedbackPanel() {
        return (FeedbackPanel) get("feedback");
    }


    private DropDownChoice<MetadataTemplate> createTemplatesDropDown() {
        PropertyModel<MetadataTemplate> model = new PropertyModel<>(this, "selected");
        List<MetadataTemplate> unlinked = linkedTemplatesDataProvider.getUnlinkedItems();
        return new DropDownChoice<MetadataTemplate>("metadataTemplate", model, unlinked, new ChoiceRenderer<>("name"));
    }


    private AjaxSubmitLink createImportAction(final DropDownChoice<MetadataTemplate> dropDown) {
        return new AjaxSubmitLink("link") {
            /**
             *
             */
            private static final long serialVersionUID = -8718015688839770852L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                boolean valid = true;
                if (dropDown.getModelObject() == null) {
                    error(new ParamResourceModel("errorSelectTemplate", ImportTemplatePanel.this).getString());
                    valid = false;
                }
                if (valid) {
                    linkTemplate(dropDown.getModelObject());
                }
                target.add(getFeedbackPanel());
                target.add(templatesPanel);
                target.add(dropDown);
                target.add(getParent());
            }

        };
    }


    private AjaxLink<Object> createUnlinkAction(DropDownChoice<MetadataTemplate> dropDown) {
        return new AjaxLink<Object>("removeSelected") {
            private static final long serialVersionUID = 3581476968062788921L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                unlinkTemplate();
                target.add(templatesPanel);
                target.add(dropDown);
            }
        };
    }


    private GeoServerTablePanel<MetadataTemplate> createTemplateTable(AjaxLink<Object> remove) {


        return new GeoServerTablePanel<MetadataTemplate>("templatesPanel",
                linkedTemplatesDataProvider,
                true) {

            private static final long serialVersionUID = -8943273843044917552L;

            @Override
            protected void onSelectionUpdate(AjaxRequestTarget target) {
                remove.setEnabled(templatesPanel.getSelection().size() > 0);
                target.add(remove);

            }

            @SuppressWarnings("unchecked")
            @Override
            protected Component getComponentForProperty(String id, IModel<MetadataTemplate> itemModel,
                                                        GeoServerDataProvider.Property<MetadataTemplate> property) {
                if (property.equals(MetadataTemplateDataProvider.NAME)) {
                    return new SimpleAjaxLink<String>(id, (IModel<String>) property.getModel(itemModel)) {
                        private static final long serialVersionUID = -9184383036056499856L;

                        @Override
                        protected void onClick(AjaxRequestTarget target) {
                            Model<MetadataTemplate> model = new Model<>(itemModel.getObject());
                            setResponsePage(new MetadataTemplatePage(model));
                        }
                    };
                }
                return null;
            }
        };
    }


    /**
     * Link the template and the current metadata
     *
     * @param selected
     */
    private void linkTemplate(MetadataTemplate selected) {
        //add template link to metadata
        linkedTemplatesDataProvider.addLink(selected);
        //todo load the data
        IModel<ComplexMetadataMap> model = (IModel<ComplexMetadataMap>) getDefaultModel();

        ComplexMetadataService service =
                GeoServerApplication.get().getApplicationContext().getBean(ComplexMetadataService.class);

        service.merge(model.getObject(), selected.getMetadata());

    }

    /**
     * Link the template and the selected metadata
     */
    private void unlinkTemplate() {
        //remove link from metadata
        for (MetadataTemplate metadataTemplate : templatesPanel.getSelection()) {
            linkedTemplatesDataProvider.removeLink(metadataTemplate);
        }
        //todo enable the fields by reloading

    }
}
