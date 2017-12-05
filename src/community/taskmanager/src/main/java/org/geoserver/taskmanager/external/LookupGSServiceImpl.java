package org.geoserver.taskmanager.external;

import java.util.List;

import org.geoserver.taskmanager.util.LookupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LookupGSServiceImpl extends LookupServiceImpl<ExternalGS> {

    @Autowired
    public void setExternalGS(List<ExternalGS> externalGS) {
        setNamed(externalGS);
    }
}
