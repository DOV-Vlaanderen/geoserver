package org.geoserver.taskmanager.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.geoserver.taskmanager.data.Run.Status;

public interface BatchRun extends Serializable, Identifiable {

    Batch getBatch();

    void setBatch(Batch batch);

    List<Run> getRuns();

    default Date getStart() {
        return getRuns().isEmpty() ? null : getRuns().get(0).getStart();
    }

    default Date getEnd() {
        return getRuns().isEmpty() ? null : getRuns().get(getRuns().size() - 1).getEnd();
    }

    default Status getStatus() {
        return getRuns().isEmpty() ? null : getRuns().get(getRuns().size() - 1).getStatus();
    }

    default String getMessage() {
        return getRuns().isEmpty() ? null : getRuns().get(getRuns().size() - 1).getMessage();
    }

}
