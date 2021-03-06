package org.sagebionetworks.web.client.widget.search;

import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.TextBox;
import org.sagebionetworks.web.client.DisplayUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SearchBoxViewImpl implements SearchBoxView {
	public interface Binder extends UiBinder<Widget, SearchBoxViewImpl> {}
	private Presenter presenter;
	Widget widget;
	@UiField
	Icon searchButton;
	@UiField
	TextBox searchField;
	public static final String INACTIVE_STYLE = "inactive";
	public static final String ACTIVE_STYLE = "active";
	
	@Inject
	public SearchBoxViewImpl(Binder binder) {
		widget = binder.createAndBindUi(this);
		searchField.getElement().setAttribute("placeholder", " Search all of Synapse");
		initClickHandlers();
	}

	private void initClickHandlers() {
	    searchField.addKeyDownHandler(new KeyDownHandler() {				
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					executeSearch();
	            }					
			}
		});
	    searchField.addBlurHandler(event -> {
	    	searchFieldInactive();
	    });
	    searchButton.addClickHandler(event -> {
	    	searchField.setFocus(true);
	    	if (isSearchFieldActive()) {
	    		executeSearch();
	    	} else {
	    		searchFieldActive();	
	    	}
	    });
	}
	
	private void executeSearch() {
		if (!"".equals(searchField.getValue())) {
			presenter.search(searchField.getValue());
			searchField.setValue("");
			searchFieldInactive();
		}
	}
	
	@Override
	public Widget asWidget() {		
		return widget;
	}	

	@Override 
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
		
	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void showLoading() {
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}
	
	@Override
	public void setVisible(boolean isVisible) {
		widget.setVisible(isVisible);
	}
	
	@Override
	public void clear() {
		//searchField.setText("");		
	}
	
	private void searchFieldInactive() {
		Element searchBoxContainer = _getSearchBoxContainer();
		searchBoxContainer.removeClassName(ACTIVE_STYLE);
		searchBoxContainer.addClassName(INACTIVE_STYLE);
	}
	private void searchFieldActive() {
		Element searchBoxContainer = _getSearchBoxContainer();
		searchBoxContainer.addClassName(ACTIVE_STYLE);
		searchBoxContainer.removeClassName(INACTIVE_STYLE);
	}
	
	private boolean isSearchFieldActive() {
		Element searchBoxContainer = _getSearchBoxContainer();
		return searchBoxContainer.hasClassName(ACTIVE_STYLE);
	}
	
	public static native Element _getSearchBoxContainer() /*-{
		try {
			return $wnd.jQuery('#headerPanel .searchBoxContainer')[0];
		} catch (err) {
			console.error(err);
		}
	}-*/;
}
