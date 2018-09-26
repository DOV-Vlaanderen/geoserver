/* (c) 2014-2015 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.layer.resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.KeywordInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.ProjectionPolicy;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.metadata.web.panel.MetadataPanel;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.data.resource.ResourceConfigurationPanel;
import org.geoserver.web.wicket.CRSPanel;
import org.geoserver.web.wicket.EnvelopePanel;
import org.geoserver.web.wicket.FeedbackMessageCleaner;
import org.geoserver.web.wicket.GeoServerAjaxFormLink;
import org.geoserver.web.wicket.KeywordsEditor;
import org.geoserver.web.wicket.LiveCollectionModel;
import org.geoserver.web.wicket.SRSToCRSModel;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A generic configuration panel for all basic ResourceInfo properties
 */
public class MetadataTabPanel extends ResourceConfigurationPanel  {

    private static final long serialVersionUID = -552158739086379566L;

    public MetadataTabPanel(String id, IModel<ResourceInfo> model) {
        super(id, model);

        IModel<MetadataMap> metadataModel = new CompoundPropertyModel<MetadataMap>(model.getObject().getMetadata());
        this.add(new MetadataPanel("metadataPanel", metadataModel));
    }


}
