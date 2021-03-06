/**
 * Copyright (C) 2012, 2014 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.engine.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.impl.APIAccessorImpl;
import org.bonitasoft.engine.connector.ConnectorAPIAccessorImpl;
import org.bonitasoft.engine.connector.EngineExecutionContext;
import org.bonitasoft.engine.core.expression.control.model.SExpressionContext;
import org.bonitasoft.engine.core.process.definition.model.SFlowNodeType;
import org.bonitasoft.engine.core.process.instance.api.ActivityInstanceService;
import org.bonitasoft.engine.core.process.instance.api.ProcessInstanceService;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SActivityInstanceNotFoundException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SActivityReadException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SFlowNodeNotFoundException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SFlowNodeReadException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SProcessInstanceNotFoundException;
import org.bonitasoft.engine.core.process.instance.api.exceptions.SProcessInstanceReadException;
import org.bonitasoft.engine.core.process.instance.model.SActivityInstance;
import org.bonitasoft.engine.core.process.instance.model.SFlowNodeInstance;
import org.bonitasoft.engine.core.process.instance.model.SHumanTaskInstance;
import org.bonitasoft.engine.core.process.instance.model.SProcessInstance;
import org.bonitasoft.engine.core.process.instance.model.archive.SAActivityInstance;
import org.bonitasoft.engine.core.process.instance.model.archive.SAFlowNodeInstance;
import org.bonitasoft.engine.core.process.instance.model.archive.SAHumanTaskInstance;
import org.bonitasoft.engine.data.instance.api.DataInstanceContainer;
import org.bonitasoft.engine.expression.exception.SExpressionEvaluationException;
import org.bonitasoft.engine.expression.exception.SInvalidExpressionException;
import org.bonitasoft.engine.expression.model.ExpressionKind;
import org.bonitasoft.engine.expression.model.SExpression;
import org.bonitasoft.engine.session.SSessionNotFoundException;
import org.bonitasoft.engine.session.SessionService;
import org.bonitasoft.engine.sessionaccessor.ReadSessionAccessor;
import org.bonitasoft.engine.sessionaccessor.STenantIdNotSetException;
import org.bonitasoft.engine.sessionaccessor.SessionIdNotSetException;

/**
 * @author Matthieu Chaffotte
 * @author Elias Ricken de Medeiros
 * @author Celine Souchet
 */
public class EngineConstantExpressionExecutorStrategy implements ExpressionExecutorStrategy {

    private final ActivityInstanceService activityInstanceService;

    private final ProcessInstanceService processInstanceService;

    protected final ReadSessionAccessor sessionAccessor;

    protected final SessionService sessionService;

    public EngineConstantExpressionExecutorStrategy(final ActivityInstanceService activityInstanceService, final ProcessInstanceService processInstanceService,
            final SessionService sessionService, final ReadSessionAccessor sessionAccessor) {
        this.activityInstanceService = activityInstanceService;
        this.processInstanceService = processInstanceService;
        this.sessionService = sessionService;
        this.sessionAccessor = sessionAccessor;

    }

    @Override
    public Serializable evaluate(final SExpression expression, final Map<String, Object> context, final Map<Integer, Object> resolvedExpressions)
            throws SExpressionEvaluationException {
        ExpressionConstants expressionConstant = ExpressionConstantsResolver.getExpressionConstantsFromName(expression.getContent());
        if (expressionConstant == null) {
            expressionConstant = ExpressionConstants.API_ACCESSOR;// just to make the expressionConstantsResolver load constants
            expressionConstant = ExpressionConstantsResolver.getExpressionConstantsFromName(expression.getContent());
        }
        final String expressionName = expression.getName();
        if (expressionConstant == null) {
            throw new SExpressionEvaluationException(expression.getContent() + " is not a valid Engine-provided variable", expressionName);
        }
        try {
            switch (expressionConstant) {
                case API_ACCESSOR:
                    return getApiAccessor();
                case CONNECTOR_API_ACCESSOR:
                    return getConnectorApiAccessor();
                case ENGINE_EXECUTION_CONTEXT:
                    return getFromContextOrEngineExecutionContext(expressionConstant, context);
                case ACTIVITY_INSTANCE_ID:
                    return getFromContextOrEngineExecutionContext(expressionConstant, context);
                case PROCESS_INSTANCE_ID:
                    return getFromContextOrEngineExecutionContext(expressionConstant, context);
                case PROCESS_DEFINITION_ID:
                    return getFromContextOrEngineExecutionContext(expressionConstant, context);
                case ROOT_PROCESS_INSTANCE_ID:
                    return getFromContextOrEngineExecutionContext(expressionConstant, context);
                case TASK_ASSIGNEE_ID:
                    return getFromContextOrEngineExecutionContext(expressionConstant, context);
                case LOGGED_USER_ID:
                    return getLoggedUserFromSession();
                default:
                    final Object object = context.get(expressionConstant.getEngineConstantName());
                    if (object == null) {
                        throw new SExpressionEvaluationException("EngineConstantExpression not supported for: " + expressionConstant.getEngineConstantName(),
                                expressionName);
                    }
                    return (Serializable) object;
            }
        } catch (final STenantIdNotSetException e) {
            throw new SExpressionEvaluationException(e, expressionName);
        } catch (final SSessionNotFoundException e) {
            throw new SExpressionEvaluationException(e, expressionName);
        } catch (final SProcessInstanceNotFoundException e) {
            throw new SExpressionEvaluationException("Error retrieving process instance while building EngineExecutionContext as EngineConstantExpression", e,
                    expressionName);
        } catch (final SProcessInstanceReadException e) {
            throw new SExpressionEvaluationException("Error retrieving process instance while building EngineExecutionContext as EngineConstantExpression", e,
                    expressionName);
        } catch (final SFlowNodeReadException e) {
            throw new SExpressionEvaluationException("Error retrieving flow node instance while building EngineExecutionContext as EngineConstantExpression",
                    e, expressionName);
        } catch (final SFlowNodeNotFoundException e) {
            throw new SExpressionEvaluationException("Error retrieving flow node instance while building EngineExecutionContext as EngineConstantExpression",
                    e, expressionName);
        } catch (final SActivityInstanceNotFoundException e) {
            throw new SExpressionEvaluationException("Error retrieving activity instance while building EngineExecutionContext as EngineConstantExpression", e,
                    expressionName);
        }
    }

    protected APIAccessorImpl getApiAccessor() {
        return new APIAccessorImpl();
    }

    protected APIAccessor getConnectorApiAccessor() throws STenantIdNotSetException {
        final long tenantId = sessionAccessor.getTenantId();
        return new ConnectorAPIAccessorImpl(tenantId);
    }

    private long getLoggedUserFromSession() throws SSessionNotFoundException {
        try {
            return sessionService.getSession(sessionAccessor.getSessionId()).getUserId();
        } catch (final SessionIdNotSetException e) {
            return -1;
        }
    }

    private Serializable getFromContextOrEngineExecutionContext(final ExpressionConstants expressionConstant, final Map<String, Object> context)
            throws SProcessInstanceNotFoundException, SProcessInstanceReadException, SActivityInstanceNotFoundException, SFlowNodeNotFoundException,
            SFlowNodeReadException {
        final Object object = context.get(expressionConstant.getEngineConstantName());
        if (object == null) {
            // try to get it from an already evaluated context
            final EngineExecutionContext engineContext = (EngineExecutionContext) context.get(ExpressionConstants.ENGINE_EXECUTION_CONTEXT
                    .getEngineConstantName());
            if (engineContext != null) {
                return engineContext.getExpressionConstant(expressionConstant);
            }
            return evaluate(expressionConstant, context);
        }
        // we have it already evaluated
        return (Serializable) object;
    }

    private Serializable evaluate(final ExpressionConstants expressionConstant, final Map<String, Object> context) throws SProcessInstanceNotFoundException,
            SProcessInstanceReadException, SActivityInstanceNotFoundException, SFlowNodeNotFoundException, SFlowNodeReadException {
        // guess it
        if (ExpressionConstants.ENGINE_EXECUTION_CONTEXT.equals(expressionConstant)) {
            return createContext(context);
        } else if (context.containsKey(SExpressionContext.containerTypeKey) && context.containsKey(SExpressionContext.containerIdKey)) {
            final String containerType = (String) context.get(SExpressionContext.containerTypeKey);
            final long containerId = (Long) context.get(SExpressionContext.containerIdKey);
            if (ExpressionConstants.PROCESS_DEFINITION_ID.equals(expressionConstant)) {
                return (Serializable) context.get(SExpressionContext.processDefinitionIdKey);
            } else if (DataInstanceContainer.ACTIVITY_INSTANCE.toString().equals(containerType)) {
                return evaluateUsingActivityInstanceContainer(expressionConstant, context, containerId);
            } else {
                return evaluateUsingProcessInstanceContainer(expressionConstant, context, containerId);
            }
        } else {
            return -1;// no container id and not processDefinition
        }
    }

    private Serializable evaluateUsingProcessInstanceContainer(final ExpressionConstants expressionConstant, final Map<String, Object> context,
            final long containerId) throws SProcessInstanceNotFoundException, SProcessInstanceReadException {
        if (ExpressionConstants.PROCESS_INSTANCE_ID.equals(expressionConstant)) {
            return containerId;
        } else if (ExpressionConstants.TASK_ASSIGNEE_ID.equals(expressionConstant)) {
            return -1; // the assignee is related to an user task
        } else {
            // get the process and fill the others elements
            fillDependenciesFromProcessInstance(context, containerId);
            return getNonNullLong(expressionConstant, context);
        }
    }

    private Serializable evaluateUsingActivityInstanceContainer(final ExpressionConstants expressionConstant, final Map<String, Object> context,
            final long containerId) throws SActivityInstanceNotFoundException, SFlowNodeNotFoundException, SFlowNodeReadException {
        if (ExpressionConstants.ACTIVITY_INSTANCE_ID.equals(expressionConstant)) {
            return containerId;
        }
        // get the activity and fill the others elements
        fillDependenciesFromFlowNodeInstance(context, containerId);
        return getNonNullLong(expressionConstant, context);
    }

    private Serializable getNonNullLong(final ExpressionConstants expressionConstant, final Map<String, Object> context) {
        final Serializable serializable = (Serializable) context.get(expressionConstant.getEngineConstantName());
        return serializable == null ? -1L : serializable;
    }

    private void fillDependenciesFromProcessInstance(final Map<String, Object> context, final long processInstanceId) throws SProcessInstanceNotFoundException,
            SProcessInstanceReadException {
        final SProcessInstance processInstance = processInstanceService.getProcessInstance(processInstanceId);
        context.put(ExpressionConstants.PROCESS_INSTANCE_ID.getEngineConstantName(), processInstance.getId());
        context.put(ExpressionConstants.ROOT_PROCESS_INSTANCE_ID.getEngineConstantName(), processInstance.getRootProcessInstanceId());
    }

    void fillDependenciesFromFlowNodeInstance(final Map<String, Object> context, final long flowNodeInstanceId) throws SActivityInstanceNotFoundException,
            SFlowNodeNotFoundException, SFlowNodeReadException {
        if (context.get("time") != null) {
            final SAActivityInstance aActivityInstance = activityInstanceService.getMostRecentArchivedActivityInstance(flowNodeInstanceId);
            context.put(ExpressionConstants.PROCESS_INSTANCE_ID.getEngineConstantName(), aActivityInstance.getLogicalGroup(3));
            context.put(ExpressionConstants.ROOT_PROCESS_INSTANCE_ID.getEngineConstantName(), aActivityInstance.getLogicalGroup(1));
            if (isHumanTask(aActivityInstance)) {
                final SAHumanTaskInstance saHumanTask = (SAHumanTaskInstance) aActivityInstance;
                context.put(ExpressionConstants.TASK_ASSIGNEE_ID.getEngineConstantName(), saHumanTask.getAssigneeId());
            }
        } else {
            final SFlowNodeInstance flowNodeInstance = activityInstanceService.getFlowNodeInstance(flowNodeInstanceId);
            context.put(ExpressionConstants.PROCESS_INSTANCE_ID.getEngineConstantName(), flowNodeInstance.getLogicalGroup(3));
            context.put(ExpressionConstants.ROOT_PROCESS_INSTANCE_ID.getEngineConstantName(), flowNodeInstance.getLogicalGroup(1));
            if (isHumanTask(flowNodeInstance)) {
                final SHumanTaskInstance taskInstance = (SHumanTaskInstance) flowNodeInstance;
                context.put(ExpressionConstants.TASK_ASSIGNEE_ID.getEngineConstantName(), taskInstance.getAssigneeId());
            }
        }
    }

    private boolean isHumanTask(final SAFlowNodeInstance aFlowNodeInstance) {
        return SFlowNodeType.USER_TASK.equals(aFlowNodeInstance.getType()) || SFlowNodeType.MANUAL_TASK.equals(aFlowNodeInstance.getType());
    }

    private boolean isHumanTask(final SFlowNodeInstance flowNodeInstance) {
        return SFlowNodeType.USER_TASK.equals(flowNodeInstance.getType()) || SFlowNodeType.MANUAL_TASK.equals(flowNodeInstance.getType());
    }

    private Serializable createContext(final Map<String, Object> context) throws SProcessInstanceNotFoundException, SProcessInstanceReadException,
            SActivityReadException, SActivityInstanceNotFoundException {
        final EngineExecutionContext ctx = new EngineExecutionContext();
        if (context.containsKey(SExpressionContext.containerTypeKey) && context.containsKey(SExpressionContext.containerIdKey)) {
            final String containerType = (String) context.get(SExpressionContext.containerTypeKey);
            final long containerId = (Long) context.get(SExpressionContext.containerIdKey);
            if (DataInstanceContainer.ACTIVITY_INSTANCE.toString().equals(containerType)) {
                updateContextFromActivityInstance(ctx, containerId);
            } else if (DataInstanceContainer.PROCESS_INSTANCE.toString().equals(containerType)) {
                updateContextFromProcessInstance(ctx, containerId);
            }
        }
        if (context.containsKey(SExpressionContext.processDefinitionIdKey)) {
            ctx.setProcessDefinitionId((Long) context.get(SExpressionContext.processDefinitionIdKey));
        }
        return ctx;
    }

    private void updateContextFromProcessInstance(final EngineExecutionContext ctx, final long processInstanceId) throws SProcessInstanceNotFoundException,
            SProcessInstanceReadException {
        final SProcessInstance processInstance = processInstanceService.getProcessInstance(processInstanceId);
        ctx.setProcessInstanceId(processInstance.getId());
        ctx.setRootProcessInstanceId(processInstance.getRootProcessInstanceId());
    }

    private void updateContextFromActivityInstance(final EngineExecutionContext ctx, final long activityInstanceId) throws SActivityReadException,
            SActivityInstanceNotFoundException {
        ctx.setActivityInstanceId(activityInstanceId);
        final SActivityInstance activityInstance = activityInstanceService.getActivityInstance(activityInstanceId);
        ctx.setProcessInstanceId(activityInstance.getParentProcessInstanceId());
        ctx.setRootProcessInstanceId(activityInstance.getRootProcessInstanceId());
        if (isHumanTask(activityInstance)) {
            ctx.setTaskAssigneeId(((SHumanTaskInstance) activityInstance).getAssigneeId());
        }
    }

    @Override
    public void validate(final SExpression expression) throws SInvalidExpressionException {
        if (ExpressionConstantsResolver.getExpressionConstantsFromName(expression.getContent()) == null) {
            throw new SInvalidExpressionException("Unable to get Engine Constant '" + expression.getContent() + "' in expression: " + expression,
                    expression.getName());
        }
    }

    @Override
    public ExpressionKind getExpressionKind() {
        return KIND_ENGINE_CONSTANT;
    }

    @Override
    public List<Object> evaluate(final List<SExpression> expressions, final Map<String, Object> context, final Map<Integer, Object> resolvedExpressions)
            throws SExpressionEvaluationException {
        final List<Object> results = new ArrayList<Object>();
        for (final SExpression sExpression : expressions) {
            results.add(evaluate(sExpression, context, resolvedExpressions));
        }
        return results;
    }

    @Override
    public boolean mustPutEvaluatedExpressionInContext() {
        return true;
    }

}
