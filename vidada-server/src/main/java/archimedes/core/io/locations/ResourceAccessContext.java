package archimedes.core.io.locations;

import java.io.IOException;
import java.net.URI;

/**
 * Represents a context in which a resource can be accessed.
 * After usage, the resource context should be closed.
 * 
 * The resource is located at the provided URL, so it can be any server/service provider. 
 * 
 * The resource might also be a static kind such as a local file path - in this case, close has no effect,
 * and this resource context might be valid even after "closing" it.
 * 
 * 
 * @author IsNull
 */
public abstract class ResourceAccessContext implements IResourceAccessContext {

	private URI uri;

	protected ResourceAccessContext(URI uri){
		this.uri = uri;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	protected void setUri(URI uri){
		this.uri = uri;
	}

	@Override
	public abstract boolean isOpen();

	@Override
	public abstract void close() throws IOException;
}
