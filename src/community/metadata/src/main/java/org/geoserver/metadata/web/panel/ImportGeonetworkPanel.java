/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.dto.MetadataEditorConfiguration;
import org.geoserver.metadata.data.dto.MetadataGeonetworkConfiguration;
import org.geoserver.metadata.data.service.MetadataEditorConfigurationService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.ParamResourceModel;
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
        add(new FeedbackPanel("feedback").setOutputMarkupId(true));

        Form<Object> form = new Form<Object>("form");

        ArrayList<String> optionsGeonetwork = new ArrayList<>();
        for (MetadataGeonetworkConfiguration geonetwork : geonetworks) {
            optionsGeonetwork.add(geonetwork.getName());
        }

        DropDownChoice<String> dropDown = createDropDown(optionsGeonetwork);
        /*dropDown.setRequired(true);
        dropDown.setOutputMarkupId(true);*/
        form.add(dropDown);

        TextField<String> inputUUID = new TextField<>("textfield", createStringModel());
        /*inputUUID.setRequired(true);
        inputUUID.setOutputMarkupId(true);*/
        form.add(inputUUID);

        form.add(new AjaxSubmitLink("link") {
            /**
             *
             */
            private static final long serialVersionUID = -8718015688839770852L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                boolean valid = true;
                if (dropDown.getModelObject() == null) {
                    error(new ParamResourceModel("errorSelectGeonetwork",
                            ImportGeonetworkPanel.this).getString());
                    valid = false;
                }
                if ("".equals(inputUUID.getValue())) {
                    error(new ParamResourceModel("errorUuidRequired",
                            ImportGeonetworkPanel.this).getString());
                    valid = false;
                }
                if (valid) {
                    String url = generateMetadataUrl(dropDown.getModelObject(), inputUUID.getValue());
                    handleImport(url, target);
                }
                target.add(getFeedbackPanel());
            }

        });
        add(form);
    }

    public void handleImport(String url, AjaxRequestTarget target) {

    }

    private String generateMetadataUrl(String modelValue, String uuid) {
        String url = "";

        if (modelValue != null) {
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
        }
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


    public FeedbackPanel getFeedbackPanel() {
        return (FeedbackPanel) get("feedback");
    }
}
