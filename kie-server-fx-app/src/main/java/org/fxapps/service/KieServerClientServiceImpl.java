package org.fxapps.service;

import java.util.List;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.ServiceTasksDefinition;
import org.kie.server.api.model.definition.UserTaskDefinitionList;
import org.kie.server.api.model.definition.VariablesDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.RuleServicesClient;

/**
 * 
 * An implementation of KieServerClient that uses the kie-server-client API
 * 
 * @author wsiqueir
 *
 */
class KieServerClientServiceImpl implements KieServerClientService {

	// TODO: Add UIServiceClient methods once it is available

	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
	static KieServerClientServiceImpl INSTANCE;
	private KieServicesClient client;
	private KieServerInfo kieServerInfo;
	private RuleServicesClient rulesClient;
	private ProcessServicesClient processesClient;
	private QueryServicesClient queryClient;
	private Marshaller marshaller;

	/**
	 * The default constructor has default access
	 */
	KieServerClientServiceImpl() {

	}

	@Override
	public void login(String url, String usr, String psw) {
		KieServicesConfiguration configuration = KieServicesFactory
				.newRestConfiguration(url, usr, psw);
		configuration.setMarshallingFormat(FORMAT);
		client = KieServicesFactory.newKieServicesClient(configuration);
		kieServerInfo = client.getServerInfo().getResult();
		for (String capability : kieServerInfo.getCapabilities()) {
			if ("BPM".equals(capability)) {
				processesClient = client
						.getServicesClient(ProcessServicesClient.class);
			}
			if ("BRM".equals(capability)) {
				rulesClient = client
						.getServicesClient(RuleServicesClient.class);
			}
		}
		queryClient = client.getServicesClient(QueryServicesClient.class);
		marshaller = MarshallerFactory.getMarshaller(FORMAT, getClass()
				.getClassLoader());
	}

	@Override
	public List<KieContainerResource> listContainers() {
		return client.listContainers().getResult().getContainers();
	}

	@Override
	public KieServerInfo getServerInfo() {
		return kieServerInfo;
	}

	@Override
	public ServiceResponse<Void> disposeContainer(String id) {
		return client.disposeContainer(id);
	}

	@Override
	public ServiceResponse<KieContainerResource> createContainer(String id,
			String groupId, String artifactId, String version) {
		ReleaseId releaseId = new ReleaseId(groupId, artifactId, version);
		KieContainerResource container = new KieContainerResource(releaseId);
		container.setContainerId(id);
		return client.createContainer(id, container);
	}

	@Override
	public ServiceResponse<String> executeCommand(String containerId,
			BatchExecutionCommandImpl batchCmd) {
		return rulesClient.executeCommands(containerId, batchCmd);
	}

	@Override
	public boolean canRunRules() {
		return rulesClient != null;
	}

	@Override
	public boolean canRunProcess() {
		return processesClient != null;
	}

	@Override
	public List<ProcessDefinition> getProcessesDefinitions(String containerId) {
		return queryClient.findProcessesByContainerId(containerId, 0, 1000);
	}

	@Override
	public Marshaller getMarshaller() {
		return marshaller;
	}

	@Override
	public String getProcessForm(String containerId, String processId,
			String language) {
		// TODO: it will be only available in jBPM 6.4
		return null;
	}

	@Override
	public VariablesDefinition getVariableDefinitions(String containerId,
			String processId) {
		return processesClient.getProcessVariableDefinitions(containerId,
				processId);
	}

	@Override
	public UserTaskDefinitionList getUserTaskDefinitions(String containerId,
			String processId) {
		return processesClient.getUserTaskDefinitions(containerId, processId);
	}

	@Override
	public ServiceTasksDefinition getServiceTaskDefinitions(String containerId,
			String processId) {
		return processesClient
				.getServiceTaskDefinitions(containerId, processId);
	}

	@Override
	public List<ProcessInstance> findProcessInstancesByProcessId(
			String processId, List<Integer> status, Integer page,
			Integer pageSize) {
		return queryClient.findProcessInstancesByProcessId(processId, status,
				page, pageSize);
	}

	@Override
	public void abortProcessInstances(String containerId,
			List<Long> processInstanceIds) {
		processesClient.abortProcessInstances(containerId, processInstanceIds);
	}

	@Override
	public List<String> getAvailableSignals(String containerId, Long processInstanceId) {
		return processesClient.getAvailableSignals(containerId, processInstanceId);
	}

	@Override
	public void signalProcessInstances(String containerId, List<Long> processInstanceId, String signalName,
			Object event) {
		processesClient.signalProcessInstances(containerId, processInstanceId, signalName, event);
	}

}