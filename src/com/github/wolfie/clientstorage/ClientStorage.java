package com.github.wolfie.clientstorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.wolfie.clientstorage.shared.ClientStorageClientRpc;
import com.github.wolfie.clientstorage.shared.ClientStorageServerRpc;
import com.github.wolfie.clientstorage.shared.Scope;
import com.vaadin.server.AbstractExtension;

public class ClientStorage extends AbstractExtension {
	public interface Closure {
		void execute(String value);
	}

	public interface ClientStorageSupportListener {
		void clientStorageIsSupported(boolean supported);
	}

	private static final long serialVersionUID = 158272621759938318L;
	private final ClientStorageClientRpc rpcProxy;

	private final Map<String, Closure> closureMap = new HashMap<String, Closure>();

	/** <code>null</code> means that the answer hasn't been answered yet */
	private Boolean supported = null;

	public ClientStorage(final ClientStorageSupportListener supportListener) {
		rpcProxy = getRpcProxy(ClientStorageClientRpc.class);
		registerRpc(new ClientStorageServerRpc() {
			private static final long serialVersionUID = -2862265177044395981L;

			@Override
			public void returnValue(final String uuid, final String value) {
				final Closure closure = closureMap.get(uuid);
				if (closure != null) {
					closure.execute(value);
				}
			}

			@Override
			public void setSupport(final boolean supported) {
				ClientStorage.this.supported = supported;
				supportListener.clientStorageIsSupported(supported);
			}
		});
	}

	public void setLocalItem(final String key, final String value) {
		setItem(Scope.LOCAL, key, value);
	}

	public void setSessionItem(final String key, final String value) {
		setItem(Scope.SESSION, key, value);
	}

	private void setItem(final Scope scope, final String key, final String value) {
		checkForSupport();
		rpcProxy.setItem(scope, key, value);
	}

	public void getLocalItem(final String key, final Closure closure) {
		getItem(Scope.LOCAL, key, closure);
	}

	public void getSessionItem(final String key, final Closure closure) {
		getItem(Scope.SESSION, key, closure);
	}

	private void getItem(final Scope scope, final String key,
			final Closure closure) {
		checkForSupport();
		final String uuid = UUID.randomUUID().toString();
		closureMap.put(uuid, closure);
		rpcProxy.getItem(scope, uuid, key);
	}

	private void checkForSupport() {
		if (Boolean.FALSE.equals(supported)) {
			throw new UnsupportedOperationException("ClientStorage "
					+ "isn't supported by the user's browser. "
					+ "Make sure that the "
					+ ClientStorageSupportListener.class.getSimpleName()
					+ " disables all ClientStorage "
					+ "access if there is no support");
		}
	}

	public void removeSessionItem(final String key) {
		removeItem(Scope.SESSION, key);
	}

	public void removeLocalItem(final String key) {
		removeItem(Scope.LOCAL, key);
	}

	private void removeItem(final Scope scope, final String key) {
		rpcProxy.removeItem(scope, key);
	}
}
