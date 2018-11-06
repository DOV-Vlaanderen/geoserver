/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.ComplexAttributeGenerator;
import org.geoserver.metadata.web.layer.MetadataTabPanel;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.web.wicket.ParamResourceModel;
import org.geotools.util.logging.Logging;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Generate the gui as a list of Complex Objects(an object contains multiple simple fields or objects).
 * Add ui components to manage the list.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class RepeatableComplexAttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(RepeatableComplexAttributesTablePanel.class);

    private GeoServerTablePanel<ComplexMetadataMap> tablePanel;

    private Label noData;

    private RepeatableComplexAttributeDataProvider dataProvider;
    
    private HashMap<String, List<Integer>> descriptionMap;

    private ComplexAttributeGenerator generator;
    
    public RepeatableComplexAttributesTablePanel(String id,
                                                 RepeatableComplexAttributeDataProvider dataProvider,
                                                 IModel<ComplexMetadataMap> metadataModel,
                                                 ComplexAttributeGenerator generator,
                                                 HashMap<String, List<Integer>> descriptionMap) {
        super(id, metadataModel);
        
        this.dataProvider = dataProvider;
        this.descriptionMap = descriptionMap;
        this.generator = generator;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();

        tablePanel = createAttributesTablePanel(dataProvider, descriptionMap);
        tablePanel.setFilterVisible(false);
        tablePanel.setFilterable(false);
        tablePanel.getTopPager().setVisible(false);
        tablePanel.getBottomPager().setVisible(false);
        tablePanel.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        tablePanel.setSelectable(true);
        tablePanel.setSortable(false);
        tablePanel.setOutputMarkupId(true);
        tablePanel.setOutputMarkupPlaceholderTag(true);
        add(tablePanel);

        // the no data links label
        noData = new Label("noData", new ResourceModel("noData"));
        add(noData);
        noData.setOutputMarkupId(true);
        noData.setOutputMarkupPlaceholderTag(true);
        updateTable();

        add(new AjaxSubmitLink("addNew") {

            private static final long serialVersionUID = 6840006565079316081L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dataProvider.addField();
                updateTable();
                target.add(tablePanel);
                target.add(noData);
            }
        });
        
        GeoServerDialog dialog =  new GeoServerDialog("dialog");
        dialog.setInitialHeight(100);
        add(dialog);
        
        add(new AjaxSubmitLink("generate") {

            private static final long serialVersionUID = 6840006565079316081L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.showOkCancel(target, 
                        new GeoServerDialog.DialogDelegate() {
                            private static final long serialVersionUID = -8716380894588651422L;

                            @Override
                            protected Component getContents(String id) {
                                return new Label(id, new ParamResourceModel(
                                        "confirmGenerate", 
                                        RepeatableComplexAttributesTablePanel.this));
                            }

                            @Override
                            protected boolean onSubmit(AjaxRequestTarget target,
                                    Component contents) {
                                generator.generate(getMetadataModel().getObject(),
                                        (LayerInfo) findParent(MetadataTabPanel.class).getDefaultModelObject());
                                dataProvider.reset();
                                target.add(RepeatableComplexAttributesTablePanel.this.get("attributesTablePanel")
                                        .replaceWith(createAttributesTablePanel(dataProvider, descriptionMap)));
                                updateTable();
                                target.add(noData);
                                return true;
                            }
                            
                        });  
            }
        }.setVisible(generator != null && generator.supports(getMetadataModel().getObject(),
               (LayerInfo) findParent(MetadataTabPanel.class).getDefaultModelObject())));
    }

    private GeoServerTablePanel<ComplexMetadataMap> createAttributesTablePanel(
            RepeatableComplexAttributeDataProvider dataProvider,
            HashMap<String, List<Integer>> descriptionMap) {

        return new GeoServerTablePanel<ComplexMetadataMap>("attributesTablePanel", dataProvider) {
            private static final long serialVersionUID = 4333335931795175790L;

            private int count = 0;

            private IModel<ComplexMetadataMap> disabledValue = null;

            @Override
            protected Component getComponentForProperty(String id,
                                                        IModel<ComplexMetadataMap> itemModel,
                                                        GeoServerDataProvider.Property<ComplexMetadataMap> property) {

                MetadataAttributeConfiguration attributeConfiguration = dataProvider.getConfiguration();

                //disable input values from template
                boolean enableInput = true;

                if (descriptionMap != null && descriptionMap.containsKey(attributeConfiguration.getKey())) {
                    List<Integer> indexes= descriptionMap.get(attributeConfiguration.getKey());
                    for (Integer index : indexes) {
                        if(index.equals(count)){
                            enableInput = false;
                            disabledValue = itemModel;
                            break;
                        }
                    }
                }

                if (property.getName().equals(RepeatableComplexAttributeDataProvider.KEY_VALUE)) {

                    Component component = new AttributesTablePanel(id,
                            new AttributeDataProvider(attributeConfiguration.getTypename()), itemModel, null);
                    component.setEnabled(enableInput);

                    return component;

                } else if (property.getName().equals(RepeatableComplexAttributeDataProvider.KEY_REMOVE_ROW)) {
                    count++;
                    if (itemModel.equals(disabledValue)) {
                        //If the object is for a row that is not editable don't show the remove button
                        disabledValue = null;
                        return new Label(id, "");
                    } else {
                        AjaxSubmitLink deleteAction = new AjaxSubmitLink(id) {

                            private static final long serialVersionUID = -8829474855848647384L;

                            @Override
                            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                                removeFields(target, itemModel);
                                updateTable();
                                target.add(tablePanel);
                                target.add(noData);
                            }
                        };
                        deleteAction.add(new AttributeAppender("class", "remove-link"));
                        return deleteAction;
                    }

                }
                return null;
            }

            private void removeFields(AjaxRequestTarget target, IModel<ComplexMetadataMap> itemModel) {
                ComplexMetadataMap object = itemModel.getObject();
                dataProvider.removeField(object);
                target.add(this);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public IModel<ComplexMetadataMap> getMetadataModel() {
        return (IModel<ComplexMetadataMap>) getDefaultModel();
    }


    private void updateTable() {
        boolean isEmpty = dataProvider.getItems().isEmpty();
        tablePanel.setVisible(!isEmpty);
        noData.setVisible(isEmpty);
    }
}
