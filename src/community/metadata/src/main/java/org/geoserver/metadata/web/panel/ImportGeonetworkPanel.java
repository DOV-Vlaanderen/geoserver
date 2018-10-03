/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
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
        Form<Object> form = new Form<Object>("form");

        ArrayList<String> optionsGeonetwork = new ArrayList<>();
        for (MetadataGeonetworkConfiguration geonetwork : geonetworks) {
            optionsGeonetwork.add(geonetwork.getName());
        }

        DropDownChoice<String> dropDown = createDropDown(optionsGeonetwork);
        form.add(dropDown);

        TextField<String> inputUUID = new TextField<>("textfield", createStringModel());
        form.add(inputUUID);

        form.add(new AjaxFallbackLink<Object>("link") {
            /**
             * 
             */
            private static final long serialVersionUID = -8718015688839770852L;

            public void onClick(AjaxRequestTarget target) {
                //String url = generateMetadataUrl(dropDown.getModelValue(), inputUUID.getValue());
                String url = "https://oefen.dov.vlaanderen.be/geonetwork/srv/api/records/1a2c6739-3c62-432b-b2a0-aaa589a9e3a1/formatters/xml";
                handleImport(url, target);
            }
        });
        add(form);
    }

    public void handleImport(String url, AjaxRequestTarget target) {

    }

    private String generateMetadataUrl(String modelValue, String uuid) {
        String url = "";

        for (MetadataGeonetworkConfiguration geonetwork : geonetworks) {
            if (modelValue.equals(geonetwork.getName())) {
                url = geonetwork.getUrl();
            }
        }
        if (!url.contains("xml_iso19139_save?uuid=")) {
            //assume we got the base url.
            if (!url.endsWith("/")) {
                url = url + "/";
            }
            url = url + "srv/xml_iso19139_save?uuid=";
        }
        url = url + uuid;
        return url;
    }


    private DropDownChoice<String> createDropDown(final ArrayList<String> optionsGeonetwork) {
        return new DropDownChoice<String>("geonetworkName", createStringModel(), optionsGeonetwork);
    }

    private IModel<String> createStringModel() {
        return new IModel<String>() {
            /**
             * 
             */
            private static final long serialVersionUID = 7255270070196033720L;
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
        };
    }

}
