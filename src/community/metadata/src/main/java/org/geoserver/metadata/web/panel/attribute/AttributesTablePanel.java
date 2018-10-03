/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.model.AttributeInput;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.logging.Logging;

import java.util.logging.Logger;

public class AttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(AttributesTablePanel.class);


    private final IModel<MetadataMap> metadataModel;


    public AttributesTablePanel(String id, GeoServerDataProvider dataProvider, IModel<MetadataMap> metadataModel) {
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

        this.metadataModel = metadataModel;


    }

    private GeoServerTablePanel<AttributeInput> createAttributesTablePanel(GeoServerDataProvider dataProvider) {
        return new GeoServerTablePanel<AttributeInput>("attributesTablePanel", dataProvider) {
            @Override
            protected Component getComponentForProperty(String id, IModel<AttributeInput> itemModel, GeoServerDataProvider.Property<AttributeInput> property) {
                if (property.equals(AttributeDataProvider.VALUE)) {
                    MetadataAttributeConfiguration attributeConfiguration = itemModel.getObject().getAttributeConfiguration();
                    if (OccurenceEnum.SINGLE.equals(attributeConfiguration.getOccurrence())) {
                        return EditorFactory.create(attributeConfiguration,id, metadataModel);
                    } else {
                        /*return new ListAttributesPanel(attributeConfiguration,id,metadataModel);*/
                        RepeatableAttributeDataProvider repeatableDataProvider = new RepeatableAttributeDataProvider(attributeConfiguration, metadataModel);
                        return new RepeatableAttributesTablePanel(id, attributeConfiguration.getLabel(), repeatableDataProvider, metadataModel);
                    }
                }
                return null;
            }

        };
    }

}