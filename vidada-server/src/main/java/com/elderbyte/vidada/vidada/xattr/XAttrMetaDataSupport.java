package com.elderbyte.vidada.vidada.xattr;

import com.sun.jna.platform.mac.XAttrUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * OS X implementation for xattr using native C API
 *
 * @author IsNull
 *
 */
class XAttrMetaDataSupport implements IFileMetaDataSupport {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static IFileMetaDataSupport instance;

    /***************************************************************************
     *                                                                         *
     * Singleton                                                               *
     *                                                                         *
     **************************************************************************/

    /**
     * Get the platform specific XAttr implementation
     * @return
     */
    public synchronized static IFileMetaDataSupport getInstance() throws MetaDataNotSupportedException{
        if(instance == null)
        {
            try {
                instance = new XAttrMetaDataSupport();
            } catch (Exception e) {
                throw new MetaDataNotSupportedException("OS X native metadata support is not available.", e);
            }
        }
        return instance;
    }

    /**
     * Private Singleton constructor
     * @throws UnsatisfiedLinkError
     * @throws IOException
     */
    private XAttrMetaDataSupport() throws UnsatisfiedLinkError, IOException{

    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**{@inheritDoc}*/
    @Override
    public boolean isMetaDataSupported(File file) {
        // xattr emulates the attributes with the "._" Prefix
        // on file systems which do not support extended attributes
        return true;
    }

    /**
     * {@inheritDoc}
     * xattr -w [-rsx] attr_name attr_value file ...
     *
     */
    @Override
    public void writeAttribute(File file, String attrKey, String attrValue) throws MetadataIOException{
        if(XAttrUtil.setXAttr(file.getAbsolutePath(), attrKey,attrValue) != 0){
            throw new MetadataIOException(
                String.format("Writing attribute '%s' with value '%s' to file '%s' failed.", attrKey, attrValue, file));
        }
    }

    /**
     * {@inheritDoc}
     * xattr -p [-lrsvx] attr_name file ...
     *
     */
    @Override
    public String readAttribute(File file, String attrKey) throws MetadataIOException{
        return XAttrUtil.getXAttr(file.getAbsolutePath(), attrKey);
    }

    @Override
    public void removeAttribute(File file, String attrKey) throws MetadataIOException {



        if(XAttrUtil.removeXAttr(file.getAbsolutePath(), attrKey) != 0){
            throw new MetadataIOException(
                    String.format("Removing attribute '%s' from file '%s' failed.", attrKey, file ));
        }
    }

    @Override
    public List<String> listAttributes(File file) throws MetadataIOException {

        List<String> attr = XAttrUtil.listXAttr(file.getAbsolutePath());

        if(attr == null){
            throw new MetadataIOException(
                String.format("Listing attributes for file '%s' failed.", file));
        }
        return attr;
    }
}
