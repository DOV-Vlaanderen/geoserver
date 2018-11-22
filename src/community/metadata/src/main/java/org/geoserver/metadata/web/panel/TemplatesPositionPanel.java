/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.geoserver.metadata.data.model.MetadataTemplate;
import org.geoserver.metadata.data.service.MetadataTemplateService;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.GeoServerBasePage;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.web.wicket.ImageAjaxLink;
import org.geoserver.web.wicket.ParamResourceModel;

import java.util.List;

public class TemplatesPositionPanel extends Panel {
    private static final long serialVersionUID = -4645368967597125299L;

    private ImageAjaxLink<Object> upLink;
    private ImageAjaxLink<Object> downLink;

    public TemplatesPositionPanel(String id, IModel<MetadataTemplate> model, GeoServerTablePanel<MetadataTemplate> tablePanel) {
        super(id, model);
        upLink = new ImageAjaxLink<Object>("up", new PackageResourceReference(GeoServerBasePage.class,
                "img/icons/silk/arrow_up.png")) {
            private static final long serialVersionUID = -4165434301439054175L;

            @Override
            protected void onClick(AjaxRequestTarget target) {
                MetadataTemplateService service =
                        GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
                service.increasePriority(model.getObject());
                ((MarkupContainer) tablePanel.get("listContainer").get("items")).removeAll();
                tablePanel.clearSelection();
                target.add(tablePanel);
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                MetadataTemplateService service =
                        GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
                List<MetadataTemplate> templates = service.list();
                if (getIndex(model.getObject(), templates) == 0) {
                    tag.put("style", "visibility:hidden");
                } else {
                    tag.put("style", "visibility:visible");
                }
            }
        };
        upLink.getImage().add(new AttributeModifier("alt",
                new ParamResourceModel("up", TemplatesPositionPanel.this)));
        add(upLink);

        downLink = new ImageAjaxLink<Object>("down", new PackageResourceReference(GeoServerBasePage.class,
                "img/icons/silk/arrow_down.png")) {
            private static final long serialVersionUID = -8005026702401617344L;

            @Override
            protected void onClick(AjaxRequestTarget target) {
                MetadataTemplateService service =
                        GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
                service.decreasePriority(model.getObject());

                ((MarkupContainer) tablePanel.get("listContainer").get("items")).removeAll();
                tablePanel.clearSelection();
                target.add(tablePanel);
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                MetadataTemplateService service =
                        GeoServerApplication.get().getApplicationContext().getBean(MetadataTemplateService.class);
                List<MetadataTemplate> templates = service.list();
                if (getIndex(model.getObject(), templates) == templates.size() - 1) {
                    tag.put("style", "visibility:hidden");
                } else {
                    tag.put("style", "visibility:visible");
                }
            }
        };
        downLink.getImage().add(new AttributeModifier("alt",
                new ParamResourceModel("down", TemplatesPositionPanel.this)));
        add(downLink);
    }


    private int getIndex(MetadataTemplate template, List<MetadataTemplate> templates) {
        for (int i = 0; i < templates.size(); i++) {
            MetadataTemplate current = templates.get(i);
            if (template.getName().equals(current.getName())) {
                return i;
            }
        }
        return -1;
    }
}