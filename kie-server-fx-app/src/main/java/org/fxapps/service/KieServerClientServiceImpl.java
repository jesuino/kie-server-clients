package org.fxapps.service;

import java.util.List;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
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
	public void executeCommand(String containerId,
			BatchExecutionCommandImpl batchCmd) {
		rulesClient.executeCommands(containerId, batchCmd);

	}

	@Override
	public boolean canRunRules() {
		return rulesClient != null;
	}

	@Override
	public boolean canRunProcess() {
		return processesClient != null;
	}

}