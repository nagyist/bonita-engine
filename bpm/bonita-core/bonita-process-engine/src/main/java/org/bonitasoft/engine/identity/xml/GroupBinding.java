/**
 * Copyright (C) 2012-2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.identity.xml;

import java.util.Map;

import org.bonitasoft.engine.identity.GroupCreator;
import org.bonitasoft.engine.xml.ElementBinding;

/**
 * @author Yanyan Liu
 * @author Matthieu Chaffotte
 */
public class GroupBinding extends ElementBinding {

    private GroupCreator groupCreator;

    public GroupBinding() {
        super();
    }

    @Override
    public void setAttributes(final Map<String, String> attributes) {
        final String name = attributes.get(OrganizationMappingConstants.NAME);
        groupCreator = new GroupCreator(name);
        final String parentPath = attributes.get(OrganizationMappingConstants.PARENT_PATH);
        groupCreator.setParentPath(parentPath);
    }

    @Override
    public void setChildElement(final String name, final String value, final Map<String, String> attributes) {
        if (OrganizationMappingConstants.DISPLAY_NAME.equals(name)) {
            groupCreator.setDisplayName(value);
        } else if (OrganizationMappingConstants.DESCRIPTION.equals(name)) {
            groupCreator.setDescription(value);
        } else if (OrganizationMappingConstants.ICON_NAME.equals(name)) {
            groupCreator.setIconName(value);
        } else if (OrganizationMappingConstants.ICON_PATH.equals(name)) {
            groupCreator.setIconPath(value);
        }
    }

    @Override
    public void setChildObject(final String name, final Object value) {
    }

    @Override
    public Object getObject() {
        return groupCreator;
    }

    @Override
    public String getElementTag() {
        return OrganizationMappingConstants.GROUP;
    }

}
