/* (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.beans;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.springframework.stereotype.Component;
import org.geoserver.taskmanager.web.ConfigurationPage;
import org.geoserver.taskmanager.web.action.Action;

@Component 
public class DummyAction implements Action {

    private static final long serialVersionUID = 2055260073253741911L;

    @Override
    public String getName() {
        return "actionDummy";
    }

    @Override
    public void execute(ConfigurationPage onPage, AjaxRequestTarget target, IModel<String> valueModel) {

    }
}
