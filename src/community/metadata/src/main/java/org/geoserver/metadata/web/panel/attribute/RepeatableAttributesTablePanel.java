/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.geoserver.metadata.data.ComplexMetadataAttribute;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.dto.MetadataAttributeConfiguration;
import org.geoserver.metadata.data.model.ComplexMetadataAttributeModel;
import org.geoserver.web.wicket.GeoServerDataProvider;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geotools.util.logging.Logging;

import java.util.Arrays;
import java.util.logging.Logger;

public class RepeatableAttributesTablePanel extends Panel {
    private static final long serialVersionUID = 1297739738862860160L;

    private static final Logger LOGGER = Logging.getLogger(RepeatableAttributesTablePanel.class);


    public RepeatableAttributesTablePanel(String id,
                                          RepeatableAttributeDataProvider<String> dataProvider,
                                          IModel<ComplexMetadataMap> metadataModel) {
        super(id, metadataModel);

        GeoServerTablePanel<ComplexMetadataAttribute<String>> tablePanel =
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
/*
        add(new AjaxSubmitLink("removeSelected") {

            private static final long serialVersionUID = -8829474855848647384L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ((RepeatableAttributeDataProvider<String>) dataProvider).removeFields(tablePanel.getSelection());
                tablePanel.clearSelection();
                target.add(tablePanel);
            }
        });*/

        add(new AjaxSubmitLink("addNew") {

            private static final long serialVersionUID = 6840006565079316081L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dataProvider.addField();

                target.add(tablePanel);
            }
        });
    }

    private GeoServerTablePanel<ComplexMetadataAttribute<String>> createAttributesTablePanel(
            RepeatableAttributeDataProvider<String> dataProvider) {
        GeoServerTablePanel<ComplexMetadataAttribute<String>> tablePanel =
                new GeoServerTablePanel<ComplexMetadataAttribute<String>>("attributesTablePanel", dataProvider) {

                    private static final long serialVersionUID = 4333335931795175790L;

                    @Override
                    protected Component getComponentForProperty(String id,
                                                                IModel<ComplexMetadataAttribute<String>> itemModel,
                                                                GeoServerDataProvider.Property<ComplexMetadataAttribute<String>> property) {
                        if (property.getName().equals(RepeatableAttributeDataProvider.KEY_VALUE)) {
                            MetadataAttributeConfiguration attributeConfiguration =
                                    dataProvider.getConfiguration();
                            switch (attributeConfiguration.getFieldType()) {
                                case TEXT:
                                    return new TextFieldPanel(id,
                                            new ComplexMetadataAttributeModel<String>(itemModel.getObject()));
                                case NUMBER:
                                    return new TextFieldPanel(id,
                                            new ComplexMetadataAttributeModel<String>(itemModel.getObject()));
                                case DROPDOWN:
                                    final DropDownPanel ddp =
                                            new DropDownPanel(id,
                                                    new ComplexMetadataAttributeModel<String>(itemModel.getObject()),
                                                    attributeConfiguration.getValues());

                                    return ddp;
                            }
                        } else if (property.getName().equals(RepeatableAttributeDataProvider.KEY_REMOVE_ROW)) {
                            AjaxSubmitLink deleteAction = new AjaxSubmitLink(id) {

                                private static final long serialVersionUID = -8829474855848647384L;

                                @Override
                                public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                                    removeFields(target, itemModel);
                                }
                            };
                            deleteAction.add(new AttributeAppender("class", "remove-link"));
                            return deleteAction;

                        }
                        return null;
                    }

                    private void removeFields(AjaxRequestTarget target, IModel<ComplexMetadataAttribute<String>> itemModel) {
                        ComplexMetadataAttribute<String> object = itemModel.getObject();
                        dataProvider.removeField(object);
                        target.add(this);
                    }
                };
        return tablePanel;
    }


}
