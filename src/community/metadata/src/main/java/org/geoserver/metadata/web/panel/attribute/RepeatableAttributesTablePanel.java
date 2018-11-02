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
import org.geoserver.metadata.data.model.ComplexMetadataAttribute;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.logging.Logging;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Generate the gui as a list of simple inputs (text, double, dropdown, ..).
 * Add ui components to manage the list.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class RepeatableAttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(RepeatableAttributesTablePanel.class);

    private GeoServerTablePanel<ComplexMetadataAttribute<String>> tablePanel;

    private Label noData;

    public RepeatableAttributesTablePanel(String id,
                                          RepeatableAttributeDataProvider<String> dataProvider,
                                          IModel<ComplexMetadataMap> metadataModel,
                                          HashMap<String, List<Integer>> descriptionMap) {
        super(id, metadataModel);

        setOutputMarkupId(true);

        tablePanel = createAttributesTablePanel(dataProvider, descriptionMap);
        tablePanel.setFilterVisible(false);
        tablePanel.setFilterable(false);
        tablePanel.getTopPager().setVisible(false);
        tablePanel.getBottomPager().setVisible(false);
        tablePanel.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        tablePanel.setSelectable(true);
        tablePanel.setSortable(false);
        tablePanel.setOutputMarkupId(true);
        add(tablePanel);

        // the no data links label
        noData = new Label("noData", new ResourceModel("noData"));
        add(noData);
        updateTable(dataProvider);

        add(new AjaxSubmitLink("addNew") {

            private static final long serialVersionUID = 6840006565079316081L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dataProvider.addField();
                updateTable(dataProvider);
                target.add(tablePanel);
                target.add(RepeatableAttributesTablePanel.this);
            }
        });
    }

    private GeoServerTablePanel<ComplexMetadataAttribute<String>> createAttributesTablePanel(
            RepeatableAttributeDataProvider<String> dataProvider,
            HashMap<String, List<Integer>> descriptionMap) {

        GeoServerTablePanel<ComplexMetadataAttribute<String>> tablePanel =
                new GeoServerTablePanel<ComplexMetadataAttribute<String>>("attributesTablePanel", dataProvider) {

                    private int count = 0;

                    private IModel<ComplexMetadataAttribute<String>> disabledValue = null;

                    private static final long serialVersionUID = 4333335931795175790L;

                    @Override
                    protected Component getComponentForProperty(String id,
                                                                IModel<ComplexMetadataAttribute<String>> itemModel,
                                                                GeoServerDataProvider.Property<ComplexMetadataAttribute<String>> property) {
                        MetadataAttributeConfiguration attributeConfiguration = dataProvider.getConfiguration();
                        boolean enableInput = true;
                        //disable input values from template
                        if (descriptionMap != null &&
                                descriptionMap.containsKey(attributeConfiguration.getKey())) {
                            List<Integer> indexes= descriptionMap.get(attributeConfiguration.getKey());
                            for (Integer index : indexes) {
                                if(index.equals(count)){
                                    enableInput = false;
                                    disabledValue= itemModel;
                                    break;
                                }
                            }
                        }
                        if (property.getName().equals(RepeatableAttributeDataProvider.KEY_VALUE)) {

                            Component component = EditorFactory.getInstance().create(
                                            attributeConfiguration, id, itemModel.getObject());

                            if (component != null) {
                                component.setEnabled(enableInput);
                            }
                            return component;

                        } else if (property.getName().equals(RepeatableAttributeDataProvider.KEY_REMOVE_ROW)) {
                            //last column updates the row counter.
                            // TODO Is there a counter in the dataprovider or table?
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
                                    }
                                };
                                deleteAction.add(new AttributeAppender("class", "remove-link"));
                                return deleteAction;
                            }

                        }
                        return null;
                    }

                    private void removeFields(AjaxRequestTarget target, IModel<ComplexMetadataAttribute<String>> itemModel) {
                        ComplexMetadataAttribute<String> object = itemModel.getObject();
                        dataProvider.removeField(object);
                        updateTable(dataProvider);
                        target.add(this);
                        target.add(RepeatableAttributesTablePanel.this);
                    }
                };
        return tablePanel;
    }

    private void updateTable(RepeatableAttributeDataProvider<String> dataProvider) {
        boolean isEmpty = dataProvider.getItems().isEmpty();
        tablePanel.setVisible(!isEmpty);
        noData.setVisible(isEmpty);
    }


}
