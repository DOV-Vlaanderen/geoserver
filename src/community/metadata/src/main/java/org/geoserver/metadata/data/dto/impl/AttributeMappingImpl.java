/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto.impl;

import org.geoserver.metadata.data.dto.AttributeMapping;
import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.dto.OccurenceEnum;

/**
 * Object that matches yaml structure.
 *
 * <p>The part describes one mapping between the geoserver fields en the xml metadata from
 * geonetwork. The geonetwork field is described as an xpath expression.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class AttributeMappingImpl implements AttributeMapping {

    private static final long serialVersionUID = -2528238667226248014L;

    String geoserver;

    String geonetwork;

    FieldTypeEnum fieldType = FieldTypeEnum.TEXT;

    OccurenceEnum occurrence = OccurenceEnum.SINGLE;

    String typename;

    public AttributeMappingImpl() {}

    public AttributeMappingImpl(AttributeMapping other) {
        if (other != null) {
            geoserver = other.getGeoserver();
            geonetwork = other.getGeonetwork();
            fieldType = other.getFieldType();
            occurrence = other.getOccurrence();
            typename = other.getTypename();
        }
    }

    @Override
    public String getGeoserver() {
        return geoserver;
    }

    @Override
    public void setGeoserver(String geoserver) {
        this.geoserver = geoserver;
    }

    @Override
    public String getGeonetwork() {
        return geonetwork;
    }

    @Override
    public void setGeonetwork(String geonetwork) {
        this.geonetwork = geonetwork;
    }

    @Override
    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    @Override
    public void setFieldType(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public OccurenceEnum getOccurrence() {
        return occurrence;
    }

    @Override
    public void setOccurrence(OccurenceEnum occurrence) {
        this.occurrence = occurrence;
    }

    @Override
    public String getTypename() {
        return typename;
    }

    @Override
    public void setTypename(String typename) {
        this.typename = typename;
    }
}
