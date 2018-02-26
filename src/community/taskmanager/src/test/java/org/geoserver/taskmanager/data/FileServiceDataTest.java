package org.geoserver.taskmanager.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geoserver.taskmanager.AbstractTaskManagerTest;

import org.geoserver.taskmanager.data.impl.FileServiceImpl;
import org.geoserver.taskmanager.data.impl.S3FileServiceImpl;
import org.junit.Assert;
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
    private FileServiceRegistry fileServiceRegistry;

    @Test
    public void testFileRegistry() {
        Assert.assertEquals(2, fileServiceRegistry.getFileServiceNames().size());


        String firstService = fileServiceRegistry.getFileServiceNames().get(0);
        Assert.assertNotNull(fileServiceRegistry.getService(firstService));

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
        service.setRootFolder(FileUtils.getTempDirectoryPath()+"/fileservicetest/");

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
        S3FileServiceImpl service = new S3FileServiceImpl(
                "endpointurl",
                "username",
                "password"
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
    @Test
    public void testFileServiceS3CreateSubFolders() throws IOException {
        S3FileServiceImpl service = new S3FileServiceImpl(
                "endpointurl",
                "username",
                "password"
        );

        Path filename = Paths.get("test/newbucket/" + System.currentTimeMillis() + "-test.txt");

        Assert.assertFalse(service.checkFileExists(filename));

        service.create(filename, IOUtils.toInputStream("test the file service", "UTF-8"));
        service.delete(filename);
    }
}
