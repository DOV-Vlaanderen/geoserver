/* (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

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
import org.quartz.TriggerBuilder;
import org.quartz.Trigger.TriggerState;
import org.springframework.beans.factory.annotation.Autowired;

import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;

/**
 * To run this test you should have a geoserver running on http://localhost:9090/geoserver.
 * 
 * @author Niels Charlier
 */
public class FileRemotePublicationTaskTest extends AbstractTaskManagerTest {
    
    //configure these constants
    private static QName REMOTE_COVERAGE = new QName("gs", "mylayer", "gs");
    private static String REMOTE_COVERAGE_URL = "test://test/salinity.tif";
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
    
    private Configuration config;
    
    private Batch batch;
    
    private boolean s3present;
    
    @Override
    public boolean setupDataDirectory() throws Exception {             
        DATA_DIRECTORY.addWcs11Coverages();
        
        //TODO: test of kan verbinding gemaakt worden met de s3 service
        //en image is aanwezig
        s3present = false;
        if (s3present) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(CatalogWriter.COVERAGE_TYPE_KEY, REMOTE_COVERAGE_TYPE);
            params.put(CatalogWriter.COVERAGE_URL_KEY, REMOTE_COVERAGE_URL);
            DATA_DIRECTORY.addCustomCoverage(REMOTE_COVERAGE, params);
        }
        
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
    public void testSuccessAndCleanup() throws SchedulerException, SQLException, MalformedURLException {
        //set some metadata
        CoverageInfo ci = geoServer.getCatalog().getCoverageByName("DEM");
        ci.setTitle("my title ë");
        ci.setAbstract("my abstract ë");
        ci.getDimensions().get(0).setName("CUSTOM_DIMENSION");
        geoServer.getCatalog().save(ci);
        
        dataUtil.setConfigurationAttribute(config, ATT_LAYER, "DEM");
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
        
        assertTrue(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        assertTrue(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        assertTrue(restManager.getReader().existsLayer("wcs", "DEM", true));
        
        RESTCoverage cov = restManager.getReader().getCoverage("wcs", "DEM", "DEM");
        assertEquals(ci.getTitle(), cov.getTitle());
        assertEquals(ci.getAbstract(), cov.getAbstract());
        assertEquals(ci.getDimensions().get(0).getName(), 
                cov.getEncodedDimensionsInfoList().get(0).getName());
        
        assertTrue(taskUtil.cleanup(config));      
        
        assertFalse(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        assertFalse(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        assertFalse(restManager.getReader().existsLayer("wcs", "DEM", true));
    }
    
    @Test
    public void testRollback() throws SchedulerException, SQLException, MalformedURLException {
        Task task2 = fac.createTask();
        task2.setName("task2");
        task2.setType(TestTaskTypeImpl.NAME);
        dataUtil.setTaskParameterToAttribute(task2, TestTaskTypeImpl.PARAM_FAIL, ATT_FAIL);
        dataUtil.addTaskToConfiguration(config, task2);  
        
        dataUtil.setConfigurationAttribute(config, ATT_LAYER, "DEM");
        dataUtil.setConfigurationAttribute(config, ATT_EXT_GS, "mygs");
        dataUtil.setConfigurationAttribute(config, ATT_FAIL, Boolean.TRUE.toString());
        config = dao.save(config);
        task2 = config.getTasks().get("task2");
        dataUtil.addBatchElement(batch, task2);
        batch = bjService.saveAndSchedule(batch);
        
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(batch.getFullName())
                .startNow()        
                .build();
        scheduler.scheduleJob(trigger);
        
        while (scheduler.getTriggerState(trigger.getKey()) != TriggerState.COMPLETE
                && scheduler.getTriggerState(trigger.getKey()) != TriggerState.NONE) {}
        
        GeoServerRESTManager restManager = extGeoservers.get("mygs").getRESTManager();

        assertFalse(restManager.getReader().existsCoveragestore("wcs", "DEM"));
        assertFalse(restManager.getReader().existsCoverage("wcs", "DEM", "DEM"));
        assertFalse(restManager.getReader().existsLayer("wcs", "DEM", true));
    }
    
    @Test
    public void testS3SuccessAndCleanup() throws SchedulerException, SQLException, MalformedURLException {
        Assume.assumeTrue(s3present);
        
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
        Assume.assumeTrue(s3present);
        
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
