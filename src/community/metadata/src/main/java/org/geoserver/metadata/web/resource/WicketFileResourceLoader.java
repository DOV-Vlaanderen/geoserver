/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.resource;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.geotools.util.logging.Logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class WicketFileResourceLoader implements IStringResourceLoader {

    private static final Logger LOGGER = Logging.getLogger(WicketFileResourceLoader.class);

    private String folder;

    private String resourceBundleName;

    private static String FILE_EXTIONSION = ".properties";

    private boolean shouldThrowException = true;

    public WicketFileResourceLoader(String folder, String resourceBundleName) {
        this.folder = folder;
        this.resourceBundleName = resourceBundleName;
        if (resourceBundleName.endsWith(FILE_EXTIONSION)) {
            this.resourceBundleName = this.resourceBundleName.replace(FILE_EXTIONSION, "");
        }
    }

    public String loadStringResource(Component component, String key) {
        return findResource(component.getLocale(), key);
    }

    public String loadStringResource(Class<?> clazz, String key, Locale locale, String style) {
        return findResource(locale, key);
    }

    private String findResource(Locale locale, String key) {
        String string = null;

        ResourceBundle resourceBundle = null;
        try {
            File file = new File(folder, resourceBundleName + "_" + locale.getLanguage() + FILE_EXTIONSION);
            //Try the specific resource
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    resourceBundle = new PropertyResourceBundle(fis);
                    try {
                        string = findString(key, string, resourceBundle);
                    } catch (Exception ignored) {
                        //ignore, try the generic resource
                    }
                }
            }
            //Fallback to the main resource
            if(string == null) {
                file = new File(folder, resourceBundleName + FILE_EXTIONSION);
                try (FileInputStream fis = new FileInputStream(file)) {
                    resourceBundle = new PropertyResourceBundle(fis);
                    string = findString(key, string, resourceBundle);
                }
            }
        } catch (IOException e) {
            if (shouldThrowExceptionForMissingResource()) {
                throw new WicketRuntimeException(String.format("Unable able to locate resource bundle for the specifed base name: %s", resourceBundleName));
            }
            LOGGER.warning("Unable able to locate resource bundle for the specifed base name:" + resourceBundleName);
        }
        return string;
    }

    private boolean shouldThrowExceptionForMissingResource() {
        return Application.get().getResourceSettings().getThrowExceptionOnMissingResource() && shouldThrowException;
    }

    @Override
    public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
        return findResource(locale, key);
    }

    @Override
    public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
        return findResource(component.getLocale(), key);
    }

    public void setShouldThrowException(boolean shouldThrowException) {
        this.shouldThrowException = shouldThrowException;
    }

    private String findString(String key, String string, ResourceBundle resourceBundle) {
        boolean caught = false;
        try {
            string = resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            caught = true;
        }

        if (caught || string == null) {
            if (shouldThrowExceptionForMissingResource()) {
                throw new WicketRuntimeException(String.format("Unable able to locate resource bundle for the specifed base name: %s", resourceBundleName));
            }

            LOGGER.warning("No value found key " + key + " in resource bundle " + resourceBundleName);
        }
        return string;
    }

    public String getResourceBundleName() {
        return resourceBundleName;
    }
}
