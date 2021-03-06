/**
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.engine.test.check;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.ArchivedActivityInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.test.WaitUntil;

@Deprecated
public final class CheckNbOfArchivedActivities extends WaitUntil {

    private final ProcessAPI processAPI;

    private final ProcessInstance processInstance;

    private final int nbActivities;

    private Set<ArchivedActivityInstance> result;

    private String activityState = null;

    @Deprecated
    public CheckNbOfArchivedActivities(final ProcessAPI processAPI, final int repeatEach, final int timeout, final boolean throwExceptions,
            final ProcessInstance processInstance, final int nbActivities) {
        super(repeatEach, timeout, throwExceptions);
        this.processInstance = processInstance;
        this.nbActivities = nbActivities;
        this.processAPI = processAPI;
    }

    @Deprecated
    public CheckNbOfArchivedActivities(final ProcessAPI processAPI, final int repeatEach, final int timeout, final boolean throwExceptions,
            final ProcessInstance processInstance, final int nbActivities, final String state) {
        this(processAPI, repeatEach, timeout, throwExceptions, processInstance, nbActivities);
        activityState = state;
    }

    @Override
    protected boolean check() {
        final List<ArchivedActivityInstance> activities = processAPI.getArchivedActivityInstances(processInstance.getId(), 0, 200,
                ActivityInstanceCriterion.NAME_ASC);
        result = new HashSet<ArchivedActivityInstance>(activities.size());
        // The number of activities is the one expected...

        if (activityState != null) {
            for (final ArchivedActivityInstance activityInstance : activities) {
                // ... and all states are equal to the expected one:
                if (activityInstance.getState().equals(activityState)) {
                    result.add(activityInstance);
                }
            }
        } else {
            result.addAll(activities);
        }
        final boolean check = result.size() == nbActivities;

        return check;// get activities with a state
    }

    public Set<ArchivedActivityInstance> getResult() {
        return result;
    }
}
