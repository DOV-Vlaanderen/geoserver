/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.taskmanager.fileservice.FileService;
import org.geoserver.taskmanager.fileservice.impl.LookupFileServiceImpl;
import org.geoserver.taskmanager.util.LookupService;
import org.geoserver.taskmanager.util.TaskManagerBeans;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.ParamResourceModel;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Upload a File with the @{FileService} .
 *
 *
 * @author Timothy De Bock
 */
public class FileUploadPanel extends Panel {


    private static final long serialVersionUID = -1821529746678003578L;

    private FeedbackPanel feedbackPanel;


    public FileUploadPanel(String id, IModel<String> model) {
        super(id, model);

        final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload", model);
        add(simpleUploadForm);
    }


    private class FileUploadForm extends Form<Void> {
        private IModel<String> fileNameModel;

        private final GeoServerDialog dialog;
        /**
         * Form for uploads.
         */

        FileUploadField fileUploadField;


        private final FileUploadModel fileUploadModel = new FileUploadModel();

        /**
         * Construct.
         *
         * @param name          Component name
         * @param fileNameModel the model that will contain the name of the file.
         */

        public FileUploadForm(String name, IModel<String> fileNameModel) {

            super(name);
            this.fileNameModel = fileNameModel;

            setDefaultModel(new CompoundPropertyModel<>(fileUploadModel));


            add(dialog = new GeoServerDialog("dialog"));
            add(feedbackPanel = new FeedbackPanel("feedback"));
            feedbackPanel.setOutputMarkupId(true);

            setMultiPart(true);


            DropDownChoice<String> fileServiceChoice =
                    new DropDownChoice("fileServiceSelection",
                            new ArrayList<>(TaskManagerBeans.get().getFileServices().names())) {
                        @Override
                        public boolean isRequired() {
                            Component submit = getParent().getParent().getParent().get("submit");
                            Form dialogForm = (Form) getParent().getParent().getParent();
                            return dialogForm.findSubmittingButton() == submit;
                        }
                    };
            add(fileServiceChoice);

            DropDownChoice<String> folderChoice = new DropDownChoice("folderSelection", new ArrayList<>()){
                @Override
                public boolean isRequired() {
                    Component submit = getParent().getParent().getParent().get("submit");
                    Form dialogForm = (Form) getParent().getParent().getParent();
                    return dialogForm.findSubmittingButton() == submit;
                }
            };
            folderChoice.setOutputMarkupId(true);
            add(folderChoice);

            fileServiceChoice.add(createFileServiceSelectionChangedBehavior(this, fileServiceChoice, folderChoice));

            add(createAddFolderButton(folderChoice));
            add(fileUploadField = new FileUploadField("fileInput") {
                @Override
                public boolean isRequired() {
                    Component submit = getParent().getParent().getParent().get("submit");
                    Form dialogForm = (Form) getParent().getParent().getParent();
                    return dialogForm.findSubmittingButton() == submit;
                }
            });
            //fileUploadField.setRequired(true);
        }


        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */

        @Override
        protected void onSubmit() {
            final List<FileUpload> uploads = fileUploadField.getFileUploads();
            if (uploads != null) {
                for (FileUpload upload : uploads) {
                    FileService fileService =
                            TaskManagerBeans.get().getFileServices().get(fileUploadModel.fileServiceSelection);
                    try {
                        Path filePath = Paths.get(fileUploadModel.folderSelection + "/" + upload.getClientFileName());
                        if (fileService.checkFileExists(filePath)) {
                            fileService.delete(filePath);
                        }
                        String path = fileService.create(filePath, upload.getInputStream());

                        fileNameModel.setObject(path);
                    } catch (Exception e) {
                        throw new IllegalStateException("Unable to write file", e);
                    }
                }
            }
        }

        /**
         * React to the file service changed event. Update the available folders drop down.
         *
         * @param parent
         * @param fileServiceChoice
         * @param folderChoice
         * @return
         */
        private AjaxFormComponentUpdatingBehavior createFileServiceSelectionChangedBehavior(
                final FileUploadForm parent,
                final DropDownChoice<String> fileServiceChoice,
                final DropDownChoice<String> folderChoice) {

            return new AjaxFormComponentUpdatingBehavior("change") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    String serviceName = fileServiceChoice.getModel().getObject();
                    List availableFolders = folderChoice.getChoicesModel().getObject();
                    availableFolders.clear();
                    if (serviceName != null) {
                        try {
                            List<Path> paths =
                                    TaskManagerBeans.get().getFileServices().get(serviceName).listSubfolders();
                            for (Path path : paths) {
                                availableFolders.add(path.toString());
                            }
                        } catch (IOException e) {
                            parent.error("Could not get folders for service:" + e.getMessage());
                        }
                    }
                    folderChoice.setChoices(availableFolders);

                    target.add(folderChoice);
                    target.add(parent);
                }
            };
        }

        /**
         * .
         * Create the 'add new folder' button.
         *
         * @param folderChoice
         * @return
         */
        protected AjaxSubmitLink createAddFolderButton(DropDownChoice<String> folderChoice) {
            return new AjaxSubmitLink("addNew") {

                private static final long serialVersionUID = 7320342263365531859L;

                @Override
                public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    dialog.setTitle(new ParamResourceModel("FileUpload.panel.filename", getPage()));
                    dialog.setInitialHeight(100);
                    dialog.setInitialWidth(630);
                    dialog.showOkCancel(target, new GeoServerDialog.DialogDelegate() {

                        private static final long serialVersionUID = 7410393012930249966L;

                        private TextFieldPanel panel;

                        @Override
                        protected Component getContents(String id) {
                            panel = new TextFieldPanel(id, new Model<>());
                            panel.getTextField().setRequired(true);
                            panel.setOutputMarkupId(true);
                            return panel;
                        }

                        @Override
                        protected boolean onSubmit(AjaxRequestTarget target, Component contents) {
                            target.add(panel);

                            List availableFolders = folderChoice.getChoicesModel().getObject();
                            String folderName = panel.getTextField().getModel().getObject();
                            availableFolders.add(folderName);
                            folderChoice.setModelObject(folderName);
                            target.add(folderChoice);
                            return true;
                        }

                    });
                }
            };
        }

        /**
         * Model for the file upload form.
         */
        public class FileUploadModel implements Serializable {

            private String fileServiceSelection;
            private String folderSelection;

            //TODO why is this needed :( ?
            private String fileInput;
        }

    }

    public FeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }

}
