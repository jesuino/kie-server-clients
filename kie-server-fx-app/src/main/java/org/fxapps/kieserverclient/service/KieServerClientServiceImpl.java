package org.fxapps.kieserverclient.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.ServiceTasksDefinition;
import org.kie.server.api.model.definition.UserTaskDefinitionList;
import org.kie.server.api.model.definition.VariablesDefinition;
import org.kie.server.api.model.instance.JobRequestInstance;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.RequestInfoInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.JobServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.kie.server.client.UIServicesClient;
import org.kie.server.client.UserTaskServicesClient;

/**
 * 
 * An implementation of KieServerClient that uses the kie-server-client API
 * 
 * @author wsiqueir
 *
 */
@Default
@ApplicationScoped
class KieServerClientServiceImpl implements KieServerClientService {

	private static final MarshallingFormat FORMAT;
	
	List<Integer> PROCESS_INSTANCE_STATUS = Arrays.asList(
			WorkflowProcessInstance.STATE_ABORTED,
			WorkflowProcessInstance.STATE_ACTIVE,
			WorkflowProcessInstance.STATE_COMPLETED,
			WorkflowProcessInstance.STATE_PENDING,
			WorkflowProcessInstance.STATE_SUSPENDED
	);
	
	private List<String> JOB_STATUS = Arrays.asList("QUEUED", "DONE",
			"CANCELLED", "ERROR", "RETRYING", "RUNNING");;

	private KieServicesClient client;
	private KieServerInfo kieServerInfo;
	private RuleServicesClient rulesClient;
	private ProcessServicesClient processesClient;
	private QueryServicesClient queryClient;
	private Marshaller marshaller;
	private UserTaskServicesClient userTasksClient;
	private JobServicesClient jobClient;
	private UIServicesClient uiClient;
	private String usr;

	static {
		String formatType = System.getProperty("kieserverfx.formatType", "json");
		FORMAT = MarshallingFormat.fromType(formatType);
	}

	/**
	 * The default constructor has default access
	 */
	KieServerClientServiceImpl() {

	}

	@Override
	public void login(String url, String usr, String psw) {
		this.usr = usr;
		KieServicesConfiguration configuration = KieServicesFactory
				.newRestConfiguration(url, usr, psw);
		configuration.setMarshallingFormat(FORMAT);
		configuration
				.setCredentialsProvider(new org.kie.server.client.credentials.EnteredCredentialsProvider(
						usr, psw) {

				});
		client = KieServicesFactory.newKieServicesClient(configuration);
		kieServerInfo = client.getServerInfo().getResult();
		
		for (String capability : kieServerInfo.getCapabilities()) {
			if ("BPM".equals(capability)) {
				processesClient = client
						.getServicesClient(ProcessServicesClient.class);
				userTasksClient = client
						.getServicesClient(UserTaskServicesClient.class);
				queryClient = client
						.getServicesClient(QueryServicesClient.class);
				jobClient = client.getServicesClient(JobServicesClient.class);
				uiClient = client.getServicesClient(UIServicesClient.class);
				supportsBPM.set(true);
			}
			if ("BRM".equals(capability)) {
				rulesClient = client
						.getServicesClient(RuleServicesClient.class);
				supportsBRM.set(true);
			}
			if("BRP".equals(capability)) {
				supportsSolvers.set(true);
			}
		}
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
	public ServiceResponse<ExecutionResults> executeCommand(String containerId,
			BatchExecutionCommandImpl batchCmd) {
		return rulesClient.executeCommandsWithResults(containerId, batchCmd);
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
	public List<String> getAvailableSignals(String containerId,
			Long processInstanceId) {
		return processesClient.getAvailableSignals(containerId,
				processInstanceId);
	}

	@Override
	public void signalProcessInstances(String containerId,
			List<Long> processInstanceId, String signalName, Object event) {
		processesClient.signalProcessInstances(containerId, processInstanceId,
				signalName, event);
	}

	@Override
	public Long startProcess(String containerId, String processId) {
		return processesClient.startProcess(containerId, processId);
	}

	@Override
	public Long startProcess(String containerId, String processId,
			Map<String, Object> variables) {
		return processesClient.startProcess(containerId, processId, variables);
	}

	@Override
	public List<TaskSummary> findTasksByProcessInstanceId(Long id) {
		return userTasksClient.findTasksByStatusByProcessInstanceId(id,
				Collections.emptyList(), 0, 1000);
	}

	@Override
	public void activateTask(String containerId, Long taskId, String userId) {
		userTasksClient.activateTask(containerId, taskId, userId);
	}

	@Override
	public void claimTask(String containerId, Long taskId, String userId) {
		userTasksClient.claimTask(containerId, taskId, userId);

	}

	@Override
	public void completeTask(String containerId, Long taskId, String userId) {
		// complete will be done by the task form in future, no parameters are
		// supported at the moment
		userTasksClient.completeTask(containerId, taskId, userId, null);
	}

	@Override
	public void delegateTask(String containerId, Long taskId, String userId,
			String targetUserId) {
		userTasksClient.delegateTask(containerId, taskId, userId, targetUserId);
	}

	@Override
	public void exitTask(String containerId, Long taskId, String userId) {
		userTasksClient.exitTask(containerId, taskId, userId);
	}

	@Override
	public void failTask(String containerId, Long taskId, String userId) {
		userTasksClient.failTask(containerId, taskId, userId, null);
	}

	@Override
	public void forwardTask(String containerId, Long taskId, String userId,
			String targetEntityId) {
		userTasksClient
				.forwardTask(containerId, taskId, userId, targetEntityId);
	}

	@Override
	public void releaseTask(String containerId, Long taskId, String userId) {
		userTasksClient.releaseTask(containerId, taskId, userId);
	}

	@Override
	public void resumeTask(String containerId, Long taskId, String userId) {
		userTasksClient.resumeTask(containerId, taskId, userId);
	}

	@Override
	public void skipTask(String containerId, Long taskId, String userId) {
		userTasksClient.skipTask(containerId, taskId, userId);

	}

	@Override
	public void startTask(String containerId, Long taskId, String userId) {
		userTasksClient.startTask(containerId, taskId, userId);
	}

	@Override
	public void stopTask(String containerId, Long taskId, String userId) {
		userTasksClient.stopTask(containerId, taskId, userId);
	}

	@Override
	public void suspendTask(String containerId, Long taskId, String userId) {
		userTasksClient.suspendTask(containerId, taskId, userId);
	}

	@Override
	public void nominateTask(String containerId, Long taskId, String userId,
			List<String> potentialOwners) {
		userTasksClient.nominateTask(containerId, taskId, userId,
				potentialOwners);
	}

	@Override
	public List<TaskSummary> findTasks(String user) {
		return userTasksClient.findTasks(user, 0, 1000);
	}

	@Override
	public List<RequestInfoInstance> getAllJobsRequest() {
		return jobClient.getRequestsByStatus(JOB_STATUS, 0, 1000);
	}

	@Override
	public Long scheduleRequest(JobRequestInstance request) {
		return jobClient.scheduleRequest(request);
	}

	@Override
	public Long scheduleRequest(String containerId, JobRequestInstance request) {
		return jobClient.scheduleRequest(containerId, request);
	}

	@Override
	public void cancelRequest(long requestId) {
		jobClient.cancelRequest(requestId);
	}

	@Override
	public void requeueRequest(long requestId) {
		jobClient.requeueRequest(requestId);
	}
	
	@Override
	public String getProcessImage(String containerId, String processDefinitionId) {
		return uiClient.getProcessImage(containerId, processDefinitionId);
	}

	@Override
	public List<ProcessInstance> allProcessInstances(String containerId, int max) {
		return queryClient.findProcessInstancesByContainerId(containerId, PROCESS_INSTANCE_STATUS, 0, max);
	}

	@Override
	public List<TaskSummary> allUserTasks(int max) {
		return userTasksClient.findTasks(usr, 0, max);
	}

	@Override
	public List<QueryDefinition> queries(int max) {
		return queryClient.getQueries(0, max);
	}
	
	@Override
	public void unregisterQuery(String queryName) {
		queryClient.unregisterQuery(queryName);
		
	}
	
	@Override
	public void registerQuery(QueryDefinition queryDefinition) {
		queryClient.registerQuery(queryDefinition);
		
	}
	
	@Override
	public List<NodeInstance> findNodeInstances(long processInstanceId, int max) {
		return queryClient.findNodeInstances(processInstanceId, 0, max);
	}
	
}