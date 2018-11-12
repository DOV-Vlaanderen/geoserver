/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.web.panel.attribute.AttributeDataProvider;
import org.geoserver.metadata.web.panel.attribute.AttributesTablePanel;

import java.util.HashMap;
import java.util.List;

/**
 * The dynamically generated metadata input panel. All fields are added on the fly based on the yaml configuration.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class MetadataPanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private final HashMap<String, List<Integer>> descriptionMap;


    public MetadataPanel(String id, IModel<ComplexMetadataMap> metadataModel,
                         HashMap<String, List<Integer>> descriptionMap) {
        super(id, metadataModel);
        this.descriptionMap = descriptionMap;
    }


    @Override
    public void onInitialize() {
        super.onInitialize();
        //the attributes panel
        AttributesTablePanel attributesPanel =
                new AttributesTablePanel("attributesPanel",
                        new AttributeDataProvider(),
                        getMetadataModel(),
                        descriptionMap);

        attributesPanel.setOutputMarkupId(true);
        add(attributesPanel);

    }
    
    @SuppressWarnings("unchecked")
    public IModel<ComplexMetadataMap> getMetadataModel() {
        return (IModel<ComplexMetadataMap>) getDefaultModel();
    }


}
