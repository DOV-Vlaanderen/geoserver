/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

public class DropDownPanel extends Panel {

    private static final long serialVersionUID = -1829729746678003578L;
    
    public DropDownPanel(String id, IModel<String> model, List<String> values) {

        super(id, model);

        add(createDropDown(model, values));


    }

    private DropDownChoice<String> createDropDown(IModel<String> model, List<String> values) {
        return new DropDownChoice<String>("dropdown", model, values);
    }

}
