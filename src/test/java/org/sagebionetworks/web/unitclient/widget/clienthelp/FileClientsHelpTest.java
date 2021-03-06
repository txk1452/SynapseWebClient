package org.sagebionetworks.web.unitclient.widget.clienthelp;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.repo.model.VersionInfo;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.widget.clienthelp.FileClientsHelp;
import org.sagebionetworks.web.client.widget.clienthelp.FileClientsHelpView;
import org.sagebionetworks.web.shared.PaginatedResults;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileClientsHelpTest {
	FileClientsHelp widget;
	@Mock
	FileClientsHelpView mockView;
	@Mock
	SynapseClientAsync mockSynapseClient;
	@Mock
	PaginatedResults<VersionInfo> mockPageResults;
	@Mock
	SynapseJSNIUtils mockJsniUtils;
	List<VersionInfo> versions;
	public static final String ENTITY_ID="syn29382";
	public static final Long ENTITY_VERSION=42L;
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		versions = new ArrayList<VersionInfo>();
		when(mockPageResults.getResults()).thenReturn(versions);
		AsyncMockStubber.callSuccessWith(mockPageResults).when(mockSynapseClient).getEntityVersions(anyString(), anyInt(), anyInt(), any(AsyncCallback.class));
		widget = new FileClientsHelp(mockView, mockSynapseClient, mockJsniUtils);
	}

	@Test
	public void testConfigureNoVersions() {
		widget.configure(ENTITY_ID, ENTITY_VERSION);
		
		verify(mockView).configure(ENTITY_ID, ENTITY_VERSION);
		verify(mockSynapseClient).getEntityVersions(eq(ENTITY_ID), eq(0), eq(1), any(AsyncCallback.class));
		verify(mockView).setVersionVisible(false);
	}
	
	@Test
	public void testConfigureCurrentVersion() {
		VersionInfo currentVersion = new VersionInfo();
		currentVersion.setVersionNumber(ENTITY_VERSION);
		versions.add(currentVersion);

		widget.configure(ENTITY_ID, ENTITY_VERSION);
		
		verify(mockView).configure(ENTITY_ID, ENTITY_VERSION);
		verify(mockSynapseClient).getEntityVersions(eq(ENTITY_ID), eq(0), eq(1), any(AsyncCallback.class));
		// showing current version, so show instructions on how to get the latest version
		verify(mockView).setVersionVisible(false);
	}

	@Test
	public void testConfigureOldVersion() {
		VersionInfo currentVersion = new VersionInfo();
		currentVersion.setVersionNumber(100L);
		versions.add(currentVersion);
		
		widget.configure(ENTITY_ID, ENTITY_VERSION);
		
		verify(mockView).configure(ENTITY_ID, ENTITY_VERSION);
		verify(mockSynapseClient).getEntityVersions(eq(ENTITY_ID), eq(0), eq(1), any(AsyncCallback.class));
		// not showing current version, so show instructions on how to get the version that's being displayed
		verify(mockView).setVersionVisible(true);
	}

	@Test
	public void testConfigureFailureToGetCurrentVersion() {
		String errorMessage = "could not retrieve";
		AsyncMockStubber.callFailureWith(new Exception(errorMessage)).when(mockSynapseClient).getEntityVersions(anyString(), anyInt(), anyInt(), any(AsyncCallback.class));
		
		widget.configure(ENTITY_ID, ENTITY_VERSION);
		
		verify(mockView).configure(ENTITY_ID, ENTITY_VERSION);
		verify(mockSynapseClient).getEntityVersions(eq(ENTITY_ID), eq(0), eq(1), any(AsyncCallback.class));
		verify(mockView).setVersionVisible(false);
		verify(mockJsniUtils).consoleError(errorMessage);
	}
	
	@Test
	public void testAsWidget() {
		widget.asWidget();
		verify(mockView).asWidget();
	}

}
