/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.model.comparator.MetadataTemplateComparator;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.metadata.web.MetadataTemplateDataProvider;
import org.geoserver.metadata.web.MetadataTemplatePage;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.web.wicket.ParamResourceModel;
import org.geoserver.web.wicket.SimpleAjaxLink;
import org.geotools.util.logging.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * The ImportTemplatePanel allows the user to link the metadata to values configured in the metadata template.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public abstract class ImportTemplatePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(ImportTemplatePanel.class);


    private GeoServerTablePanel<MetadataTemplate> templatesPanel;

    private ImportTemplateDataProvider linkedTemplatesDataProvider;

    private MetadataTemplate selected;

    private IModel<List<MetadataTemplate>> templatesModel;

    private final HashMap<String, List<Integer>> descriptionMap;

    private Label noData;

    private AjaxLink<Object> remove;


    public ImportTemplatePanel(String id,
                               String workspace,
                               String layerName,
                               IModel<ComplexMetadataMap> metadataModel,
                               IModel<List<MetadataTemplate>> templatesModel,
                               HashMap<String,List<Integer>> customDescription) {
        super(id, metadataModel);
        this.templatesModel = templatesModel;
        try {
            if (templatesModel.getObject() == null) {
                MetadataTemplateService service =
                        GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
                templatesModel.setObject(service.list());
            }
        } catch (IOException e) {
            error(new ParamResourceModel("errorSelectGeonetwork",
                    ImportTemplatePanel.this).getString());
        }
        this.descriptionMap = customDescription;
        linkedTemplatesDataProvider = new ImportTemplateDataProvider(workspace, layerName, templatesModel);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        add(new FeedbackPanel("feedback").setOutputMarkupId(true));

        Form<Object> form = new Form<>("form");

        //link action and dropdown
        DropDownChoice<MetadataTemplate> dropDown = createTemplatesDropDown();
        dropDown.setOutputMarkupId(true);
        form.add(dropDown);
        AjaxSubmitLink importAction = createImportAction(dropDown);
        form.add(importAction);
        //unlink button
        remove = createUnlinkAction(dropDown);
        remove.setOutputMarkupId(true);
        remove.setEnabled(false);
        form.add(remove);

        //the panel
        templatesPanel = createTemplateTable(remove);
        templatesPanel.setFilterVisible(false);
        templatesPanel.setFilterable(false);
        templatesPanel.getTopPager().setVisible(false);
        templatesPanel.getBottomPager().setVisible(false);
        templatesPanel.setSelectable(true);
        templatesPanel.setSortable(false);
        templatesPanel.setOutputMarkupId(true);

        form.add(templatesPanel);

        // the no data links label
        noData = new Label("noData", new ResourceModel("noData"));
        form.add(noData);
        updateTable(linkedTemplatesDataProvider);

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
                    try {
                        linkTemplate(dropDown.getModelObject());
                        dropDown.setChoices(linkedTemplatesDataProvider.getUnlinkedItems());
                    } catch (IOException e) {
                        error(new ParamResourceModel("errorSelectGeonetwork",
                                ImportTemplatePanel.this).getString());
                    }
                }
                updateTable(linkedTemplatesDataProvider);
                target.add(getFeedbackPanel());
                target.add(templatesPanel);
                target.add(dropDown);
                target.add(ImportTemplatePanel.this);
                handleUpdate(target);
            }

        };
    }


    private AjaxLink<Object> createUnlinkAction(DropDownChoice<MetadataTemplate> dropDown) {
        return new AjaxLink<Object>("removeSelected") {
            private static final long serialVersionUID = 3581476968062788921L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    unlinkTemplate();
                    dropDown.setChoices(linkedTemplatesDataProvider.getUnlinkedItems());
                } catch (IOException e) {
                    error(new ParamResourceModel("errorSelectGeonetwork",
                            ImportTemplatePanel.this).getString());
                }
                updateTable(linkedTemplatesDataProvider);
                target.add(getFeedbackPanel());
                target.add(templatesPanel);
                target.add(dropDown);
                target.add(ImportTemplatePanel.this);
                handleUpdate(target);
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
    private void linkTemplate(MetadataTemplate selected) throws IOException {
        //add template link to metadata
        linkedTemplatesDataProvider.addLink(selected);
        //
        updateModel();
    }

    /**
     * Link the template and the selected metadata
     */
    private void unlinkTemplate() throws IOException {
        //remove link from metadata
        for (MetadataTemplate metadataTemplate : templatesPanel.getSelection()) {
            linkedTemplatesDataProvider.removeLink(metadataTemplate);
        }
        templatesPanel.clearSelection();
        updateModel();
    }

    /**
     * Merge the model and the linked templates.
     */
    private void updateModel() {
        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> model = (IModel<ComplexMetadataMap>) getDefaultModel();
        ComplexMetadataService service =
                GeoServerApplication.get().getApplicationContext().getBean(ComplexMetadataService.class);

        ArrayList<ComplexMetadataMap> maps = new ArrayList<>();
        List<MetadataTemplate> templates = linkedTemplatesDataProvider.getItems();
        Collections.sort(templates, new MetadataTemplateComparator());
        for (MetadataTemplate template : templates) {
            maps.add(template.getMetadata());
        }

        service.merge(model.getObject(), maps, descriptionMap);
    }


    protected abstract void handleUpdate(AjaxRequestTarget target);

    /**
     * Store the changes in the links.
     */
    public void save() {
        MetadataTemplateService service =
                GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
            try {
                for (MetadataTemplate template : templatesModel.getObject()) {
                    service.update(template);
                }
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
    }


    private void updateTable(ImportTemplateDataProvider dataProvider) {
        boolean isEmpty = dataProvider.getItems().isEmpty();
        templatesPanel.setVisible(!isEmpty);
        remove.setVisible(!isEmpty);
        noData.setVisible(isEmpty);
    }
}
