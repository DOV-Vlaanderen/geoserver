/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web.action;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.geoserver.taskmanager.web.ConfigurationPage;
import org.geoserver.taskmanager.web.panel.FileUploadPanel;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.ParamResourceModel;
import org.springframework.stereotype.Component;

/**
 * FileUploadAction
 *
 * @author Timothy De Bock
 */
@Component
public class FileUploadAction implements Action {

    private static final long serialVersionUID = 4996136164811697150L;

    private final static String NAME = "FileUpload";

    FileUploadPanel panel;
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute(ConfigurationPage onPage, AjaxRequestTarget target, IModel<String> valueModel) {
        GeoServerDialog dialog = onPage.getDialog();

        dialog.setTitle(new ParamResourceModel("FileUploadPanel.dialogTitle", onPage.getPage()));
        dialog.setInitialWidth(650);
        dialog.setInitialHeight(300);
        dialog.showOkCancel(target, new GeoServerDialog.DialogDelegate() {

            private static final long serialVersionUID = 7410393012930249966L;

            @Override
            protected org.apache.wicket.Component getContents(String id) {
                panel = new FileUploadPanel(id, valueModel);
                return panel;
            }

            @Override
            protected boolean onSubmit(AjaxRequestTarget target, org.apache.wicket.Component contents) {
                panel.onSubmit();
                onPage.addAttributesPanel(target);
                return true;
            }

            @Override
            public void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(panel.getFeedbackPanel());
            }
        });



    }

}
