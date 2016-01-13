package org.fxapps.service;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.fxapps.utils.ClientRequestUtil;
import org.jboss.resteasy.client.ClientRequest;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinitionList;
import org.kie.server.api.rest.RestURI;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.RuleServicesClient;

/**
 * 
 * An implementation of KieServerClient that uses the kie-server-client API
 * 
 * @author wsiqueir
 *
 */
class KieServerClientServiceImpl implements KieServerClientService {

	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
	static KieServerClientServiceImpl INSTANCE;
	private KieServicesClient client;
	private KieServerInfo kieServerInfo;
	private RuleServicesClient rulesClient;
	private ProcessServicesClient processesClient;
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
	public ProcessDefinitionList getProcessesDefinitions(String containerId)
			throws Exception {
		// This is a method that does not seem to be implemented on the API!
		// hence we are making a manual HTTP request
		ClientRequest cr = ClientRequestUtil.create(RestURI.QUERY_URI,
				RestURI.PROCESS_DEFINITIONS_BY_CONTAINER_ID_GET_URI);
		cr.pathParameter(RestURI.CONTAINER_ID, containerId);
		cr.accept(MediaType.APPLICATION_JSON);
		String payload = cr.get(String.class).getEntity();
		return marshaller.unmarshall(payload, ProcessDefinitionList.class);
	}

	@Override
	public Marshaller getMarshaller() {
		return marshaller;
	}

}