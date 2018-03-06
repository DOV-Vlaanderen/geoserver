package org.geoserver.taskmanager.data;

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Test data methods.
 *
 * @author Niels Charlier
 * @author Timothy De Bock
 */
public class FileServiceDataTest extends AbstractTaskManagerTest {

    @Autowired
    LookupService<FileService> fileServiceRegistry;

    @Test
    public void testFileRegistry() {
        Assert.assertEquals(2, fileServiceRegistry.names().size());


        String firstService = fileServiceRegistry.names().iterator().next();
        Assert.assertNotNull(fileServiceRegistry.get(firstService));

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
    @Ignore
    @Test
    public void testFileServiceS3() throws IOException {
        S3FileServiceImpl service = new S3FileServiceImpl(
                "endpoint url",
                "xxx",
                "xxx",
                "alias"
        );

        Path filename = Paths.get("test/" + System.currentTimeMillis() + "-test.txt");

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
        Path filenamePath = Paths.get("newbucket/" + filename);

        Assert.assertFalse(service.checkFileExists(filenamePath));

        String location = service.create(filenamePath, IOUtils.toInputStream("test the file service", "UTF-8"));
        Assert.assertEquals("alias://newbucket/" + filename, location);
        service.delete(filenamePath);
    }
}
