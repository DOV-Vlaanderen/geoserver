/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.dto.MetadataGeonetworkConfiguration;
import org.geoserver.metadata.data.service.MetadataEditorConfigurationService;
import org.geoserver.web.GeoServerApplication;
import org.geotools.util.logging.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ImportGeonetworkPanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(ImportGeonetworkPanel.class);

    private List<MetadataGeonetworkConfiguration> geonetworks = new ArrayList<>();


    public ImportGeonetworkPanel(String id) {
        super(id);
        MetadataEditorConfigurationService metadataService = GeoServerApplication.get().getApplicationContext().getBean(MetadataEditorConfigurationService.class);
        MetadataEditorConfiguration configuration = metadataService.readConfiguration();
        if (configuration != null && configuration.getGeonetworks() != null) {
            this.geonetworks = configuration.getGeonetworks();
        }

    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        Form form = new Form("form");

        ArrayList<String> optionsGeonetwork = new ArrayList<>();
        for (MetadataGeonetworkConfiguration geonetwork : geonetworks) {
            optionsGeonetwork.add(geonetwork.getName());
        }

        form.add(createDropDown(optionsGeonetwork));

        form.add(new TextField<String>("textfield"));

        Button buttonImport = new Button("buttonImport") {
            public void onSubmit() {
                info("buttonImport.onSubmit executed");
            }
        };
        form.add(buttonImport);
        add(form);
    }


    private DropDownChoice<String> createDropDown(final ArrayList<String> optionsGeonetwork) {
        return new DropDownChoice<String>("geonetworkName", new IModel<String>() {
            public String option;

            @Override
            public String getObject() {
                return option;
            }

            @Override
            public void setObject(String t) {
                option = t;
            }

            @Override
            public void detach() {
            }
        }, optionsGeonetwork);
    }

}
