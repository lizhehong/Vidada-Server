package com.elderbyte.server.vidada.xattr;

import com.elderbyte.common.locations.ResourceLocation;
import ch.securityvision.metadata.FileMetaDataSupportFactory;
import ch.securityvision.metadata.IFileMetaDataSupport;
import ch.securityvision.metadata.MetaDataNotSupportedException;
import ch.securityvision.metadata.MetadataIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Provides read / write functionality of metadata stored in extended attributes.
 */
@Service
public class XAttrMetadataService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private final IFileMetaDataSupport xattrUtil;

    /**
     * Creates a new MediaMetaDataService
     */
    public XAttrMetadataService() {
        IFileMetaDataSupport xattrUtil = null;
        try {
            xattrUtil = FileMetaDataSupportFactory.buildFileMetaSupport();
        } catch (MetaDataNotSupportedException e) {
            LOG.error("Metadata is not supported on this operating system.", e);
        }
        this.xattrUtil = xattrUtil;
    }



    /**
     * Does the given file (file-system where the file resides) support meta-data?
     * @param location
     * @return
     */
    public boolean isMetaDataSupported(ResourceLocation location){
        if(isFileUri(location)){
            File file = new File(location.getUri());
            return xattrUtil != null && xattrUtil.isMetaDataSupported(file);
        }
        return false;
    }



    /**
     * Write the given attribute value to the given file
     * @param location
     * @param attribute
     * @param value
     */
    public boolean writeMetaData(ResourceLocation location, KnownXAttr attribute, String value){
        boolean success = false;
        File file = new File(location.getUri());
        try {
            if(xattrUtil != null) {
                xattrUtil.writeAttribute(file, attribute.getAttributeName(), value);
                success = true;
            }
        } catch (MetadataIOException e) {
            if(!file.canWrite()){
                LOG.warn("Can not write meta-data since file is read-only: " + file);
            }

            LOG.error("Can not write meta-data!", e);
        }
        return success;
    }

    /**
     * Read the given attribute from the given files meta data.
     *
     * If the attribute does not exists or when it could not be retrieved,
     * NULL is returned.
     *
     * @param file
     * @param attribute
     * @return
     */
    public String readMetaData(ResourceLocation file, KnownXAttr attribute){
        try {
            if(xattrUtil != null) {
                return xattrUtil.readAttribute(new File(file.getUri()), attribute.getAttributeName());
            }
        } catch (MetadataIOException e) {
            LOG.error("Could not read metadata!", e);
        }
        return null;
    }

    /**
     * Lists all meta-data attributes of the given file
     * @param file the file from which all attributes should be listed.
     * @return All found attributes
     */
    public List<String> listAllAttributes(ResourceLocation file){
        try {
            if(xattrUtil != null) {
                return xattrUtil.listAttributes(new File(file.getUri()));
            }
        } catch (MetadataIOException e) {
            LOG.error("Can not list all meta-data attributes!", e);
        }
        return null;
    }

    private static boolean isFileUri(ResourceLocation file){
        return file.getUri().getScheme().contains("file");
    }

}
