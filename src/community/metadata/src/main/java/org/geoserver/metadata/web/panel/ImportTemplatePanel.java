/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.ComplexMetadataService;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.web.wicket.ParamResourceModel;
import org.geotools.util.logging.Logging;

/**
 * The ImportTemplatePanel allows the user to link the metadata to values configured in the metadata
 * template.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public abstract class ImportTemplatePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(ImportTemplatePanel.class);

    private GeoServerTablePanel<MetadataTemplate> templatesPanel;

    private ImportTemplateDataProvider linkedTemplatesDataProvider;

    private IModel<List<MetadataTemplate>> templatesModel;

    private final HashMap<String, List<Integer>> derivedAtts;

    private Label noData;

    private AjaxLink<Object> remove;

    public ImportTemplatePanel(
            String id,
            String layerId,
            IModel<ComplexMetadataMap> metadataModel,
            IModel<List<MetadataTemplate>> templatesModel,
            HashMap<String, List<Integer>> derivedAtts) {
        super(id, metadataModel);
        this.templatesModel = templatesModel;
        this.derivedAtts = derivedAtts;
        linkedTemplatesDataProvider =
                new ImportTemplateDataProvider(layerId, templatesModel);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        GeoServerDialog dialog = new GeoServerDialog("importDialog");
        dialog.setInitialHeight(100);
        add(dialog);

        // link action and dropdown
        DropDownChoice<MetadataTemplate> dropDown = createTemplatesDropDown();
        dropDown.setOutputMarkupId(true);
        add(dropDown);
        AjaxSubmitLink importAction = createImportAction(dropDown, dialog);
        add(importAction);
        // unlink button
        remove = createUnlinkAction();
        remove.setOutputMarkupId(true);
        remove.setEnabled(false);
        add(remove);
        add(
                new FeedbackPanel("linkTemplateFeedback", new ContainerFeedbackMessageFilter(this))
                        .setOutputMarkupId(true));

        // the panel
        templatesPanel = createTemplateTable(remove);
        templatesPanel.setFilterVisible(false);
        templatesPanel.setFilterable(false);
        templatesPanel.getTopPager().setVisible(false);
        templatesPanel.getBottomPager().setVisible(false);
        templatesPanel.setSelectable(true);
        templatesPanel.setSortable(false);
        templatesPanel.setOutputMarkupId(true);
        templatesPanel.setItemReuseStrategy(DefaultItemReuseStrategy.getInstance());

        add(templatesPanel);

        // the no data links label
        noData = new Label("noData", new ResourceModel("noData"));
        add(noData);
        updateTableState(linkedTemplatesDataProvider);
    }

    public FeedbackPanel getFeedbackPanel() {
        return (FeedbackPanel) get("linkTemplateFeedback");
    }

    private DropDownChoice<MetadataTemplate> createTemplatesDropDown() {
        IModel<MetadataTemplate> model = new Model<MetadataTemplate>();
        List<MetadataTemplate> unlinked = linkedTemplatesDataProvider.getUnlinkedItems();
        DropDownChoice<MetadataTemplate> dropDownChoice =
                new DropDownChoice<>(
                        "metadataTemplate", model, unlinked, new ChoiceRenderer<>("name"));
        dropDownChoice.add(
                new OnChangeAjaxBehavior() {

                    private static final long serialVersionUID = 2208064045807777479L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget art) {
                        // just model update
                    }
                });
        return dropDownChoice;
    }

    @SuppressWarnings("unchecked")
    protected DropDownChoice<MetadataTemplate> getDropDown() {
        return (DropDownChoice<MetadataTemplate>) get("metadataTemplate");
    }

    private AjaxSubmitLink createImportAction(
            final DropDownChoice<MetadataTemplate> dropDown, GeoServerDialog dialog) {
        return new AjaxSubmitLink("link") {
            private static final long serialVersionUID = -8718015688839770852L;

            @Override
            public boolean getDefaultFormProcessing() {
                return false;
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                boolean valid = true;
                if (dropDown.getModelObject() == null) {
                    error(
                            new ParamResourceModel("errorSelectTemplate", ImportTemplatePanel.this)
                                    .getString());
                    valid = false;
                }
                if (valid) {
                    dialog.setTitle(
                                new ParamResourceModel(
                                        "confirmImportDialog.title", ImportTemplatePanel.this));
                        dialog.showOkCancel(
                                target,
                                new GeoServerDialog.DialogDelegate() {

                                    private static final long serialVersionUID =
                                            -5552087037163833563L;

                                    @Override
                                    protected Component getContents(String id) {
                                        ParamResourceModel resource =
                                                new ParamResourceModel(
                                                        "confirmImportDialog.content",
                                                        ImportTemplatePanel.this);
                                        return new MultiLineLabel(id, resource.getString());
                                    }

                                    @Override
                                    protected boolean onSubmit(
                                            AjaxRequestTarget target, Component contents) {
                                        performLink(target);
                                        return true;
                                    }
                                });
                }
                target.add(getFeedbackPanel());
            }

            private void performLink(AjaxRequestTarget target) {
                try {
                    linkTemplate(dropDown.getModelObject());
                    dropDown.setModelObject(null);
                    dropDown.setChoices(linkedTemplatesDataProvider.getUnlinkedItems());
                } catch (IOException e) {
                    error(
                            new ParamResourceModel(
                                            "errorSelectGeonetwork", ImportTemplatePanel.this)
                                    .getString());
                }
                updateTableState(linkedTemplatesDataProvider);
                target.add(templatesPanel);
                target.add(dropDown);
                handleUpdate(target);
            }
        };
    }

    private AjaxLink<Object> createUnlinkAction() {
        return new AjaxLink<Object>("removeSelected") {
            private static final long serialVersionUID = 3581476968062788921L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    unlinkTemplate(target, templatesPanel.getSelection());
                } catch (IOException e) {
                    error(
                            new ParamResourceModel(
                                            "errorSelectGeonetwork", ImportTemplatePanel.this)
                                    .getString());
                }
            }
        };
    }

    private GeoServerTablePanel<MetadataTemplate> createTemplateTable(AjaxLink<Object> remove) {

        return new GeoServerTablePanel<MetadataTemplate>(
                "templatesPanel", linkedTemplatesDataProvider, true) {

            private static final long serialVersionUID = -8943273843044917552L;

            @Override
            protected void onSelectionUpdate(AjaxRequestTarget target) {
                for (MetadataTemplate template : templatesPanel.getSelection()) {
                    System.out.println(template.getName());
                }
                remove.setEnabled(templatesPanel.getSelection().size() > 0);
                target.add(remove);
            }

            @Override
            protected Component getComponentForProperty(
                    String id,
                    IModel<MetadataTemplate> itemModel,
                    GeoServerDataProvider.Property<MetadataTemplate> property) {

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
        // add template link to metadata
        linkedTemplatesDataProvider.addLink(selected);
        updateModel();
    }

    /** Link the template and the selected metadata */
    public void unlinkTemplate(AjaxRequestTarget target, List<MetadataTemplate> templates)
            throws IOException {

        linkedTemplatesDataProvider.removeLinks(templates);
        updateModel();

        templatesPanel.clearSelection();

        getDropDown().setChoices(linkedTemplatesDataProvider.getUnlinkedItems());
        updateTableState(linkedTemplatesDataProvider);

        target.add(getFeedbackPanel());
        target.add(templatesPanel);
        target.add(getDropDown());
        target.add(ImportTemplatePanel.this);
        handleUpdate(target);
    }

    public List<MetadataTemplate> getLinkedTemplates() {
        return linkedTemplatesDataProvider.getItems();
    }

    /** Merge the model and the linked templates. */
    private void updateModel() {
        @SuppressWarnings("unchecked")
        IModel<ComplexMetadataMap> model = (IModel<ComplexMetadataMap>) getDefaultModel();
        ComplexMetadataService service =
                GeoServerApplication.get()
                        .getApplicationContext()
                        .getBean(ComplexMetadataService.class);

        ArrayList<ComplexMetadataMap> maps = new ArrayList<>();
        List<MetadataTemplate> templates = linkedTemplatesDataProvider.getItems();
        for (MetadataTemplate template : templates) {
            maps.add(template.getMetadata());
        }

        service.merge(model.getObject(), maps, derivedAtts);
    }

    protected abstract void handleUpdate(AjaxRequestTarget target);

    /** Store the changes in the links. */
    public void save() {
        MetadataTemplateService service =
                GeoServerApplication.get()
                        .getApplicationContext()
                        .getBean(MetadataTemplateService.class);
        try {
            for (MetadataTemplate template : templatesModel.getObject()) {
                service.update(template);
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private void updateTableState(ImportTemplateDataProvider dataProvider) {
        boolean isEmpty = dataProvider.getItems().isEmpty();
        templatesPanel.setVisible(!isEmpty);
        remove.setVisible(!isEmpty);
        noData.setVisible(isEmpty);
    }

}
