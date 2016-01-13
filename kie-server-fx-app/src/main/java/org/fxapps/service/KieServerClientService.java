package org.fxapps.service;

import java.util.List;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;

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
			// the default is the only implementation we have
			service = new KieServerClientServiceImpl();
			break;
		}
		return service;
	}

	public ServiceResponse<String>  executeCommand(String containerId,
			BatchExecutionCommandImpl batchCmd);
	
	public boolean canRunRules();
	
	public boolean canRunProcess();
	
	public List<ProcessDefinition> getProcessesDefinitions(String containerId) throws Exception;

	Marshaller getMarshaller();

}
