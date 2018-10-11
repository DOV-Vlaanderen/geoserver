/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.logging.Logging;

import java.util.logging.Logger;

public class RepeatableComplexAttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(RepeatableComplexAttributesTablePanel.class);


    public RepeatableComplexAttributesTablePanel(String id,
            RepeatableComplexAttributeDataProvider dataProvider, 
            IModel<ComplexMetadataMap> metadataModel) {
        super(id, metadataModel);

        GeoServerTablePanel<ComplexMetadataMap> tablePanel = 
                createAttributesTablePanel(dataProvider);
        tablePanel.setFilterVisible(false);
        tablePanel.setFilterable(false);
        tablePanel.getTopPager().setVisible(false);
        tablePanel.getBottomPager().setVisible(false);
        tablePanel.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        tablePanel.setSelectable(true);
        tablePanel.setSortable(false);
        tablePanel.setOutputMarkupId(true);
        add(tablePanel);

        add(new AjaxSubmitLink("removeSelected") {

            private static final long serialVersionUID = -8829474855848647384L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dataProvider.removeFields(tablePanel.getSelection());
                tablePanel.clearSelection();
                target.add(tablePanel);
            }
        });

        add(new AjaxSubmitLink("addNew") {

            private static final long serialVersionUID = 6840006565079316081L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dataProvider.addField();

                target.add(tablePanel);
            }
        });
    }

    private GeoServerTablePanel<ComplexMetadataMap> createAttributesTablePanel(
            RepeatableComplexAttributeDataProvider dataProvider) {
        return new GeoServerTablePanel<ComplexMetadataMap>("attributesTablePanel", dataProvider) {
            private static final long serialVersionUID = 4333335931795175790L;

            @Override
            protected Component getComponentForProperty(String id, 
                    IModel<ComplexMetadataMap> itemModel, 
                    GeoServerDataProvider.Property<ComplexMetadataMap> property) {
                if (property.getName().equals(RepeatableAttributeDataProvider.KEY_VALUE)) {
                    MetadataAttributeConfiguration attributeConfiguration = 
                            dataProvider.getConfiguration();
                   
                    return new AttributesTablePanel(id,
                         new AttributeDataProvider(attributeConfiguration.getTypename()),
                             itemModel);
                }
                return null;
            }
        };
    }

}
