/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.AbstractPropertyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.model.AttributeInput;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.logging.Logging;

import java.util.logging.Logger;

public class RepeatableAttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(RepeatableAttributesTablePanel.class);


    private final IModel<MetadataMap> metadataModel;


    public RepeatableAttributesTablePanel(String id, String fieldprefix, GeoServerDataProvider dataProvider, IModel<MetadataMap> metadataModel) {
        super(id, metadataModel);

        GeoServerTablePanel<AttributeInput> tablePanel = createAttributesTablePanel(fieldprefix, dataProvider);
        tablePanel.setFilterVisible(false);
        tablePanel.setFilterable(false);
        tablePanel.getTopPager().setVisible(false);
        tablePanel.getBottomPager().setVisible(false);
        tablePanel.setOutputMarkupId(false);
        tablePanel.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        tablePanel.setSelectable(true);
        tablePanel.setSortable(false);
        tablePanel.setOutputMarkupId(true);
        add(tablePanel);

        this.metadataModel = metadataModel;


        add(new AjaxLink<Object>("removeSelected") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ((RepeatableAttributeDataProvider) dataProvider).removeFields(tablePanel.getSelection());
                tablePanel.clearSelection();
                target.add(tablePanel);
            }
        });

        add(new AjaxLink<Object>("addNew") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ((RepeatableAttributeDataProvider) dataProvider).addField();

                target.add(tablePanel);
            }
        });
    }

    private GeoServerTablePanel<AttributeInput> createAttributesTablePanel(String fieldprefix, GeoServerDataProvider dataProvider) {
        return new GeoServerTablePanel<AttributeInput>("attributesTablePanel", dataProvider) {
            @Override
            protected Component getComponentForProperty(String id, IModel<AttributeInput> itemModel, GeoServerDataProvider.Property<AttributeInput> property) {
                if (property.equals(RepeatableAttributeDataProvider.VALUE)) {
                    MetadataAttributeConfiguration attributeConfiguration = itemModel.getObject().getAttributeConfiguration();
                    if (OccurenceEnum.SINGLE.equals(attributeConfiguration.getOccurrence())) {
                        switch (attributeConfiguration.getFieldType()) {
                            case TEXT:
                                return new TextFieldPanel(id,
                                        createModel(itemModel.getObject()));
                            case NUMBER:
                                return new TextFieldPanel(id,
                                        createModel(itemModel.getObject()));
                            case DROPDOWN:
                                final DropDownPanel ddp =
                                        new DropDownPanel(id,
                                                createModel(itemModel.getObject()),
                                                attributeConfiguration.getValues());

                                return ddp;
                            case COMPLEX:
                                return new AttributesTablePanel(id,
                                        new AttributeDataProvider(attributeConfiguration.getTypename(),
                                                attributeConfiguration.getLabel()), createModel(itemModel.getObject(), metadataModel));
                        }
                    } else {
                        RepeatableAttributeDataProvider repeatableDataProvider = new RepeatableAttributeDataProvider(attributeConfiguration, metadataModel);
                        return new RepeatableAttributesTablePanel(id, attributeConfiguration.getLabel(), repeatableDataProvider, metadataModel);
                    }
                }
                return null;
            }
        };
    }

    private IModel<MetadataMap> createModel(AttributeInput attributeInput, IModel<MetadataMap> metadataModel) {
        if (attributeInput.getInputValue() == null) {
            MetadataMap map = new MetadataMap();
            attributeInput.setInputValue(map);
        }
        return new AbstractPropertyModel<MetadataMap>(attributeInput.getInputValue()) {
            @Override
            protected String propertyExpression() {
                return "";
            }
        };
    }


    private IModel<String> createModel(AttributeInput attributeInput) {
        return new IModel<String>() {
            @Override
            public String getObject() {
                return (String) attributeInput.getInputValue();
            }

            @Override
            public void setObject(String object) {
                attributeInput.setInputValue(object);
            }

            @Override
            public void detach() {

            }
        };
    }

}