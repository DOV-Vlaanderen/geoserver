/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.web.wicket.ParamResourceModel;

import java.util.ArrayList;
import java.util.List;

public class DropDownPanel extends Panel {

    private static final long serialVersionUID = -1829729746678003578L;
    
    public DropDownPanel(String id, IModel<String> model, List<String> values) {

        super(id, model);

        add(createDropDown(values));


    }

    private DropDownChoice<String> createDropDown(List<String> values) {
        return new DropDownChoice<String>("dropdown", new IModel<String>() {
            public String option;

            @Override
            public String getObject() {
                return option;
            }

            @Override
            public void setObject(String t) {
                option = t;
            }

            @Override
            public void detach() {
            }
        }, values);
    }

}
