/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.ComplexMetadataMap;
import org.geoserver.metadata.data.ComplexMetadataAttribute;

public class ComplexMetadataMapImpl implements ComplexMetadataMap  {

    private static final long serialVersionUID = 1857277796433431947L;

    private static final String PATH_SEPARATOR = "_";
        
    /**
     * the underlying flat map
     */
    private MetadataMap delegate;
    
    /**
     * for submaps
     */
    private String[] basePath;

    
    /**
     * for submaps
     */
    private ComplexMetadataIndexReference baseIndexRef;
    
    /**
     * indexes, helps attributes to auto-update their indexes after delete
     */
    private HashMap<String, ArrayList<ComplexMetadataIndexReference>> indexes = 
            new HashMap<String, ArrayList<ComplexMetadataIndexReference>>();

    public ComplexMetadataMapImpl(MetadataMap delegate) {
        this.delegate = delegate;
        this.basePath = new String[] {};
        this.baseIndexRef = new ComplexMetadataIndexReference(new int[] {});
    }
    
    protected ComplexMetadataMapImpl(ComplexMetadataMapImpl parent,
            String[] basePath, ComplexMetadataIndexReference baseIndexRef) {        
        this.delegate = parent.delegate;
        this.indexes = parent.indexes;
        this.basePath = basePath;
        this.baseIndexRef = baseIndexRef;
    }

    @Override
    public <T extends Serializable> 
        ComplexMetadataAttribute<T> get(Class<T> clazz,
                String path, int... index) {
        String strPath = String.join(PATH_SEPARATOR, 
                concat(basePath, path));
        int[] fullIndex = concat(baseIndexRef.getIndex(), index);
        return new ComplexMetadataAttributeImpl<T>(delegate, 
                strPath,
                getOrCreateIndex(strPath, fullIndex),
                clazz);
    }

    @Override
    public ComplexMetadataMap subMap(String path, 
            int... index) {
        String[] fullPath = concat(basePath, path);
        return new ComplexMetadataMapImpl(this, fullPath,
                getOrCreateIndex(String.join(PATH_SEPARATOR, fullPath),
                        concat(baseIndexRef.getIndex(), index)));
    }
    
    protected ComplexMetadataIndexReference getOrCreateIndex(
            String strPath, int[] index) {
        ComplexMetadataIndexReference result = null;
        ArrayList<ComplexMetadataIndexReference> list = indexes.get(strPath);
        if (list == null) {
            list = new ArrayList<>();
            indexes.put(strPath, list);
        } else {
            for (ComplexMetadataIndexReference item : list) {
                if (Arrays.equals(item.getIndex(), index)) {
                    result = item;
                    break;                     
                }
            }
        }
        if (result == null) {
            result = new ComplexMetadataIndexReference(index);
            list.add(result);
        }
        return result;
    }

    @Override
    public int size(String path, int... index) {
        String strPath = String.join(PATH_SEPARATOR, 
                concat(basePath, path));
        int[] fullIndex = concat(baseIndexRef.getIndex(), index);
        if (delegate.containsKey(strPath)) {
            return sizeInternal(strPath, index);
        } else {
            //subtype
            int size = 0;
            for (String key : delegate.keySet()) {
                if (key.startsWith(strPath + PATH_SEPARATOR)) {
                    size = Math.max(size, sizeInternal(key, fullIndex));
                }
            }        
            return size;
        }
    }
    
    @SuppressWarnings("unchecked")
    private int sizeInternal(String path, int[] index) {
        Object object = delegate.get(path);
        for (int i = 0; i < index.length; i++) {
            if (object instanceof ArrayList<?>) {
                object = ((ArrayList<Object>) object).get(index[i]);
            } else {
                return object == null ? 0 : 1;
            }
        }
        if (object instanceof ArrayList<?>) {
            return ((ArrayList<Object>) object).size();
        } else {
            return object == null ? 0 : 1;
        }
    }

    @Override
    public void delete(String path, int... index) {
        String strPath = String.join(PATH_SEPARATOR, 
                concat(basePath, path));
        int[] fullIndex = concat(baseIndexRef.getIndex(), index);
        if (fullIndex.length > 0) {
            deleteFromList(strPath, fullIndex);
            for (String key : delegate.keySet()) {
                if (key.startsWith(strPath + PATH_SEPARATOR)) {
                    deleteFromList(key, fullIndex);
                }
            }
        } else {
            delegate.remove(strPath);
            Iterator<String> it = delegate.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key.startsWith(strPath + PATH_SEPARATOR)) {
                    it.remove();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteFromList(String path, int[] index) {
        Object object = delegate.get(path);
        for (int i = 0; i < index.length - 1; i++) {
            if (object instanceof List<?>) {
                object = ((List<Object>) object).get(index[i]);
            } else {
                return;
            }
        }
        if (object instanceof List<?>) {
            ((List<Object>) object).remove(index[index.length - 1]);
        }
        // update indexes
        ArrayList<ComplexMetadataIndexReference> list = indexes.get(path);
        if (list != null) {
            Iterator<ComplexMetadataIndexReference> it = list.iterator();
            while (it.hasNext()) {
                ComplexMetadataIndexReference item = it.next();
                if (item.getIndex().length >= index.length) {
                    if (item.getIndex()[index.length - 1] == index[index.length - 1]) {
                        item.setIndex(null);
                        it.remove();
                    } else if (item.getIndex()[index.length - 1] > index[index.length - 1]) {
                        item.getIndex()[index.length - 1]--;
                    }
                }
            }
        }
    }
    
    protected static String[] concat(String[] first, String... second) {
        return (String[]) ArrayUtils.addAll(first, second);
    }
    
    protected static int[] concat(int[] first, int... second) {
        return ArrayUtils.addAll(first, second);
    }

}
