package archimedes.core.io.locations;

import archimedes.core.io.locations.factories.ResourceLocationFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a general resource location to a file.
 * This can be a local file path, a smb share, web resource or any other valid URI / Credital combo
 *
 * @author IsNull
 */
@Embeddable
public abstract class ResourceLocation  extends UniformLocation {

	transient public static ResourceLocationFactory Factory = new ResourceLocationFactory();
	transient private static final  Pattern extensionRegex = java.util.regex.Pattern.compile(".*(\\..{1,5})$");


	public ResourceLocation(URI uri) {
		this(uri, null);
	}

	public ResourceLocation(URI uri, Credentials creditals) {
		super(uri, creditals);
	}


	/**
	 * Returns the extension ".ext" of this location if applicable
	 * or an empty string otherwise
	 */
	@Transient
	public String getExtension(){
		Matcher m = extensionRegex.matcher(getUri().getPath());
		return m.matches() ? m.group(1) : "no match!";
	}

	/**
	 * Gets the file name with extension
	 * @return
	 */
	@Transient
	@Override
	public String getName(){
		String filename = FilenameUtils.getName(getUri().toString());
		try {
			return URLDecoder.decode(filename,  "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads the whole file into a byte buffer
	 * @return
	 */
	public byte[] readAllBytes() throws IOException{
		byte[] bytes = null;
        try (InputStream resourceStream = this.openInputStream()) {
            bytes = IOUtils.toByteArray(resourceStream);
        }
		return bytes;
	}

	/**
	 * Writes all bytes to this file.
	 * If the file does not exists, it will be created.
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public void writeAllBytes(byte[] bytes) throws IOException{
        try (OutputStream resourceOutputStream = this.openOutputStream()) {
            resourceOutputStream.write(bytes);
        }
	}

	/**
	 * Open an input stream to the given file for reading
	 * @return
	 */
	public abstract InputStream openInputStream();

	/**
	 * Open an output stream to the given file for reading
	 * @return
	 */
	public abstract OutputStream openOutputStream();


	/**
	 * Open a access-context for this resource
	 * @return
	 */
	public abstract IResourceAccessContext openResourceContext();

	/**
	 * Gets the file size
	 * @return
	 */
	public abstract long length();
}
