/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.fileservice;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geoserver.taskmanager.AbstractTaskManagerTest;

import org.geoserver.taskmanager.fileservice.FileService;
import org.geoserver.taskmanager.fileservice.impl.FileServiceImpl;
import org.geoserver.taskmanager.fileservice.impl.S3FileServiceImpl;
import org.geoserver.taskmanager.util.LookupService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * Test data methods.
 *
 * @author Timothy De Bock
 */
public class FileServiceDataTest extends AbstractTaskManagerTest {

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

        String filename = System.currentTimeMillis() + "-test.txt";
        String path = FileUtils.getTempDirectoryPath() + "/" + filename;

        Assert.assertFalse(Files.exists(Paths.get(path)));
        String content = "test the file service";
        service.create(filename, IOUtils.toInputStream(content, "UTF-8"));
        Assert.assertTrue(Files.exists(Paths.get(path)));

        boolean fileExists = service.checkFileExists(filename);
        Assert.assertTrue(fileExists);

        String actualContent = IOUtils.toString(service.read(filename));
        Assert.assertEquals(content, actualContent);

        service.delete(filename);
        Assert.assertFalse(Files.exists(Paths.get(path)));

    }

    @Test
    public void testFileServiceCreateSubFolders() throws IOException {
        FileServiceImpl service = new FileServiceImpl();
        service.setRootFolder(FileUtils.getTempDirectoryPath() + "/fileservicetest/");

        String filename = "subfolder/" + System.currentTimeMillis() + "-test.txt";
        String path = FileUtils.getTempDirectoryPath() + "/fileservicetest/" + filename;

        Assert.assertFalse(Files.exists(Paths.get(path)));
        service.create(filename, IOUtils.toInputStream("test the file service", "UTF-8"));
        Assert.assertTrue(Files.exists(Paths.get(path)));

        boolean fileExists = service.checkFileExists(filename);
        Assert.assertTrue(fileExists);

        service.delete(filename);
        Assert.assertFalse(Files.exists(Paths.get(path)));

        List<String> folders = service.listSubfolders();
        Assert.assertEquals(1, folders.size());
    }

    /**
     * Enable this test if you have acces to aws compatible service.
     *
     * @throws IOException
     */
    @Ignore
    @Test
    public void testFileServiceS3() throws IOException {
        S3FileServiceImpl service = new S3FileServiceImpl(
                "endpoint url",
                "xxx",
                "xxx",
                "alias"
        );

        String filename = "test/" + System.currentTimeMillis() + "-test.txt";

        Assert.assertFalse(service.checkFileExists(filename));

        String content = "test the file service";
        service.create(filename, IOUtils.toInputStream(content, "UTF-8"));

        boolean fileExists = service.checkFileExists(filename);
        Assert.assertTrue(fileExists);

        String actualContent = IOUtils.toString(service.read(filename));
        Assert.assertEquals(content, actualContent);

        service.delete(filename);

        Assert.assertFalse(service.checkFileExists(filename));
    }

    /**
     * Enable this test if you have acces to aws compatible service.
     *
     * @throws IOException
     */
    @Ignore
    @Test
    public void testFileServiceS3CreateSubFolders() throws IOException {
        S3FileServiceImpl service = new S3FileServiceImpl(
                "xxx",
                "xxx",
                "xxx",
                "alias"
        );

        String filename = System.currentTimeMillis() + "-test.txt";
        String filenamePath = "newbucket/" + filename;

        Assert.assertFalse(service.checkFileExists(filenamePath));

        String location = service.create(filenamePath, IOUtils.toInputStream("test the file service", "UTF-8"));
        Assert.assertEquals("alias://newbucket/" + filename, location);
        service.delete(filenamePath);
    }
}
