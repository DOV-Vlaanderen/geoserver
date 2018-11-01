/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.Date;

public class DateFieldPanel extends Panel {

    private static final long serialVersionUID = -1829729746678003578L;

    public DateFieldPanel(String id, IModel<Date> model) {
        super(id, model);
        DateTextField dateTextField = new DateTextField("dateField", "dd/MM/yyyy");
        dateTextField.add(new DatePicker());
        dateTextField.setModel(model);
        add(dateTextField);
    }


}
