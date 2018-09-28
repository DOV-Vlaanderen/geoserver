/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.model.AttributeInput;
import org.geoserver.metadata.web.panel.attribute.AttributeDataProvider;
import org.geoserver.metadata.web.panel.attribute.AttributesTablePanel;
import org.geotools.util.logging.Logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MetadataPanel extends Panel implements IFormModelUpdateListener {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(MetadataPanel.class);


    private final IModel<MetadataMap> metadataModel;

    private ImportGeonetworkPanel geonetworkPanel;

    private boolean geonetworkPanelVisible = true;


    public MetadataPanel(String id, IModel<MetadataMap> metadataModel) {
        super(id);
        this.metadataModel = new IModel<MetadataMap>() {
            @Override
            public MetadataMap getObject() {
                //return metadataModel.getObject();
                return toView(metadataModel);
            }

            @Override
            public void setObject(MetadataMap object) {

            }

            @Override
            public void detach() {

            }
        };
    }


    @Override
    public void onInitialize() {
        super.onInitialize();
        this.setOutputMarkupId(true);
        geonetworkPanel = new ImportGeonetworkPanel("geonetworkPanel") {
            @Override
            public void handleImport(String url, AjaxRequestTarget target) {
                metadataModel.getObject().put("postcode", "negenduust");
                target.add(MetadataPanel.this);
            }
        };
        add(geonetworkPanel);
        geonetworkPanel.setVisible(geonetworkPanelVisible);
        //the attributes panel
        add(new AttributesTablePanel("attributesPanel", new AttributeDataProvider(), metadataModel));

    }


    public boolean isGeonetworkPanelVisible() {
        return geonetworkPanel.isVisible();
    }

    public void setGeonetworkPanelVisible(boolean geonetworkPanelVisible) {
        this.geonetworkPanelVisible = geonetworkPanelVisible;
        if (geonetworkPanel != null) {
            geonetworkPanel.setVisible(geonetworkPanelVisible);
        }
    }

    //******************************************************************************************************************//
    //*** TODO refactor conversion *************************************************************************************//
    //******************************************************************************************************************//
    @Override
    public void updateModel() {
        List<String> keysToDelete = new ArrayList<>();


        MetadataMap tempMap = new MetadataMap();
        MetadataMap metadataMap = metadataModel.getObject();

        for (String metadataKey : metadataMap.keySet()) {
            Serializable val = metadataMap.get(metadataKey);
            if (val instanceof List && !((List) val).isEmpty()) {
                //converting complex objects
                List values = (List) val;
                for (Object value : values) {

                    if (value instanceof AttributeInput) {
                        Object inputAttribute = ((AttributeInput) value).getInputValue();
                        if (inputAttribute instanceof MetadataMap) {
                            keysToDelete.add(metadataKey);
                            MetadataMap map = ((MetadataMap) inputAttribute);
                            for (String key : map.keySet()) {
                                if (!tempMap.containsKey(key)) {
                                    tempMap.put(key, new ArrayList<>());
                                }
                                ((List) tempMap.get(key)).add(map.get(key));
                            }
                        } else if (inputAttribute instanceof String) {
                            //converting list of simple objects
                            if (!tempMap.containsKey(metadataKey)) {
                                tempMap.put(metadataKey, new ArrayList<>());
                            }
                            ((List) tempMap.get(metadataKey)).add(inputAttribute);
                        }
                    }
                }

            }
        }
        //deleting wrong records
        for (String key : keysToDelete) {
            metadataMap.remove(key);
        }
        for (String key : tempMap.keySet()) {
            metadataMap.put(key, tempMap.get(key));
        }
    }


    private MetadataMap toView(IModel<MetadataMap> metadataModel) {
        List<String> keysToDelete = new ArrayList<>();

        MetadataMap tempMap = new MetadataMap();
        MetadataMap metadataMap = metadataModel.getObject();

        for (String metadataKey : metadataMap.keySet()) {
            Serializable val = metadataMap.get(metadataKey);
            if (val instanceof List) {
                List list = (List) val;
                if (metadataKey.contains("_")) {
                    //complex
                    String[] keys = metadataKey.split("_");
                    for (int i = 0; i < list.size(); i++) {
                        Object object = list.get(i);
                        if (!tempMap.containsKey(keys[0])) {
                            ArrayList<AttributeInput> value = new ArrayList<>();
                            for (Object o : list) {
                                AttributeInput e = new AttributeInput(null);
                                e.setInputValue(new MetadataMap());
                                value.add(e);
                            }
                            tempMap.put(keys[0], value);
                        }
                        ((MetadataMap) ((List<AttributeInput>) tempMap.get(keys[0])).get(i).getInputValue()).put(metadataKey, (Serializable) object);
                    }
                    keysToDelete.add(metadataKey);
                } else {
                    //simple
                    for (Object object : list) {
                        if (object instanceof String) {
                            if (!tempMap.containsKey(metadataKey)) {
                                tempMap.put(metadataKey, new ArrayList<>());
                            }
                            AttributeInput e = new AttributeInput(null);
                            e.setInputValue(object);
                            ((List) tempMap.get(metadataKey)).add(e);
                        }
                    }
                }
            }

        }
        //deleting wrong records
        for (String key : keysToDelete) {
            metadataMap.remove(key);
        }
        for (String key : tempMap.keySet()) {
            metadataMap.put(key, tempMap.get(key));
        }
        return metadataMap;
    }


}
