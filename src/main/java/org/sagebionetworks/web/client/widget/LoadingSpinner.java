package org.sagebionetworks.web.client.widget;

import org.gwtbootstrap3.client.ui.html.Div;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.lazyload.LazyLoadHelper;
import org.sagebionetworks.web.client.widget.lazyload.SupportsLazyLoadInterface;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class LoadingSpinner implements IsWidget, SupportsLazyLoadInterface {
	
	public interface Binder extends UiBinder<Widget, LoadingSpinner> {}
	private static Binder uiBinder = GWT.create(Binder.class);
	Widget widget = null;
	
	@UiField
	Div loadingSpinnerDiv;
	Div spinnerContainer = new Div();
	Callback onAttachCallback;
	/**
	 * ## Usage
	 * 
	 * In your ui.xml, add the loading widget.
	 * ```
	 * xmlns:w="urn:import:org.sagebionetworks.web.client.widget"
	 * <w:LoadingSpinner size="100px" />
	 * ```
	 */
	public LoadingSpinner() {
		Callback isInViewportCallback = () -> {
			lazyConstruct();
		};
		spinnerContainer.addAttachHandler(event -> {
			if (event.isAttached()) {
				onAttachCallback.invoke();
			}
		});
		PortalGinInjector ginjector = GWT.create(PortalGinInjector.class);
		LazyLoadHelper lazyLoadHelper = ginjector.getLazyLoadHelper();
		lazyLoadHelper.configure(isInViewportCallback, this);
		lazyLoadHelper.setIsConfigured();
	}
	
	@Override
	public boolean isAttached() {
		return spinnerContainer.isAttached();
	}
	@Override
	public boolean isInViewport() {
		return DisplayUtils.isInViewport(spinnerContainer) && spinnerContainer.isVisible();
	}
	private void lazyConstruct() {
		if (widget == null) {
			widget = uiBinder.createAndBindUi(this);
		}
		spinnerContainer.clear();
		spinnerContainer.add(widget); 
	}
	
	@Override
	public void setOnAttachCallback(Callback onAttachCallback) {
		this.onAttachCallback = onAttachCallback;
	}
	@Override
	public Widget asWidget() {
		return spinnerContainer;
	}
	
	public void setVisible(boolean visible) {
		spinnerContainer.clear();
		if (visible) {
			lazyConstruct();
		} 
		
		spinnerContainer.setVisible(visible);
	}
	
	public void setSize(int px) {
		setSize(px + "px");
	}
	
	public void setSize(String size) {
		spinnerContainer.setHeight(size);
		spinnerContainer.setWidth(size);
	}
	
	public void setAddStyleNames(String styleNames) {
		spinnerContainer.addStyleName(styleNames);
	}
	
}
