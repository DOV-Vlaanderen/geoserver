/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.util;

import java.util.Set;

public interface LookupService<T extends Named> {

    public T get(String name);

    public <S extends T> S get(String name, Class<S> clazz);
    
    public Set<String> names();
}
