/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.dto;


import java.io.Serializable;

/**
 * Object that matches yaml structure.
 *
 * The part describes one mapping between the geoserver fields en the xml metadata from geonetwork.
 * The geonetwork field is described as an xpath expression.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class AttributeMapping implements Serializable {

    private static final long serialVersionUID = -2528238667226248014L;

    String geoserver;

    String geonetwork;

    FieldTypeEnum fieldType = FieldTypeEnum.TEXT;

    OccurenceEnum occurrence = OccurenceEnum.SINGLE;

    String typename;

    public AttributeMapping() {
    }

    public AttributeMapping(AttributeMapping other) {
        if (other != null) {
            geoserver = other.getGeoserver();
            geonetwork = other.getGeonetwork();
            fieldType = other.getFieldType();
            occurrence = other.getOccurrence();
            typename = other.getTypename();

        }
    }


    public String getGeoserver() {
        return geoserver;
    }

    public void setGeoserver(String geoserver) {
        this.geoserver = geoserver;
    }

    public String getGeonetwork() {
        return geonetwork;
    }

    public void setGeonetwork(String geonetwork) {
        this.geonetwork = geonetwork;
    }

    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public OccurenceEnum getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(OccurenceEnum occurrence) {
        this.occurrence = occurrence;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }
}
