/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.store.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.geoserver.csw.CSWTestSupport;
import org.geoserver.csw.util.PropertyPath;
import org.geoserver.security.PropertyFileWatcher;
import org.junit.Test;

public class InternalCatalogStoreTest extends CSWTestSupport {

    @Test
    public void testModifyMappingFiles() throws IOException, InterruptedException {

        // clear any existing mappings
        File root = testData.getDataDirectoryRoot();
        File csw = new File(root, "csw");
        if (csw.exists()) {
            csw.delete();
        }

        // get the store
        InternalCatalogStore store =
                applicationContext.getBean(
                        InternalCatalogStore
                                .class); // new InternalCatalogStore(this.getGeoServer());
        assertNotNull(store);

        // test if we have default mapping
        File record = new File(csw, "Record.properties");
        assertTrue(record.exists());

        assertNotNull(store.getMapping("Record"));
        assertFalse(
                store.getMapping("Record")
                        .elements(PropertyPath.fromDotPath("identifier.value"))
                        .isEmpty());

        assertTrue(
                store.getMapping("Record")
                        .elements(PropertyPath.fromDotPath("format.value"))
                        .isEmpty());

        // On Linux and older versions of JDK last modification resolution is one second,
        // and we need the watcher to see the file as changed. Account for slow build servers too.
        PropertyFileWatcher watcher = store.watchers.get("Record");
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(
                        () -> {
                            try (PrintWriter out = new PrintWriter(new FileWriter(record, true))) {
                                out.println("\nformat.value='img/jpeg'");
                            }
                            return watcher.isStale();
                        });

        // mapping should be automatically reloaded now
        assertEquals(
                "img/jpeg",
                store.getMapping("Record").elements(PropertyPath.fromDotPath("format.value"))
                        .stream()
                        .findFirst()
                        .get()
                        .getContent()
                        .toString());
    }
}
