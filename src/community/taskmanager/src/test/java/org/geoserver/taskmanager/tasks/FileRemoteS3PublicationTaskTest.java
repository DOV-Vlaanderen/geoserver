/* (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.data.CatalogWriter;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.beans.TestTaskTypeImpl;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.ExternalGS;
import org.geoserver.taskmanager.fileservice.FileService;
import org.geoserver.taskmanager.schedule.BatchJobService;
import org.geoserver.taskmanager.util.LookupService;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * To run this test you should have a geoserver running on http://localhost:9090/geoserver.
 * 
 * @author Niels Charlier
 */
public class FileRemoteS3PublicationTaskTest extends AbstractTaskManagerTest {

    private final static Logger LOGGER = Logger.getLogger("FileRemotePublicationTaskTest");
    
    //configure these constants
    private static QName REMOTE_COVERAGE = new QName("gs", "mylayer", "gs");
    private static String REMOTE_COVERAGE_FILE_LOCATION = "test/salinity.tif";
    private static String REMOTE_COVERAGE_URL = "test://" + REMOTE_COVERAGE_FILE_LOCATION;
    private static String REMOTE_COVERAGE_OTHER_URL = "dovminio://test/salinity.tif";
    private static String REMOTE_COVERAGE_TYPE = "S3GeoTiff";
    
    private static final String ATT_LAYER = "layer";
    private static final String ATT_EXT_GS = "geoserver";
    private static final String ATT_FAIL = "fail";
    private static final String ATT_FILE = "file";
        
    @Autowired
    private LookupService<ExternalGS> extGeoservers;
        
    @Autowired
    private TaskManagerDao dao;
    
    @Autowired
    private TaskManagerFactory fac;
    
    @Autowired
    private TaskManagerDataUtil dataUtil;

    @Autowired
    private TaskManagerTaskUtil taskUtil;
    
    @Autowired
    private BatchJobService bjService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private LookupService<FileService> fileServices;

    private Configuration config;
    
    private Batch batch;
    
    @Override
    public boolean setupDataDirectory() throws Exception {             
        DATA_DIRECTORY.addWcs11Coverages();

        try {
            FileService fileService = fileServices.get("s3-test");
            Assume.assumeNotNull(fileService);
            Assume.assumeTrue("File exists on s3 service",
                    fileService.checkFileExists(REMOTE_COVERAGE_FILE_LOCATION));
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            Assume.assumeTrue("S3 service is configured and available", false);
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(CatalogWriter.COVERAGE_TYPE_KEY, REMOTE_COVERAGE_TYPE);
        params.put(CatalogWriter.COVERAGE_URL_KEY, REMOTE_COVERAGE_URL);
        DATA_DIRECTORY.addCustomCoverage(REMOTE_COVERAGE, params);

        return true;
    }
    
    @Before
    public void setupBatch() throws Exception { 
        Assume.assumeTrue(extGeoservers.get("mygs").getRESTManager().getReader().existGeoserver());
        
        config = fac.createConfiguration();  
        config.setName("my_config");
        config.setWorkspace("some_ws");
        
        Task task1 = fac.createTask();
        task1.setName("task1");
        task1.setType(FileRemotePublicationTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task1, FileRemotePublicationTaskTypeImpl.PARAM_LAYER, ATT_LAYER);
        dataUtil.setTaskParameterToAttribute(task1, FileRemotePublicationTaskTypeImpl.PARAM_EXT_GS, ATT_EXT_GS);
        dataUtil.setTaskParameterToAttribute(task1, FileRemotePublicationTaskTypeImpl.PARAM_FILE, ATT_FILE);
        dataUtil.addTaskToConfiguration(config, task1);
        
        config = dao.save(config);
        task1 = config.getTasks().get("task1");
        
        batch = fac.createBatch();
        
        batch.setName("my_batch");
        dataUtil.addBatchElement(batch, task1);
        
        batch = bjService.saveAndSchedule(batch);
    }

    @After
    public void clearDataFromDatabase() {
        if (batch != null) {
            dao.delete(batch);
        }
        if (config != null) {
            dao.delete(config);
        }
    }

    @Test
    public void testS3SuccessAndCleanup() throws SchedulerException, SQLException, MalformedURLException {
        Assume.assumeTrue(false);
        
        dataUtil.setConfigurationAttribute(config, ATT_LAYER, 
                REMOTE_COVERAGE.getPrefix() + ":" + REMOTE_COVERAGE.getLocalPart());
        dataUtil.setConfigurationAttribute(config, ATT_EXT_GS, "mygs");
        config = dao.save(config);
        
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(batch.getFullName())
                .startNow()        
                .build();
        scheduler.scheduleJob(trigger);
        
        while (scheduler.getTriggerState(trigger.getKey()) != TriggerState.COMPLETE
                && scheduler.getTriggerState(trigger.getKey()) != TriggerState.NONE) {}
        
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        
        assertTrue(restManager.getReader().existsCoveragestore(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart()));
        assertTrue(restManager.getReader().existsCoverage(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), 
                REMOTE_COVERAGE.getLocalPart()));
        assertTrue(restManager.getReader().existsLayer(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), true));
        
        assertTrue(taskUtil.cleanup(config));      
        
        assertFalse(restManager.getReader().existsCoveragestore(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart()));
        assertFalse(restManager.getReader().existsCoverage(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), 
                REMOTE_COVERAGE.getLocalPart()));
        assertFalse(restManager.getReader().existsLayer(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), true));
    }
    
    @Test
    public void testS3ReplaceUrlSuccessAndCleanup() throws SchedulerException, SQLException, MalformedURLException {

        dataUtil.setConfigurationAttribute(config, ATT_LAYER, 
                REMOTE_COVERAGE.getPrefix() + ":" + REMOTE_COVERAGE.getLocalPart());
        dataUtil.setConfigurationAttribute(config, ATT_FILE, REMOTE_COVERAGE_OTHER_URL);
        dataUtil.setConfigurationAttribute(config, ATT_EXT_GS, "mygs");
        config = dao.save(config);
        
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(batch.getFullName())
                .startNow()        
                .build();
        scheduler.scheduleJob(trigger);
        
        while (scheduler.getTriggerState(trigger.getKey()) != TriggerState.COMPLETE
                && scheduler.getTriggerState(trigger.getKey()) != TriggerState.NONE) {}
        
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();
        
        assertTrue(restManager.getReader().existsCoveragestore(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart()));
        assertEquals(REMOTE_COVERAGE_OTHER_URL, restManager.getReader().getCoverageStore("wcs", "DEM").getURL());
        assertTrue(restManager.getReader().existsCoverage(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), 
                REMOTE_COVERAGE.getLocalPart()));
        assertTrue(restManager.getReader().existsLayer(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), true));
        
        assertTrue(taskUtil.cleanup(config));      
        
        assertFalse(restManager.getReader().existsCoveragestore(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart()));
        assertFalse(restManager.getReader().existsCoverage(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), 
                REMOTE_COVERAGE.getLocalPart()));
        assertFalse(restManager.getReader().existsLayer(
                REMOTE_COVERAGE.getPrefix(), REMOTE_COVERAGE.getLocalPart(), true));
    }
    

}
