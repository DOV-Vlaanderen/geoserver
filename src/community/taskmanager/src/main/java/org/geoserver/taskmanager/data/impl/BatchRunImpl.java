/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.data.impl;

import java.util.ArrayList;
import java.util.Date;
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

import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.BatchRun;
import org.geoserver.taskmanager.data.Run;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table
public class BatchRunImpl extends BaseImpl implements BatchRun {

    private static final long serialVersionUID = 2468505054020768482L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "batch")
    private BatchImpl batch;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = RunImpl.class, mappedBy = "batchRun",
            cascade = CascadeType.ALL)
    @OrderBy("start")
    @Fetch(FetchMode.SUBSELECT)
    List<Run> runs = new ArrayList<Run>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BatchImpl getBatch() {
        return batch;
    }

    @Override
    public void setBatch(Batch batch) {
        this.batch = (BatchImpl) batch;
    }

    @Override
    public List<Run> getRuns() {
        return runs;
    }

    @Override
    public Date getStart() {
        return getRuns().isEmpty() ? null : getRuns().get(0).getStart();
    }

    @Override
    public Date getEnd() {
        return getRuns().isEmpty() ? null : getRuns().get(getRuns().size() - 1).getEnd();
    }

    @Override
    public Run.Status getStatus() {
        if (getRuns().isEmpty()) {
            return null;
        } else {
            for (int i = getRuns().size() - 1; i >= 0; i--) {
                if (getRuns().get(i).getStatus() != Run.Status.COMMITTED) {
                    return getRuns().get(i).getStatus();
                }
            }
            return Run.Status.COMMITTED;
        }
    }

    @Override
    public String getMessage() {
        if (getRuns().isEmpty()) {
            return null;
        } else {
            for (int i = getRuns().size() - 1; i >= 0; i--) {
                if (getRuns().get(i).getMessage() != null) {
                    return getRuns().get(i).getMessage();
                }
            }
            return null;
        }
    }
}