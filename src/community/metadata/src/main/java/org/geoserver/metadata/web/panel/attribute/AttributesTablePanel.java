/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.model.AttributeInput;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.logging.Logging;

import java.util.logging.Logger;

public class AttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(AttributesTablePanel.class);



    public AttributesTablePanel(String id, GeoServerDataProvider<AttributeInput> dataProvider, 
            IModel<ComplexMetadataMap> metadataModel) {
        super(id, metadataModel);


        GeoServerTablePanel<AttributeInput> tablePanel = createAttributesTablePanel(dataProvider);
        tablePanel.setFilterVisible(false);
        tablePanel.setFilterable(false);
        tablePanel.getTopPager().setVisible(false);
        tablePanel.getBottomPager().setVisible(false);
        tablePanel.setOutputMarkupId(false);
        tablePanel.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        tablePanel.setSelectable(false);
        tablePanel.setSortable(false);
        add(tablePanel);


    }

    private GeoServerTablePanel<AttributeInput> createAttributesTablePanel(GeoServerDataProvider<AttributeInput> dataProvider) {
        return new GeoServerTablePanel<AttributeInput>("attributesTablePanel", dataProvider) {
            private static final long serialVersionUID = 5267842353156378075L;

            @Override
            protected Component getComponentForProperty(String id, IModel<AttributeInput> itemModel, GeoServerDataProvider.Property<AttributeInput> property) {
                if (property.equals(AttributeDataProvider.VALUE)) {
                    MetadataAttributeConfiguration attributeConfiguration = itemModel.getObject().getAttributeConfiguration();
                    if (OccurenceEnum.SINGLE.equals(attributeConfiguration.getOccurrence())) {
                        return EditorFactory.create(attributeConfiguration,id, getMetadataModel());
                    } else if (attributeConfiguration.getFieldType() == FieldTypeEnum.COMPLEX){
                        RepeatableComplexAttributeDataProvider repeatableDataProvider = 
                                new RepeatableComplexAttributeDataProvider(attributeConfiguration, 
                                getMetadataModel());

                        return new RepeatableComplexAttributesTablePanel(id,
                                repeatableDataProvider,
                                getMetadataModel());
                    } else {
                        RepeatableAttributeDataProvider<String> repeatableDataProvider = 
                                new RepeatableAttributeDataProvider<String>(String.class, attributeConfiguration, 
                                getMetadataModel());
                        return new RepeatableAttributesTablePanel(id,
                                  repeatableDataProvider,
                                getMetadataModel());
                    }
                }
                return null;
            }

        };
    }


    @SuppressWarnings("unchecked")
    public IModel<ComplexMetadataMap> getMetadataModel() {
        return (IModel<ComplexMetadataMap>) getDefaultModel();
    }
}
