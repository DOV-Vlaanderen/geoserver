/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.data.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.BatchElement;
import org.geoserver.taskmanager.data.Configuration;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

/**
 * @author Niels Charlier
 *
 */
@Entity 
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "configuration", "removeStamp" }) })
@FilterDef(name="activeElementFilter", defaultCondition="removeStamp = 0")
public class BatchImpl extends BaseImpl implements Batch {

    private static final long serialVersionUID = 3321130631692899821L;

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @OneToMany(fetch = FetchType.EAGER, targetEntity = BatchElementImpl.class, mappedBy = "batch", 
            cascade = CascadeType.ALL)
    @OrderBy("index, id")
    @Filter(name="activeElementFilter")
    @Fetch(FetchMode.SUBSELECT)
    List<BatchElement> elements = new ArrayList<BatchElement>();
    
    @Column
    String workspace;
        
    @Column(nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "configuration")
    private ConfigurationImpl configuration;
    
    @Column
    String description;
    
    @Column(nullable = true)
    String frequency;
    
    @Column(nullable = false)
    Boolean enabled = true;

    @Column(nullable = false)
    Long removeStamp = 0L;
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public List<BatchElement> getElements() {
        return elements;
    }

    @Override
    public String getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    @Override
    public String getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = (ConfigurationImpl) configuration;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setRemoveStamp(long removeStamp) {
        this.removeStamp = removeStamp;
    }

    @Override
    public long getRemoveStamp() {
        return removeStamp;
    }
}
