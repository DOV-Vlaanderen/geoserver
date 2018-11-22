/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;
import org.geoserver.metadata.data.dto.impl.MetadataAttributeConfigurationImpl;

/**
 * Object that matches yaml structure.
 *
 * <p>The configuration descibes one field for the gui.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
@JsonDeserialize(as = MetadataAttributeConfigurationImpl.class)
public interface MetadataAttributeConfiguration extends Serializable {

    public static final String PREFIX = "metadata.generated.form.";

    public String getKey();

    public void setKey(String key);

    public String getLabel();

    public void setLabel(String label);

    public FieldTypeEnum getFieldType();

    public void setFieldType(FieldTypeEnum fieldType);

    public List<String> getValues();

    public void setValues(List<String> values);

    public String getTypename();

    public void setTypename(String typename);

    public OccurenceEnum getOccurrence();

    public void setOccurrence(OccurenceEnum occurrence);
}
