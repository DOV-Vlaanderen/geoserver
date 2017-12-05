package org.geoserver.taskmanager.external;

import java.util.List;

import org.geoserver.taskmanager.util.LookupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LookupDbSourceServiceImpl extends LookupServiceImpl<DbSource> {

    @Autowired
    public void setDbSources(List<DbSource> dbSources) {
        setNamed(dbSources);
    }
}
