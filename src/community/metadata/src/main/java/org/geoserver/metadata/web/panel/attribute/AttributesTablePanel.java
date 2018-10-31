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
import org.geoserver.metadata.data.model.ComplexMetadataAttribute;
import org.geoserver.metadata.data.model.ComplexMetadataAttributeModel;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.logging.Logging;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
/**
 * Entry point for the gui generation.
 * This parses the configuration and adds simple fields, complex fields (composition of multiple simple fields)
 * and lists of simple or complex fields.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class AttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(AttributesTablePanel.class);



    public AttributesTablePanel(String id,
                                GeoServerDataProvider<AttributeInput> dataProvider,
                                IModel<ComplexMetadataMap> metadataModel,
                                HashMap<String, List<Integer>> descriptionMap) {
        super(id, metadataModel);


        GeoServerTablePanel<AttributeInput> tablePanel = createAttributesTablePanel(dataProvider, descriptionMap);
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

    private GeoServerTablePanel<AttributeInput> createAttributesTablePanel(GeoServerDataProvider<AttributeInput> dataProvider,
                                                                           HashMap<String, List<Integer>> descriptionMap) {

        return new GeoServerTablePanel<AttributeInput>("attributesTablePanel", dataProvider) {
            private static final long serialVersionUID = 5267842353156378075L;

            @Override
            protected Component getComponentForProperty(String id, IModel<AttributeInput> itemModel,
                                                        GeoServerDataProvider.Property<AttributeInput> property) {
                if (property.equals(AttributeDataProvider.VALUE)) {
                    MetadataAttributeConfiguration attributeConfiguration = itemModel.getObject().getAttributeConfiguration();
                    if (OccurenceEnum.SINGLE.equals(attributeConfiguration.getOccurrence())) {
                        Component component = EditorFactory.getInstance().create(
                                attributeConfiguration,
                                id,
                                getMetadataModel().getObject());
                        //disable components with values from the templates
                        if (component != null &&
                                descriptionMap != null &&
                                descriptionMap.containsKey(attributeConfiguration.getKey())) {
                            boolean disableInput = descriptionMap.get(attributeConfiguration.getKey()).size() > 0;
                            component.setEnabled(!disableInput);
                        }
                        return component;
                    } else if (attributeConfiguration.getFieldType() == FieldTypeEnum.COMPLEX){
                        RepeatableComplexAttributeDataProvider repeatableDataProvider = 
                                new RepeatableComplexAttributeDataProvider(attributeConfiguration, 
                                getMetadataModel());

                        return new RepeatableComplexAttributesTablePanel(id,
                                repeatableDataProvider,
                                getMetadataModel(),
                                descriptionMap);
                    } else {
                        RepeatableAttributeDataProvider<String> repeatableDataProvider = 
                                new RepeatableAttributeDataProvider<String>(String.class, attributeConfiguration, 
                                getMetadataModel());
                        return new RepeatableAttributesTablePanel(id,
                                  repeatableDataProvider,
                                getMetadataModel(),
                                descriptionMap);
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
