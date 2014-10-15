package org.sagebionetworks.web.unitclient.widget.entity.team;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sagebionetworks.markdown.constants.WidgetConstants;
import org.sagebionetworks.repo.model.ACTAccessRequirement;
import org.sagebionetworks.repo.model.AccessRequirement;
import org.sagebionetworks.repo.model.PostMessageContentAccessRequirement;
import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.TermsOfUseAccessRequirement;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.PlaceChanger;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.team.JoinTeamWidget;
import org.sagebionetworks.web.client.widget.team.JoinTeamWidgetView;
import org.sagebionetworks.web.shared.EntityWrapper;
import org.sagebionetworks.web.shared.PaginatedResults;
import org.sagebionetworks.web.shared.WebConstants;
import org.sagebionetworks.web.shared.WikiPageKey;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class JoinTeamWidgetTest {

	SynapseClientAsync mockSynapseClient;
	GlobalApplicationState mockGlobalApplicationState;
	JoinTeamWidgetView mockView;
	String teamId = "123";
	JoinTeamWidget joinWidget;
	AuthenticationController mockAuthenticationController;
	Callback mockTeamUpdatedCallback;
	JSONObjectAdapter jsonObjectAdapter;
	NodeModelCreator mockNodeModelCreator;
	GWTWrapper mockGwt;
	PlaceChanger mockPlaceChanger;
	List<AccessRequirement> ars;
	
	@Before
	public void before() throws JSONObjectAdapterException {
		mockGlobalApplicationState = mock(GlobalApplicationState.class);
		mockSynapseClient = mock(SynapseClientAsync.class);
		mockView = mock(JoinTeamWidgetView.class);
		mockAuthenticationController = mock(AuthenticationController.class);
		mockTeamUpdatedCallback = mock(Callback.class);
		mockNodeModelCreator = mock(NodeModelCreator.class);
		mockGwt = mock(GWTWrapper.class);
		jsonObjectAdapter = new JSONObjectAdapterImpl();
		
		mockPlaceChanger = mock(PlaceChanger.class);
        when(mockGlobalApplicationState.getPlaceChanger()).thenReturn(mockPlaceChanger);
        mockAuthenticationController = mock(AuthenticationController.class);
        when(mockAuthenticationController.isLoggedIn()).thenReturn(true);
        UserSessionData currentUser = new UserSessionData();                
        UserProfile currentUserProfile = new UserProfile();
        currentUserProfile.setOwnerId("1");
        currentUser.setProfile(currentUserProfile);
        when(mockAuthenticationController.getCurrentUserSessionData()).thenReturn(currentUser);
        ars = new ArrayList<AccessRequirement>();
        AsyncMockStubber.callSuccessWith(true).when(mockSynapseClient).hasAccess(anyString(), anyString(), anyString(), any(AsyncCallback.class));
        AsyncMockStubber.callSuccessWith(ars).when(mockSynapseClient).getTeamAccessRequirements(anyString(), any(AsyncCallback.class));
        
		
		joinWidget = new JoinTeamWidget(mockView, mockSynapseClient, mockGlobalApplicationState, mockAuthenticationController, mockNodeModelCreator, jsonObjectAdapter, mockGwt);
		TeamMembershipStatus status = new TeamMembershipStatus();
		status.setHasOpenInvitation(false);
		status.setCanJoin(false);
		status.setHasOpenRequest(false);
		status.setIsMember(false);
		joinWidget.configure(teamId, false, false, status, mockTeamUpdatedCallback, null, null, null);
		
		AsyncMockStubber.callSuccessWith(null).when(mockSynapseClient).deleteOpenMembershipRequests(anyString(), anyString(), any(AsyncCallback.class));
		AsyncMockStubber.callSuccessWith(null).when(mockSynapseClient).requestMembership(anyString(), anyString(), anyString(), any(AsyncCallback.class));
		
		AsyncMockStubber.callSuccessWith(null).when(mockSynapseClient).createAccessApproval(any(EntityWrapper.class), any(AsyncCallback.class));
	}
	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testDeleteAllJoinRequests() throws Exception {
//		joinWidget.deleteAllJoinRequests();
//		verify(mockSynapseClient).deleteOpenMembershipRequests(anyString(), anyString(), any(AsyncCallback.class));
//		verify(mockView).showInfo(anyString(), anyString());
//		verify(mockTeamUpdatedCallback).invoke();
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testDeleteAllJoinRequestsFailure() throws Exception {
//		AsyncMockStubber.callFailureWith(new Exception("unhandled exception")).when(mockSynapseClient).deleteOpenMembershipRequests(anyString(), anyString(), any(AsyncCallback.class));
//		joinWidget.deleteAllJoinRequests();
//		verify(mockSynapseClient).deleteOpenMembershipRequests(anyString(), anyString(), any(AsyncCallback.class));
//		verify(mockView).showErrorMessage(anyString());
//	}
	
	@Test
	public void testJoinRequestStep1() throws Exception {
		WikiPageKey challengeInfoKey = mock(WikiPageKey.class);
		joinWidget.sendJoinRequestStep0();
		joinWidget.sendJoinRequestStep1(challengeInfoKey);
		verify(mockView).showChallengeInfoPage(any(UserProfile.class), eq(challengeInfoKey), any(Callback.class));
		verify(mockView, times(2)).setButtonsEnabled(anyBoolean());
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void testJoinRequestStep2WithRestriction() throws Exception {
		ars.add(new TermsOfUseAccessRequirement());
        
        joinWidget.sendJoinRequestStep0();
		verify(mockSynapseClient).getTeamAccessRequirements(anyString(), any(AsyncCallback.class));
		verify(mockView).showTermsOfUseAccessRequirement(anyString(), any(Callback.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testJoinRequestStep2WithACTRestriction() throws Exception {
		ars.add(new ACTAccessRequirement());
        
        joinWidget.sendJoinRequestStep0();
		verify(mockSynapseClient).getTeamAccessRequirements(anyString(), any(AsyncCallback.class));
		verify(mockView).showACTAccessRequirement(anyString(), any(Callback.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testJoinRequestStep2NoRestriction() throws Exception {
		joinWidget.sendJoinRequestStep0();
		verify(mockSynapseClient).getTeamAccessRequirements(anyString(), any(AsyncCallback.class));
		//no ARs shown...
		verify(mockView, times(0)).showTermsOfUseAccessRequirement(anyString(), any(Callback.class));
		verify(mockSynapseClient).requestMembership(anyString(), anyString(), anyString(), any(AsyncCallback.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetTotalPageCount() throws Exception {
		boolean isChallengeSignup = true;
        joinWidget.configure(teamId, false, isChallengeSignup, null, mockTeamUpdatedCallback, null, null, null);
        joinWidget.sendJoinRequestStep0();
        //one page for the AR
        Assert.assertEquals(1, joinWidget.getTotalPageCount());
        
        isChallengeSignup = false;
        joinWidget.configure(teamId, false, isChallengeSignup, null, mockTeamUpdatedCallback, null, null, null);
        joinWidget.sendJoinRequestStep0();
        //0 pages
        Assert.assertEquals(0, joinWidget.getTotalPageCount());
		

        //Now test with an access requirement.  First as part of a challenge, then outside of challenge 
        ars.add(new TermsOfUseAccessRequirement());
        
        isChallengeSignup = true;
        joinWidget.configure(teamId, false, isChallengeSignup, null, mockTeamUpdatedCallback, null, null, null);
        joinWidget.sendJoinRequestStep0();
        //One page for challenge info, one page for the AR
        Assert.assertEquals(2, joinWidget.getTotalPageCount());
        
        isChallengeSignup = false;
        joinWidget.configure(teamId, false, isChallengeSignup, null, mockTeamUpdatedCallback, null, null, null);
        joinWidget.sendJoinRequestStep0();
        //One page for challenge info
        Assert.assertEquals(1, joinWidget.getTotalPageCount());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testJoinRequestStep2Failure() throws Exception {
		AsyncMockStubber.callFailureWith(new Exception("unhandled exception")).when(mockSynapseClient).getTeamAccessRequirements(anyString(), any(AsyncCallback.class));
		joinWidget.sendJoinRequestStep0();
		joinWidget.sendJoinRequestStep2();
		verify(mockSynapseClient).getTeamAccessRequirements(anyString(), any(AsyncCallback.class));
		verify(mockView).showErrorMessage(anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJoinRequestStep3() throws Exception {
		joinWidget.sendJoinRequestStep3();
		verify(mockSynapseClient).requestMembership(anyString(), anyString(), anyString(), any(AsyncCallback.class));
		verify(mockView).showInfo(anyString(), anyString());
		//verify that team updated callback is invoked
		verify(mockTeamUpdatedCallback).invoke();
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void testJoinRequestStep3Failure() throws Exception {
		AsyncMockStubber.callFailureWith(new Exception("unhandled exception")).when(mockSynapseClient).requestMembership(anyString(), anyString(),anyString(), any(AsyncCallback.class));
		joinWidget.sendJoinRequest("", false);
		verify(mockSynapseClient).requestMembership(anyString(), anyString(), anyString(), any(AsyncCallback.class));
		verify(mockView).showErrorMessage(anyString());
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testJoinRequestStep3WikiRefresh() throws Exception {
		//configure using wiki widget renderer version
		Map<String, String> descriptor = new HashMap<String, String>();
		descriptor.put(WidgetConstants.JOIN_WIDGET_TEAM_ID_KEY, teamId);
		descriptor.put(WidgetConstants.JOIN_WIDGET_SHOW_PROFILE_FORM_KEY, Boolean.TRUE.toString());
		Callback mockWidgetRefreshRequired = mock(Callback.class);
		joinWidget.configure(null, descriptor, mockWidgetRefreshRequired, null);
		joinWidget.sendJoinRequestStep3();
		verify(mockSynapseClient).requestMembership(anyString(), anyString(), anyString(), any(AsyncCallback.class));
		verify(mockView).showInfo(anyString(), anyString());
		//verify that wiki page refresh is invoked
		verify(mockWidgetRefreshRequired).invoke();
	}
	
	@Test
	public void testShowChallengeInfoParam() throws Exception {
		Map<String, String> descriptor = new HashMap<String, String>();
		descriptor.put(WidgetConstants.JOIN_WIDGET_TEAM_ID_KEY, teamId);
		descriptor.put(WidgetConstants.JOIN_WIDGET_SHOW_PROFILE_FORM_KEY, Boolean.TRUE.toString());
		Callback mockWidgetRefreshRequired = mock(Callback.class);
		joinWidget.configure(null, descriptor, mockWidgetRefreshRequired, null);
		Assert.assertTrue(joinWidget.isChallengeSignup());
		
		descriptor.put(WidgetConstants.JOIN_WIDGET_SHOW_PROFILE_FORM_KEY, Boolean.FALSE.toString());
		joinWidget.configure(null, descriptor, mockWidgetRefreshRequired, null);
		Assert.assertFalse(joinWidget.isChallengeSignup());
		
		//if both params are defined, the new param is used
		descriptor.put(WebConstants.JOIN_WIDGET_IS_CHALLENGE_KEY, Boolean.TRUE.toString());
		joinWidget.configure(null, descriptor, mockWidgetRefreshRequired, null);
		Assert.assertTrue(joinWidget.isChallengeSignup());
		
		descriptor.remove(WidgetConstants.JOIN_WIDGET_SHOW_PROFILE_FORM_KEY);
		descriptor.put(WebConstants.JOIN_WIDGET_IS_CHALLENGE_KEY, Boolean.FALSE.toString());
		joinWidget.configure(null, descriptor, mockWidgetRefreshRequired, null);
		Assert.assertFalse(joinWidget.isChallengeSignup());
	}

	@Test
	public void testSetLicenseAccepted() throws Exception {
		//single ToU
		AccessRequirement ar = new TermsOfUseAccessRequirement();
		ars.add(ar);
		joinWidget.sendJoinRequestStep0();
		//verify we showTermsOfUseAccessRequirement
		ArgumentCaptor<Callback> callbackArg = ArgumentCaptor.forClass(Callback.class);
		verify(mockView).showTermsOfUseAccessRequirement(anyString(), callbackArg.capture());
		
		//manually invoke the okbutton callback
		callbackArg.getValue().invoke();
		
        //it should try to sign this type of ar
        verify(mockSynapseClient).createAccessApproval(any(EntityWrapper.class), any(AsyncCallback.class));
        verify(mockView).hideJoinWizard();
	}
	
	@Test
	public void testSetLicenseAcceptedACT() throws Exception {
		//single ToU
		AccessRequirement ar = new ACTAccessRequirement();
		ars.add(ar);
		joinWidget.sendJoinRequestStep0();
		//verify we showTermsOfUseAccessRequirement
		ArgumentCaptor<Callback> callbackArg = ArgumentCaptor.forClass(Callback.class);
		verify(mockView).showACTAccessRequirement(anyString(), callbackArg.capture());
		
		//manually invoke the okbutton callback
		callbackArg.getValue().invoke();
				
        //verify it does not try to sign, we just continue
        verify(mockSynapseClient, never()).createAccessApproval(any(EntityWrapper.class), any(AsyncCallback.class));
        verify(mockView).hideJoinWizard();
	}

	
	@Test
	public void testSetLicenseAcceptedPostMessage() throws Exception {
		//single ToU
		AccessRequirement ar = new PostMessageContentAccessRequirement();
		ars.add(ar);
		joinWidget.sendJoinRequestStep0();
		//verify we showTermsOfUseAccessRequirement
		ArgumentCaptor<Callback> callbackArg = ArgumentCaptor.forClass(Callback.class);
		verify(mockView).showPostMessageContentAccessRequirement(anyString(), callbackArg.capture());
		
		//manually invoke the okbutton callback
		callbackArg.getValue().invoke();
				
        //verify it does not try to sign, we just continue
        verify(mockSynapseClient).createAccessApproval(any(EntityWrapper.class), any(AsyncCallback.class));
        verify(mockView).hideJoinWizard();
	}
	
	@Test
	public void testSetLicenseAcceptedInvalidType() throws Exception {
		//single ToU
		AccessRequirement ar = mock(AccessRequirement.class);
		ars.add(ar);
		joinWidget.sendJoinRequestStep0();
		//verify we show an error for an unrecognized ar
		verify(mockView).showErrorMessage(anyString());
	}
}
