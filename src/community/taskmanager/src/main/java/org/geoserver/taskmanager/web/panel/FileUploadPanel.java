/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web.panel;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.geoserver.taskmanager.data.FileService;
import org.geoserver.taskmanager.data.FileServiceRegistry;
import org.geoserver.web.GeoServerApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUploadPanel extends Panel {

    private Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "wicket-uploads");




    private static final long serialVersionUID = -1821529746678003578L;

    public FileUploadPanel(String id, IModel<String> model) {
        super(id, model);

        final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload");
        add(simpleUploadForm);

    }


    /**
     * Form for uploads.
     */

    private class FileUploadForm extends Form<Void> {
        FileUploadField fileUploadField;

        /**
         * Construct.
         *
         * @param name Component name
         */

        public FileUploadForm(String name) {
            super(name);

            // set this form to multipart mode (always needed for uploads!)
            setMultiPart(true);

            // Add one file input field
            add(fileUploadField = new FileUploadField("fileInput"));
        }

        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */

        @Override
        protected void onSubmit() {
            final List<FileUpload> uploads = fileUploadField.getFileUploads();
            if (uploads != null) {
                FileServiceRegistry fileServiceRegistry=
                        GeoServerApplication.get().getApplicationContext().getBean(FileServiceRegistry.class);
                for (FileUpload upload : uploads) {
                    //TODO get from gui
                    FileService fileService =
                            fileServiceRegistry.getService(fileServiceRegistry.getFileServiceNames().get(0));

                    /*// Create a new file
                    File newFile = new File(getUploadFolder(), upload.getClientFileName());

                    // Check new file, delete if it already existed
                    checkFileExists(newFile);*/
                    try {
                        Path filePath = Paths.get("test/"+upload.getClientFileName());
                        if (fileService.checkFileExists(filePath)) {
                            fileService.delete(filePath);
                        }
                        fileService.create(filePath, upload.getInputStream());
                        /*// Save to new file
                        newFile.createNewFile();
                        upload.writeTo(newFile);*/
                        System.out.println("UPLOAD complete");
                    } catch (Exception e) {
                        throw new IllegalStateException("Unable to write file", e);
                    }
                }
            }
        }
    }


    /**
     * Check whether the file allready exists, and if so, try to delete it.
     *
     * @param newFile the file to check
     */
    private void checkFileExists(File newFile) {
        if (newFile.exists()) {
            // Try to delete the file
            if (!Files.remove(newFile)) {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    private Folder getUploadFolder() {
        return uploadFolder;
    }
}
