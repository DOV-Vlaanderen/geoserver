/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.mapper;

import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.model.AttributeInput;
import org.geoserver.metadata.data.service.AbstractMetadataTest;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * Test data methods.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class MapperTest extends AbstractMetadataTest {

    private final static Logger LOGGER = Logging.getLogger(MapperTest.class);

    ViewObjectMetadataMapper mapper = new ViewObjectMetadataMapper();

    @Test
    public void ToPersited() throws IOException {

        IModel<MetadataMap> metadataModel = createViewModel();
        mapper.toPersistedModel(metadataModel);

        MetadataMap actual = metadataModel.getObject();
        MetadataMap expected = createPersistedMap();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void ToView() throws IOException {

        IModel<MetadataMap> metadataModel = createPersitedModel();
        mapper.toViewModel(metadataModel);

        MetadataMap actual = metadataModel.getObject();
        MetadataMap expected = createViewMap();

        //TODO equals and hascode overschrijven zodat het werkt
        //Assert.assertEquals(expected, actual);
    }

    private IModel<MetadataMap> createViewModel() {
        MetadataMap map = createViewMap();
        return new IModel<MetadataMap>() {
            @Override
            public MetadataMap getObject() {
                return map;
            }

            @Override
            public void setObject(MetadataMap metadataMap) {

            }

            @Override
            public void detach() {

            }
        };
    }

    private MetadataMap createViewMap() {
        MetadataMap map = new MetadataMap();
        //String
        map.put("field-single", "single value string");

        // list String
        ArrayList<Object> fieldAsList = new ArrayList<>();
        fieldAsList.add(new AttributeInput(null, "field list value 1"));
        fieldAsList.add(new AttributeInput(null, "field list value 2"));
        map.put("field-as-list", fieldAsList);

        //Object
        map.put("object-field_field1", "object field 01");
        map.put("object-field_field2", "object field 02");

        //list object
        ArrayList<Object> objectAsList = new ArrayList<>();

        MetadataMap objectMap01 = new MetadataMap();
        objectMap01.put("object-as-list_field 01", "object list value 1");
        objectMap01.put("object-as-list_field 02", "object list value other 1");
        objectAsList.add(new AttributeInput(null, objectMap01));

        MetadataMap objectMap02 = new MetadataMap();
        objectMap02.put("object-as-list_field 01", "object list value 2");
        objectMap02.put("object-as-list_field 02", "object list value other 2");
        objectAsList.add(new AttributeInput(null, objectMap02));

        map.put("object-as-list", objectAsList);


        return map;
    }


    private IModel<MetadataMap> createPersitedModel() {
        MetadataMap map = createPersistedMap();
        return new IModel<MetadataMap>() {
            @Override
            public MetadataMap getObject() {
                return map;
            }

            @Override
            public void setObject(MetadataMap metadataMap) {

            }

            @Override
            public void detach() {

            }
        };
    }

    private MetadataMap createPersistedMap() {
        MetadataMap map = new MetadataMap();
        //String
        map.put("field-single", "single value string");

        // list String
        ArrayList<Object> fieldAsList = new ArrayList<>();
        fieldAsList.add("field list value 1");
        fieldAsList.add("field list value 2");
        map.put("field-as-list", fieldAsList);

        //Object
        map.put("object-field_field1", "object field 01");
        map.put("object-field_field2", "object field 02");

        //list object
        ArrayList<Object> fieldAsListObjectValue01 = new ArrayList<>();
        fieldAsListObjectValue01.add("object list value 1");
        fieldAsListObjectValue01.add("object list value 2");
        map.put("object-as-list_field 01", fieldAsListObjectValue01);

        ArrayList<Object> fieldAsListObjectValue02 = new ArrayList<>();
        fieldAsListObjectValue02.add("object list value other 1");
        fieldAsListObjectValue02.add("object list value other 2");
        map.put("object-as-list_field 02", fieldAsListObjectValue02);
        return map;
    }
}
