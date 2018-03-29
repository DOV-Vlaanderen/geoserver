/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.fileservice;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geoserver.taskmanager.AbstractTaskManagerTest;

import org.geoserver.taskmanager.fileservice.impl.FileServiceImpl;
import org.geoserver.taskmanager.fileservice.impl.S3FileServiceImpl;
import org.geoserver.taskmanager.util.LookupService;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;


/**
 * Test data methods.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class FileServiceDataTest extends AbstractTaskManagerTest {

    private final static Logger LOGGER = Logger.getLogger("FileServiceRegistryImpl");

    @Autowired
    LookupService<FileService> fileServiceRegistry;

    @Test
    public void testFileRegistry() {
        Assert.assertEquals(3, fileServiceRegistry.names().size());
        
        FileService fs = fileServiceRegistry.get("s3-test");
        Assert.assertNotNull(fs);
        Assert.assertTrue(fs instanceof S3FileServiceImpl);
        Assert.assertEquals("http://dov-minio-s3-on-1.vm.cumuli.be:9000",
                ((S3FileServiceImpl) fs).getEndpoint());

        fs = fileServiceRegistry.get("Temporary Directory");
        Assert.assertNotNull(fs);
        Assert.assertTrue(fs instanceof FileServiceImpl);
        Assert.assertEquals("/tmp", ((FileServiceImpl) fs).getRootFolder());
    }

    @Test
    public void testFileService() throws IOException {
        FileServiceImpl service = new FileServiceImpl();
        service.setRootFolder(FileUtils.getTempDirectoryPath());

        Path filename = Paths.get(System.currentTimeMillis() + "-test.txt");
        Path path = Paths.get(FileUtils.getTempDirectoryPath() + "/" + filename);

        Assert.assertFalse(Files.exists(path));
        String content = "test the file service";
        service.create(filename, IOUtils.toInputStream(content, "UTF-8"));
        Assert.assertTrue(Files.exists(path));

        boolean fileExists = service.checkFileExists(filename);
        Assert.assertTrue(fileExists);

        String actualContent = IOUtils.toString(service.read(filename));
        Assert.assertEquals(content, actualContent);

        service.delete(filename);
        Assert.assertFalse(Files.exists(path));

    }

    @Test
    public void testFileServiceCreateSubFolders() throws IOException {
        FileServiceImpl service = new FileServiceImpl();
        service.setRootFolder(FileUtils.getTempDirectoryPath() + "/fileservicetest/");

        Path filename = Paths.get("subfolder/" + System.currentTimeMillis() + "-test.txt");
        Path path = Paths.get(FileUtils.getTempDirectoryPath() + "/fileservicetest/" + filename);

        Assert.assertFalse(Files.exists(path));
        service.create(filename, IOUtils.toInputStream("test the file service", "UTF-8"));
        Assert.assertTrue(Files.exists(path));

        boolean fileExists = service.checkFileExists(filename);
        Assert.assertTrue(fileExists);

        service.delete(filename);
        Assert.assertFalse(Files.exists(path));

        List<Path> folders = service.listSubfolders();
        Assert.assertEquals(1, folders.size());
    }

    /**
     * Enable this test if you have acces to aws compatible service.
     *
     * @throws IOException
     */
    @Test
    public void testFileServiceS3() throws IOException {
        S3FileServiceImpl service = getS3FileService();

        String filename = +System.currentTimeMillis() + "-test.txt";
        Path filenamePath = Paths.get("test", filename);

        Assert.assertFalse(service.checkFileExists(filenamePath));

        String content = "test the file service";
        String fileUri = service.create(filenamePath, IOUtils.toInputStream(content, "UTF-8"));
        Assert.assertEquals("alias://test/" + filename, fileUri);


        boolean fileExists = service.checkFileExists(filenamePath);
        Assert.assertTrue(fileExists);

        String actualContent = IOUtils.toString(service.read(filenamePath));
        Assert.assertEquals(content, actualContent);

        service.delete(filenamePath);

        Assert.assertFalse(service.checkFileExists(filenamePath));
    }


    /**
     * Enable this test if you have acces to aws compatible service.
     *
     * @throws IOException
     */
    @Test
    public void testFileServiceS3CreateSubFolders() throws IOException {
        S3FileServiceImpl service = getS3FileService();

        String filename = System.currentTimeMillis() + "-test.txt";
        Path filenamePath = Paths.get("newbucket", filename);

        Assert.assertFalse(service.checkFileExists(filenamePath));

        String location = service.create(filenamePath, IOUtils.toInputStream("test the file service", "UTF-8"));
        Assert.assertEquals("alias://newbucket/" + filename, location);
        service.delete(filenamePath);
    }

    /**
     * Add the properties to your S3 service here.
     * @return
     */
    private S3FileServiceImpl getS3FileService() {
        S3FileServiceImpl s3FileService = new S3FileServiceImpl(
                "your-s3-service-uri",
                "your-s3-user",
                "your-s3-password",
                "alias"
        );
        List<Path> folders = null;
        try {
            folders = s3FileService.listSubfolders();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
        Assume.assumeNotNull(folders);

        return s3FileService;
    }
}
