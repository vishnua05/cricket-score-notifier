package scorenotifierplugin.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class PullDownAction implements IViewActionDelegate, IMenuCreator {

	private Menu menu;

	public void init(IViewPart view) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
		if (getAvailableItems().size() > 0) {
			handleSelection(getAvailableItems().get(0));
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setMenuCreator(this);
	}

	public void dispose() {
		if (menu != null) {
			menu.dispose();
		}
	}

	public Menu getMenu(Control parent) {
		if (menu != null) {
			menu.dispose();
		}
		menu = new Menu(parent);
		Collection<String> availableItems = getAvailableItems();
		for (final String menuItemId : availableItems) {
			MenuItem menuItem = new MenuItem(menu, getMenuStyle());
			menuItem.setText(getDisplayName(menuItemId));
			menuItem.setSelection(getSelection(menuItemId));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleSelection(menuItemId);
				}
			});
		}
		return menu;
	}

	protected int getMenuStyle() {
		return SWT.PUSH;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}

	protected boolean getSelection(String menuItemId) {
		return false;
	}

	protected List<String> getAvailableItems() {
		return Collections.<String> emptyList();
	}

	protected String getDisplayName(String menuItemId) {
		return menuItemId;
	}

	protected void handleSelection(String menuItemId) {

	}

}
