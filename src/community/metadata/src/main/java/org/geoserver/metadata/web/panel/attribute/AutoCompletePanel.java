/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.web.panel.attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AutoCompletePanel extends Panel {

    private static final long serialVersionUID = -1829729746678003578L;

    public AutoCompletePanel(String id, IModel<String> model, List<String> values) {

        super(id, model);

        add(
                new AutoCompleteTextField<String>("autoComplete", model) {

                    private static final long serialVersionUID = 7742400754591550452L;

                    @Override
                    protected Iterator<String> getChoices(String input) {
                        List<String> result = new ArrayList<String>();
                        for (String value : values) {
                            if (value.toLowerCase().startsWith(input.toLowerCase())) {
                                result.add(value);
                            }
                        }
                        if (result.isEmpty()) {
                            return values.iterator();
                        } else {
                            return result.iterator();
                        }
                    }
                });
    }
}
