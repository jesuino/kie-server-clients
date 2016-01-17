package org.fxapps.service;

import java.util.List;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.ServiceTasksDefinition;
import org.kie.server.api.model.definition.UserTaskDefinitionList;
import org.kie.server.api.model.definition.VariablesDefinition;
import org.kie.server.api.model.instance.ProcessInstance;

/**
 * The contract of a kie server client. It is also a factory of implementations,
 * hiding everything from our app
 * 
 * @author wsiqueir
 *
 */
public interface KieServerClientService {

	enum Type {
		API;
	};

	public void login(String url, String username, String password);

	public List<KieContainerResource> listContainers();

	public KieServerInfo getServerInfo();

	public ServiceResponse<Void> disposeContainer(String id);

	public ServiceResponse<KieContainerResource> createContainer(String id, String groupId, String artifactId,
			String version);

	public default KieServerClientService factory(Type type) {
		KieServerClientService service;
		switch (type) {
		case API:
			service = new KieServerClientServiceImpl();
			break;

		default:
			service = new KieServerClientServiceImpl();
			break;
		}
		return service;
	}

	public ServiceResponse<String>  executeCommand(String containerId,
			BatchExecutionCommandImpl batchCmd);
	
	public boolean canRunRules();
	
	public boolean canRunProcess();
	
	public List<ProcessDefinition> getProcessesDefinitions(String containerId);

	public Marshaller getMarshaller();
	
	public String getProcessForm(String containerId, String processId, String language);
	
	public VariablesDefinition getVariableDefinitions(String containerId, String processId);
	
	public UserTaskDefinitionList getUserTaskDefinitions(String containerId, String processId);

	public ServiceTasksDefinition getServiceTaskDefinitions(String containerId, String processId);

	public List<ProcessInstance> findProcessInstancesByProcessId(String processId, List<Integer> status, Integer page, Integer pageSize);

	public void abortProcessInstances(String containerId, List<Long> processInstanceIds);

	public List<String> getAvailableSignals(String containerId, Long processInstanceId);
	
	public void signalProcessInstances(String containerId, List<Long> processInstanceId, String signalName, Object event);

}