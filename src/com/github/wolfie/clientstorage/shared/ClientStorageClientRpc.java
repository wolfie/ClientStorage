package com.github.wolfie.clientstorage.shared;

import com.vaadin.shared.communication.ClientRpc;

public interface ClientStorageClientRpc extends ClientRpc {
	void setItem(Scope scope, String key, String value);

	void getItem(Scope scope, String uuid, String key);

	void removeItem(Scope scope, String key);
}
