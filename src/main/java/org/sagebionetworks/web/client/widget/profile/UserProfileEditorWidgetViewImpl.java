package org.sagebionetworks.web.client.widget.profile;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserProfileEditorWidgetViewImpl implements
		UserProfileEditorWidgetView {
	
	public interface Binder extends UiBinder<Widget, UserProfileEditorWidgetViewImpl> {}
	
	@UiField
	SimplePanel imagePanel;
	@UiField
	SimplePanel fileInputWidgetPanel;
	@UiField
	Alert uploadAlert;
	@UiField
	Button uploadFileButton;
	@UiField
	FormGroup usernameFormGroup;
	@UiField
	HelpBlock usernameHelpBlock;
	
	@UiField
	TextBox username;
	@UiField
	TextBox firstName;
	@UiField
	TextBox lastName;
	@UiField
	TextArea bio;
	
	private Widget widget;
	
	@Inject
	public UserProfileEditorWidgetViewImpl(Binder binder){
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setUsername(String username) {
		this.username.setText(username);
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName.setText(firstName);
	}

	@Override
	public String getFirstName() {
		return this.firstName.getText();
	}

	@Override
	public String getLastName() {
		return this.lastName.getText();
	}

	@Override
	public String getUsername() {
		return username.getText();
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName.setText(lastName);
	}

	@Override
	public void setBio(String summary) {
		this.bio.setText(summary);
	}

	@Override
	public String getBio() {
		return this.bio.getText();
	}

	@Override
	public void showUsernameError(String error) {
		usernameFormGroup.setValidationState(ValidationState.ERROR);
		usernameHelpBlock.setText(error);
	}

	@Override
	public void hideUsernameError() {
		usernameFormGroup.setValidationState(ValidationState.NONE);
		usernameHelpBlock.setText("");
	}

	@Override
	public void addImageWidget(IsWidget image) {
		imagePanel.add(image);
	}

	@Override
	public void addFileInputWidget(IsWidget fileInputWidget) {
		fileInputWidgetPanel.add(fileInputWidget);
	}

	@Override
	public void setPresenter(final Presenter presenter) {
		uploadFileButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onUploadFile();
			}
		});
	}

	@Override
	public void showUploadError(String string) {
		uploadAlert.setText(string);
		uploadAlert.setVisible(true);
	}

	@Override
	public void hideUploadError() {
		uploadAlert.setVisible(false);
	}

	@Override
	public void setUploading(boolean uploading) {
		if(uploading){
			uploadFileButton.state().loading(); 
		}else{
			uploadFileButton.state().reset(); 
		}
	}

}
