package org.sagebionetworks.web.client.widget.header;

import java.util.List;

import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.UserSessionData;

import com.google.gwt.user.client.ui.IsWidget;

public interface HeaderView extends IsWidget {
	/**
	 * Set the presenter.
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	public void refresh();
	/**
	 * Sets the search box to visible or not
	 * @param searchVisible
	 */
	public void setSearchVisible(boolean searchVisible);
	/**
	 * Presenter interface
	 */
	public interface Presenter {
		void onTrashClick();
		void onLogoutClick();
		void onLoginClick();
		void refreshFavorites();
		void onLogoClick();
	}
	public void clearFavorite();
	public void setEmptyFavorite();
	public void addFavorite(List<EntityHeader> headers);
	void setUser(UserSessionData userData);
	void setProjectHeaderText(String text);
	void setProjectHeaderAnchorTarget(String href);
	void setProjectFavoriteWidget(IsWidget favWidget);
	void showProjectFavoriteWidget();
	void hideProjectFavoriteWidget();
	void setStagingAlertVisible(boolean visible);
	void openNewWindow(String url);
	void clear();
}
