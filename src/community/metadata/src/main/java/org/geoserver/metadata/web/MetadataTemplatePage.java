/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.metadata.web;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.ComponentAuthorizer;
import org.geoserver.web.GeoServerBasePage;
import org.geotools.util.logging.Logging;

import java.util.logging.Logger;

public class MetadataTemplatePage extends GeoServerBasePage {

    private static final Logger LOGGER = Logging.getLogger(MetadataTemplatePage.class);

    private static final long serialVersionUID = 2273966783474224452L;

    public MetadataTemplatePage() {
    }

    public void onInitialize() {
        super.onInitialize();
        CompoundPropertyModel<MetadataMap> metadataModel = new CompoundPropertyModel<MetadataMap>(getMetadataMap());

        MetadataPanel metadataTemplatePanel = new MetadataPanel("metadataTemplatePanel", metadataModel);
        Form<?> form = new Form<Object>("form") {
            /**
             * 
             */
            private static final long serialVersionUID = -5120413426598396101L;

            @Override
            protected void onSubmit() {
                System.out.println("Form submitted.");
                metadataModel.getObject();
            }
        };
        form.add(metadataTemplatePanel);
        this.add(form);
        //metadataTemplatePanel.setGeonetworkPanelVisible(false);
    }

    protected ComponentAuthorizer getPageAuthorizer() {
        return ComponentAuthorizer.AUTHENTICATED;
    }

    private MetadataMap getMetadataMap() {
        MetadataMap map = new MetadataMap();
       /* //Simple String veld:
        map.put("status", "compleet");
        map.put("scale", "1/150000");
        //simple list
        map.put("concepten", new ArrayList<>());
        ((List<String>) map.get("concepten")).add("bodemerosie");
        ((List<String>) map.get("concepten")).add("erosie");

        //object
        map.put("contact_email", "test.test@test.be");
        map.put("contact_adres", "Koning Albert II-laan 20 bus 8, Brussel, 1000, België");

        //object list
        map.put("contactfortheresource_email", new ArrayList<>());
        ((List<String>) map.get("Contactfortheresource_email")).add("vpo.omgeving@vlaanderen.be");
        ((List<String>) map.get("Contactfortheresource_email")).add("dov@vlaanderen.be");

        map.put("contactfortheresource_adres", new ArrayList<>());
        ((List<String>) map.get("Contactfortheresource_adres")).add("Koning Albert II-laan 20 bus 8, Brussel, 1000, België");
        ((List<String>) map.get("Contactfortheresource_adres")).add("Technologiepark Gebouw 905, Zwijnaarde, 9052, België");
*/
        return map;
    }
}
