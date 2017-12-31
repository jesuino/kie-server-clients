package org.fxapps.kieserverclient.service;

import java.util.List;
import java.util.Map;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.ServiceTasksDefinition;
import org.kie.server.api.model.definition.UserTaskDefinitionList;
import org.kie.server.api.model.definition.VariablesDefinition;
import org.kie.server.api.model.instance.JobRequestInstance;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.RequestInfoInstance;
import org.kie.server.api.model.instance.TaskSummary;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * The contract of a kie server client. It is also a factory of implementations,
 * hiding everything from our app
 * 
 * @author wsiqueir
 *
 */
public interface KieServerClientService {

	final BooleanProperty supportsBPM = new SimpleBooleanProperty(false);
	final BooleanProperty supportsBRM = new SimpleBooleanProperty(false);
	final BooleanProperty supportsSolvers = new SimpleBooleanProperty(false);
	
	enum Type {
		API;
	};
	
	default public BooleanProperty getSupportsBPM() {
		return supportsBPM;
	}
	
	default public BooleanProperty getSupportsBRM() {
		return supportsBRM;
	}
	
	default public BooleanProperty getSupportsSolver() {
		return supportsSolvers;	
	}

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

	public ServiceResponse<ExecutionResults>  executeCommand(String containerId,
			BatchExecutionCommandImpl batchCmd);
	
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

	public Long startProcess(String containerId, String processId);

	public List<TaskSummary> findTasksByProcessInstanceId(Long id);
	
	// TASK OPERATIONS
    void activateTask(String containerId, Long taskId, String userId);

    void claimTask(String containerId, Long taskId, String userId);

    // no parameters at the moment, it will be done once we can load forms in our app
    void completeTask(String containerId, Long taskId, String userId);

    void delegateTask(String containerId, Long taskId, String userId, String targetUserId);

    void exitTask(String containerId, Long taskId, String userId);
    
    // no parameters at the moment, it will be done once we can load forms in our app
    void failTask(String containerId, Long taskId, String userId);

    void forwardTask(String containerId, Long taskId, String userId, String targetEntityId);

    void releaseTask(String containerId, Long taskId, String userId);

    void resumeTask(String containerId, Long taskId, String userId);

    void skipTask(String containerId, Long taskId, String userId);

    void startTask(String containerId, Long taskId, String userId);

    void stopTask(String containerId, Long taskId, String userId);

    void suspendTask(String containerId, Long taskId, String userId);

    void nominateTask(String containerId, Long taskId, String userId, List<String> potentialOwners);

	List<TaskSummary> findTasks(String user);

	public List<RequestInfoInstance> getAllJobsRequest();

	public Long scheduleRequest(JobRequestInstance request);

	public Long scheduleRequest(String containerId, JobRequestInstance request);
	
	public void cancelRequest(long requestId);
	
	public void requeueRequest(long requestId);

	public Long startProcess(String containerId, String processId, Map<String, Object> variables);

	public String getProcessImage(String containerId, String processDefinitionId);
	
//	public String getProcessForm(String containerId, String processDefinitionId);
	
	public List<ProcessInstance> allProcessInstances(String containerId, int max);
	
	public List<TaskSummary> allUserTasks(int max);

	


}
