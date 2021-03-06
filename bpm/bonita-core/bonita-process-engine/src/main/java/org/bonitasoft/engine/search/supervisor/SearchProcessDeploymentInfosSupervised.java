package org.bonitasoft.engine.search.supervisor;

import java.util.List;

import org.bonitasoft.engine.core.process.definition.ProcessDefinitionService;
import org.bonitasoft.engine.core.process.definition.model.SProcessDefinitionDeployInfo;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.persistence.SBonitaSearchException;
import org.bonitasoft.engine.search.AbstractProcessDeploymentInfoSearchEntity;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.descriptor.SearchProcessDefinitionsDescriptor;

public class SearchProcessDeploymentInfosSupervised extends AbstractProcessDeploymentInfoSearchEntity {

    private final ProcessDefinitionService processDefinitionService;

    private final long userId;

    public SearchProcessDeploymentInfosSupervised(final ProcessDefinitionService processDefinitionService,
            final SearchProcessDefinitionsDescriptor searchEntitiesDescriptor, final SearchOptions options, final long userId) {
        super(searchEntitiesDescriptor, options);
        this.processDefinitionService = processDefinitionService;
        this.userId = userId;
    }

    @Override
    public long executeCount(final QueryOptions searchOptions) throws SBonitaSearchException {
        return processDefinitionService.getNumberOfProcessDeploymentInfos(userId, searchOptions, "UserSupervised");
    }

    @Override
    public List<SProcessDefinitionDeployInfo> executeSearch(final QueryOptions searchOptions) throws SBonitaSearchException {
        return processDefinitionService.searchProcessDeploymentInfos(userId, searchOptions, "UserSupervised");
    }

}
