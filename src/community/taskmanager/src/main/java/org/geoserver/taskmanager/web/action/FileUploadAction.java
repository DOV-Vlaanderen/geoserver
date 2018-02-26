/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web.action;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.taskmanager.data.Attribute;
import org.geoserver.taskmanager.data.BatchElement;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.util.TaskManagerBeans;
import org.geoserver.taskmanager.web.ConfigurationPage;
import org.geoserver.taskmanager.web.panel.FileUploadPanel;
import org.geoserver.taskmanager.web.panel.TaskParameterPanel;
import org.geoserver.web.data.resource.ResourceConfigurationPage;
import org.geoserver.web.data.store.panel.FileModel;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.ParamResourceModel;
import org.geoserver.web.wicket.browser.GeoServerFileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

/**
 * FileParamPanel
 * @author Timothy De Bock
 *
 */
@Component
public class FileUploadAction implements Action {
    
    private static final long serialVersionUID = 4996136164811697150L;
    
    private final static String NAME = "FileUpload";




    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String execute(ConfigurationPage onPage, String value) {return value;}

    public String execute(ConfigurationPage onPage, AjaxRequestTarget target, IModel<Attribute> itemModel) {
        GeoServerDialog dialog = onPage.getDialog();

        dialog.setTitle(new ParamResourceModel("confirmDeleteDialog.title", onPage.getPage()));
        dialog.setInitialWidth(600);
        dialog.setInitialHeight(150);
        dialog.showOkCancel(target, new GeoServerDialog.DialogDelegate() {

            private static final long serialVersionUID = 7410393012930249966L;

            @Override
            protected org.apache.wicket.Component getContents(String id) {
                return new FileUploadPanel(id, null);
            }

            @Override
            protected boolean onSubmit(AjaxRequestTarget target,
                    org.apache.wicket.Component contents) {
                itemModel.getObject().setValue("TEST");
                return true;
            }

        });


        return itemModel.getObject().getValue();
    }

}
