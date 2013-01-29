package com.github.wolfie.clientstorage;

import com.github.wolfie.clientstorage.ClientStorage.ClientStorageSupportListener;
import com.github.wolfie.clientstorage.ClientStorage.Closure;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class ClientStorageUi extends UI {

	private static final String KEY = "EXAMPLE_KEY";
	private ClientStorage clientStorage;
	private int ignoreValueChange = 0;
	private TextField sessionStorageField;
	private TextField localStorageField;

	@Override
	protected void init(final VaadinRequest request) {
		final VerticalLayout vlayout = new VerticalLayout();
		vlayout.setMargin(true);
		setContent(vlayout);

		final Label label = new Label("The following is stored into, and "
				+ "retreived from, HTML5 storage");
		label.setStyleName(Reindeer.LABEL_H2);
		vlayout.addComponent(label);

		final FormLayout layout = new FormLayout();
		vlayout.addComponent(layout);

		localStorageField = new TextField("Local Storage");
		localStorageField.setImmediate(true);
		localStorageField.setInputPrompt("No value stored");
		localStorageField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				if (ignoreValueChange == 0) {
					clientStorage.setLocalItem(KEY,
							localStorageField.getValue());
				}
			}
		});
		layout.addComponent(localStorageField);
		layout.addComponent(new Button("Clear Local Storage",
				new ClickListener() {
					@Override
					public void buttonClick(final ClickEvent event) {
						clientStorage.removeLocalItem(KEY);
						updateClientStorage();
					}
				}));

		sessionStorageField = new TextField("Session Storage");
		sessionStorageField.setImmediate(true);
		sessionStorageField.setInputPrompt("No value stored");
		sessionStorageField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				if (ignoreValueChange == 0) {
					clientStorage.setSessionItem(KEY,
							sessionStorageField.getValue());
				}
			}
		});
		layout.addComponent(sessionStorageField);
		layout.addComponent(new Button("Clear Session Storage",
				new ClickListener() {
					@Override
					public void buttonClick(final ClickEvent event) {
						clientStorage.removeSessionItem(KEY);
						updateClientStorage();
					}
				}));

		vlayout.addComponent(new Link("More information about HTML5 storage",
				new ExternalResource("https://developers.google.com"
						+ "/web-toolkit/doc/latest/"
						+ "DevGuideHtml5Storage#GwtStorage")));

		clientStorage = new ClientStorage(new ClientStorageSupportListener() {
			@Override
			public void clientStorageIsSupported(final boolean supported) {
				if (!supported) {
					layout.removeComponent(localStorageField);
					layout.removeComponent(sessionStorageField);
					layout.addComponent(new Label(
							"This browser doesn't support HTML5 storage"));
				}
			}
		});

		addExtension(clientStorage);

		updateClientStorage();
		updateSessionStorage();
	}

	private void updateSessionStorage() {
		clientStorage.getSessionItem(KEY, new Closure() {
			@Override
			public void execute(final String value) {
				ignoreValueChange++;
				sessionStorageField.setValue(value == null ? "" : value);
				ignoreValueChange--;
			}
		});
	}

	private void updateClientStorage() {
		clientStorage.getLocalItem(KEY, new Closure() {
			@Override
			public void execute(final String value) {
				ignoreValueChange++;
				localStorageField.setValue(value == null ? "" : value);
				ignoreValueChange--;
			}
		});
	}

}