/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/
package org.bonitasoft.engine.command;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.impl.ProcessStarter;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.command.system.CommandWithParameters;
import org.bonitasoft.engine.execution.AdvancedStartProcessValidator;
import org.bonitasoft.engine.operation.Operation;
import org.bonitasoft.engine.service.TenantServiceAccessor;

/**
 * This command start the process in the specified activity(ies).
 * Parameters:
 * - started_by: the user id (long) used as process starter. It's a mandatory parameter.
 * - process_definition_id: the process definition id (long) of process to start. It's a mandatory parameter.
 * - activity_names: the name of activities (List<String>) where the execution must starts. It's a mandatory parameter.
 * - operations: the operations (List<Operation>) to execute when starting the process (set variables and documents). It's an optional parameter.
 * - context: the context (Map<String, Serializable>) to be used during operations execution. It's an optional parameter.
 * 
 * Limitations:
 * - It is not possible to start the execution of a process from a gateway, a boundary event or an event sub-process
 * - The only use case where it's possible to start a process in several parallel branch is in the case where all these branches will merged with a exclusive
 * gateway. In all others cases the process must be started when there is only one active branch. Examples:
 * start -> step1 -> gateway1 -> (step2 || step3) -> gateway2 -> step4 -> end
 * - Always Ok: start at "start" or "step1" or "step4" or "end"
 * - OK if gateway2 is an exclusive gateway: start at "step2" and "step3"
 * - All other start points are invalid. 
 * 
 * @author Vincent Elcrin
 */
public class AdvancedStartProcessCommand extends CommandWithParameters {

    public static final String STARTED_BY = "started_by";

    public static final String PROCESS_DEFINITION_ID = "process_definition_id";

    public static final String ACTIVITY_NAMES = "activity_names";

    public static final String OPERATIONS = "operations";

    public static final String CONTEXT = "context";

    @Override
    public Serializable execute(Map<String, Serializable> parameters, TenantServiceAccessor serviceAccessor) throws SCommandParameterizationException,
            SCommandExecutionException {
        // get parameters
        long processDefinitionId = getProcessDefinitionId(parameters);
        List<String> activityNames = getActivityNames(parameters);
        long startedBy = getStartedBy(parameters);
        Map<String, Serializable> context = getContext(parameters);
        List<Operation> operations = getOperations(parameters);

        ProcessInstance processInstance;
        try {
            // validate inputs
            AdvancedStartProcessValidator validator = new AdvancedStartProcessValidator(serviceAccessor.getProcessDefinitionService(), processDefinitionId);
            List<String> problems = validator.validate(activityNames);
            handleProblems(problems);

            // start the process
            ProcessStarter starter = new ProcessStarter(startedBy, processDefinitionId, operations, context, activityNames);
            processInstance = starter.start();
        } catch (final Exception e) {
            throw new SCommandExecutionException(e);
        }
        return processInstance;
    }

    private void handleProblems(List<String> problems) throws SCommandExecutionException {
        if (!problems.isEmpty()) {
            StringBuilder stb = new StringBuilder();
            for (String problem : problems) {
                stb.append(problem);
                stb.append("\n");
            }
            throw new SCommandExecutionException(stb.toString());
        }
    }

    private long getStartedBy(Map<String, Serializable> parameters) throws SCommandParameterizationException {
        return getMandatory(STARTED_BY, parameters);
    }

    private long getProcessDefinitionId(Map<String, Serializable> parameters) throws SCommandParameterizationException {
        return getMandatory(PROCESS_DEFINITION_ID, parameters);
    }

    private List<Operation> getOperations(Map<String, Serializable> parameters) throws SCommandParameterizationException {
        return get(OPERATIONS, parameters);
    }

    private Map<String, Serializable> getContext(Map<String, Serializable> parameters) throws SCommandParameterizationException {
        return get(CONTEXT, parameters);
    }

    private List<String> getActivityNames(Map<String, Serializable> parameters) throws SCommandParameterizationException {
        return getMandatory(ACTIVITY_NAMES, parameters);
    }

    private <T> T get(String parameter, Map<String, Serializable> parameters) throws SCommandParameterizationException {
        return getParameter(parameters, parameter, "An error occurred while parsing " + parameter);
    }

    private <T> T getMandatory(String parameter, Map<String, Serializable> parameters) throws SCommandParameterizationException {
        return getMandatoryParameter(parameters, parameter, "Missing mandatory field: " + parameter);
    }

}
