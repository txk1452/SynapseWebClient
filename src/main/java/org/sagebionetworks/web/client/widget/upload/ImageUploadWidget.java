package org.sagebionetworks.web.client.widget.upload;

import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ImageUploadWidget implements ImageUploadView.Presenter, IsWidget {
	private ImageUploadView view;
	private MultipartUploader multipartUploader;
	private SynapseAlert synAlert;
	private CallbackP<FileUpload> finishedUploadingCallback;
	private Callback startedUploadingCallback;
	private SynapseJSNIUtils synapseJsniUtils;
	private FileMetadata fileMeta;
	@Inject
	public ImageUploadWidget(ImageUploadView view, MultipartUploader multipartUploader,
			SynapseJSNIUtils synapseJsniUtils, SynapseAlert synAlert) {
		super();
		this.view = view;
		this.synAlert = synAlert;
		this.multipartUploader = multipartUploader;
		this.synapseJsniUtils = synapseJsniUtils;
		this.view.setPresenter(this);
		view.setSynAlert(synAlert);
	}

	@Override
	public Widget asWidget() {
		return this.view.asWidget();
	}

	public void configure(CallbackP<FileUpload> finishedUploadingCallback) {
		this.finishedUploadingCallback = finishedUploadingCallback;
		view.showProgress(false);
	}
	
	public void setUploadingCallback(Callback startedUploadingCallback) {
		this.startedUploadingCallback = startedUploadingCallback;
	}
	
	public void setUploadedFileText(String text) {
		view.setUploadedFileText(text);
	}
	
	public FileMetadata getSelectedFileMetadata() {
		String inputId = view.getInputId();
		String[] fileNames = synapseJsniUtils.getMultipleUploadFileNames(inputId);
		if(fileNames != null && fileNames.length > 0){
			String name = fileNames[0];
			double fileSize = synapseJsniUtils.getFileSize(synapseJsniUtils.getFileBlob(0, inputId));
			String contentType = synapseJsniUtils.getContentType(inputId, 0);
			return new FileMetadata(name, contentType, fileSize);
		}
		return null;
	}
	
	@Override
	public void resizeComplete(JavaScriptObject blob) {
		synAlert.clear();
		fileMeta = getSelectedFileMetadata();
		if (fileMeta != null && blob != null) {
			if (startedUploadingCallback != null) {
				startedUploadingCallback.invoke();
			}
			view.updateProgress(1, "1%");
			view.showProgress(true);
			view.setInputEnabled(false);
			doMultipartUpload(fileMeta, blob);
		}
	}
	
	public void reset() {
		view.setInputEnabled(true);
		view.showProgress(false);
		synAlert.clear();
		view.resetForm();
	}
	
	private void doMultipartUpload(final FileMetadata fileMeta, JavaScriptObject blob) {
		// The uploader does the real work
		multipartUploader.uploadFile(fileMeta.getFileName(), "image/jpeg", blob,
			new ProgressingFileUploadHandler() {
				@Override
				public void uploadSuccess(String fileHandleId) {
					FileUpload uploadedFile = new FileUpload(fileMeta, fileHandleId);
					finishedUploadingCallback.invoke(uploadedFile);
				}

				@Override
				public void uploadFailed(String error) {
					view.showProgress(false);
					view.setInputEnabled(true);
					synAlert.showError(error);
				}

				@Override
				public void updateProgress(double currentProgress,
						String progressText, String uploadSpeed) {
					int totalProgress = (int)(currentProgress * 100);
					view.updateProgress(totalProgress, totalProgress + "%");
				}
		}, null, view);
	}

}
