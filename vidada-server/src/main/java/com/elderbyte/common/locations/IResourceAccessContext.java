package com.elderbyte.common.locations;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

/**
 * Represents a context in which a resource can be accessed.
 * After usage, the resource context should be closed.
 *
 * The resource might be a URL to a service offering a resource.
 *
 * The resource might also be a static kind such as a local file path - in this case, close has no effect,
 * and this resource context might be valid even after "closing" it.
 *
 *
 * @author IsNull
 */
public interface IResourceAccessContext extends Closeable {

	/**
	 * Gets the Uri to access the resource
	 * @return
	 */
	public URI getUri();

	/**
	 * Is the resource accessible / context valid?
	 * @return
	 */
	public boolean isOpen();

	/**
	 * Notify that this resource context no longer is required.
	 * This does NOT guarantee that the resource is actually no longer available under the given Uri.
	 *
	 * Details depend on the concrete implementation.
	 */
	@Override
	public void close() throws IOException;
}
