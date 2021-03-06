/**
 * Copyright (C) 2014 BonitaSoft S.A.
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
 */
package org.bonitasoft.engine.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.identity.CustomUserInfo;
import org.bonitasoft.engine.identity.CustomUserInfoValue;
import org.bonitasoft.engine.identity.IdentityService;
import org.bonitasoft.engine.identity.SIdentityException;
import org.bonitasoft.engine.identity.model.SCustomUserInfoDefinition;
import org.bonitasoft.engine.identity.model.SCustomUserInfoValue;
import org.bonitasoft.engine.persistence.FilterOption;
import org.bonitasoft.engine.persistence.OrderByOption;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.persistence.SBonitaSearchException;

/**
 * @author Vincent Elcrin
 */
public class CustomUserInfoAPI {

    private IdentityService service;

    private final CustomUserInfoConverter converter = new CustomUserInfoConverter();

    public CustomUserInfoAPI(IdentityService service) {
        this.service = service;
    }

    public List<CustomUserInfo> list(long userId, int startIndex, int maxResult) throws SIdentityException, SBonitaSearchException {
        List<SCustomUserInfoDefinition> definitions = service.getCustomUserInfoDefinitions(startIndex, maxResult);
        if(definitions.size() == 0) {
            return Collections.emptyList();
        }
        Map<Long, CustomUserInfoValue> values = transform(searchCorrespondingValues(userId, definitions));
        List<CustomUserInfo> info = new ArrayList<CustomUserInfo>();
        for (SCustomUserInfoDefinition definition : definitions) {
            info.add(new CustomUserInfo(
                    userId,
                    converter.convert(definition),
                    values.get(definition.getId())));
        }
        return info;
    }

    private Map<Long, CustomUserInfoValue> transform(List<SCustomUserInfoValue> values) {
        Map<Long, CustomUserInfoValue> map = new HashMap<Long, CustomUserInfoValue>();
        for (SCustomUserInfoValue value : values) {
            map.put(value.getDefinitionId(), converter.convert(value));
        }
        return map;
    }

    private List<SCustomUserInfoValue> searchCorrespondingValues(long userId, List<SCustomUserInfoDefinition> definitions) throws SBonitaSearchException {
        return service.searchCustomUserInfoValue(new QueryOptions(
                0,
                definitions.size(),
                Collections.<OrderByOption> emptyList(),
                Arrays.asList(
                        new FilterOption(SCustomUserInfoValue.class, "userId", userId),
                        new FilterOption(SCustomUserInfoValue.class, "definitionId")
                                .in(getIds(definitions))),
                null));
    }

    private List<Long> getIds(List<SCustomUserInfoDefinition> definitions) {
        List<Long> ids = new ArrayList<Long>(definitions.size());
        for (SCustomUserInfoDefinition definition : definitions) {
            ids.add(definition.getId());
        }
        return ids;
    }
}
