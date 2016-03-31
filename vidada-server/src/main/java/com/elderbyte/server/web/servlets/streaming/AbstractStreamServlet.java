package com.elderbyte.server.web.servlets.streaming;

import com.elderbyte.common.streaming.ISeekableInputStream;
import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.server.web.IllegalHttpRequestException;
import com.elderbyte.server.web.servlets.AutowiredHttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * A servlet which supports streaming data to the client.
 * Support seeking (byte-ranges) to support large datasets.
 */
public abstract class AbstractStreamServlet extends AutowiredHttpServlet {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger log = LoggerFactory.getLogger(AbstractStreamServlet.class);

    private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 week.
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    /***************************************************************************
     *                                                                         *
     * Public methods                                                          *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the servlet.
     * @see HttpServlet#init().
     */
    @Override
    public void init() throws ServletException {
        super.init(); // Required for auto-wiring support

        // Do some init here if necessary
    }

    /**
     * Process HEAD request. This returns the same headers as GET request, but without content.
     * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse).
     */
    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Process request without content.
        processRequest(request, response, false);
    }

    /**
     * Process GET request.
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Process request with content.
        processRequest(request, response, true);
    }

    /**
     * Process the actual request.
     * @param request The request to be processed.
     * @param response The response to be created.
     * @param content Whether the request body should be written (GET) or not (HEAD).
     * @throws IOException If something fails at I/O level.
     */
    private void processRequest
        (HttpServletRequest request, HttpServletResponse response, boolean content)
            throws IOException {

        StreamResource resource = getStreamResource(request, response);

        if (resource != null) {
            log.info("Processing HTTP request for stream resource " + resource.getName());
            sendStream(resource,request, response, content);
        } else {
            log.warn("Could not find requested stream resource!");
            response.sendError(404);
        }
    }


    private void sendStream
        ( StreamResource resource, HttpServletRequest request, HttpServletResponse response, boolean content)
        throws IOException
    {
        // Prepare some variables.
        String fileName = resource.getName();
        long length = resource.getLength();
        long lastModified = resource.getLastModified();
        String eTag = fileName + "_" + length + "_" + lastModified; // The ETag is an unique identifier of the file.
        long expires = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME;


        if(!validateRequestHeader(request, response, eTag, lastModified, expires)){
            log.warn("Request for stream had an illegal header. Terminating request.");
            return; // Request was invalid !
        }

        try{
            List<Range> ranges = getRanges(request, response, length, eTag, lastModified);

            // Prepare and initialize response --------------------------------------------------------

            // Get content type and set default GZIP support and content disposition.
            String contentType = resource.getMimeType();
            boolean acceptsGzip = false;
            String disposition = "inline";

            // If content type is unknown, then set the default value.
            // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
            // To add new content types, add new mime-mapping entry in web.xml.
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // If content type is text, then determine whether GZIP content encoding is supported by
            // the browser and expand content type with the one and right character encoding.
            if (contentType.startsWith("text")) {
                String acceptEncoding = request.getHeader("Accept-Encoding");
                acceptsGzip = acceptEncoding != null && accepts(acceptEncoding, "gzip");
                contentType += ";charset=UTF-8";
            }

            // Else, expect for images, determine content disposition. If content type is supported by
            // the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
            else if (!contentType.startsWith("image")) {
                String accept = request.getHeader("Accept");
                disposition = accept != null && accepts(accept, contentType) ? "inline" : "attachment";
            }

            // Initialize response.
            response.reset();
            response.setBufferSize(DEFAULT_BUFFER_SIZE);
            response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("ETag", eTag);
            response.setDateHeader("Last-Modified", lastModified);
            response.setDateHeader("Expires", expires);


            // Send requested file (part(s)) to client ------------------------------------------------
            sendRequestedRanges(resource, ranges, response, content, contentType, acceptsGzip);


        }catch (IllegalHttpRequestException e){
            log.warn("Failed to send requested resource!", e);
        }

    }

    /**
     *
     * @param resource
     * @param ranges
     * @param response
     * @param content
     * @param contentType
     * @param acceptsGzip
     * @throws IOException
     */
    private void sendRequestedRanges(
        StreamResource resource, List<Range> ranges, HttpServletResponse response,
        boolean content,  String contentType, boolean acceptsGzip) throws IOException {

        InputStream input = null;
        OutputStream output = null;

        try {
            // Open streams.
            input = resource.openInputStream();
            output = response.getOutputStream();

            if (ranges.get(0).isFull()) {

                // Return full file.
                Range r = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);

                if (content) {
                    if (acceptsGzip) {
                        // The browser accepts GZIP, so GZIP the content.
                        response.setHeader("Content-Encoding", "gzip");
                        output = new GZIPOutputStream(output, DEFAULT_BUFFER_SIZE);
                    } else {
                        // Content length is not directly predictable in case of GZIP.
                        // So only add it if there is no means of GZIP, else browser will hang.
                        response.setHeader("Content-Length", String.valueOf(r.length));
                    }

                    // Copy full range.
                    copyRange(input, output, r);
                }

            } else if (ranges.size() == 1) {

                // Return single part of file.
                Range r = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                if (content) {
                    // Copy single part range.
                    copyRange(input, output, r);
                }

            } else {

                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                if (content) {
                    // Cast back to ServletOutputStream to get the easy println methods.
                    ServletOutputStream sos = (ServletOutputStream) output;

                    // Copy multi part range.
                    for (Range r : ranges) {
                        // Add multipart boundary and header fields for every range.
                        sos.println();
                        sos.println("--" + MULTIPART_BOUNDARY);
                        sos.println("Content-Type: " + contentType);
                        sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

                        // Copy single part range of multi part range.
                        copyRange(input, output, r);
                    }

                    // End with multipart boundary.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY + "--");
                }
            }
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }
    }

    private void copyRange(InputStream input, OutputStream output, Range r) throws IOException {

        log.debug("Sending byte range to client " + r);

        copy(input, output, r.start, r.length);
    }

    /**
     * Gets all byte ranges of the given request
     * @param request
     * @param response
     * @param length
     * @param eTag
     * @param lastModified
     * @return
     * @throws IOException
     * @throws IllegalHttpRequestException
     */
    private List<Range> getRanges(HttpServletRequest request, HttpServletResponse response,
                                  long length, String eTag, long lastModified)
                                                throws IOException, IllegalHttpRequestException
    {

        // Validate and process range -------------------------------------------------------------

        // Prepare some variables. The full Range represents the complete file.
        final Range full = Range.createFull(length);
        List<Range> ranges = new ArrayList<>();

        // Validate and process Range and If-Range headers.
        String range = request.getHeader("Range");
        if (range != null) {

            // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                throw new IllegalHttpRequestException("Range header not matching format 'bytes=n-n,n-n,n-n...'");
            }

            // If-Range header should either match ETag or be greater then LastModified. If not,
            // then return full file.
            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(eTag)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte range.
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    // Assuming a file with length of 100, the following examples returns bytes at:
                    // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                    long start = sublong(part, 0, part.indexOf("-"));
                    long end = sublong(part, part.indexOf("-") + 1, part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        throw new IllegalHttpRequestException("Range is syntactically invalid!");
                    }

                    // Add range.
                    ranges.add(new Range(start, end, length));
                }
            }
        }

        if(ranges.isEmpty()){
            ranges.add(full);
        }

        return ranges;

    }


    /**
     * Validates the given request.
     *
     * @param request
     * @param response
     * @param eTag
     * @param lastModified
     * @param expires
     * @return Returns true if the request is valid, false otherwise.
     *
     * @throws IOException
     */
    private boolean validateRequestHeader(HttpServletRequest request, HttpServletResponse response,
                                          String eTag, long lastModified, long expires) throws IOException {

        // Validate request headers for caching ---------------------------------------------------

        // If-None-Match header should contain "*" or ETag. If so, then return 304.
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setHeader("ETag", eTag); // Required in 304.
            response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
            return false;
        }

        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
        // This header is ignored if any If-None-Match header is specified.
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setHeader("ETag", eTag); // Required in 304.
            response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
            return false;
        }


        // Validate request headers for resume ----------------------------------------------------

        // If-Match header should contain "*" or ETag. If not, then return 412.
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !matches(ifMatch, eTag)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return false;
        }

        // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return false;
        }

        return true;
    }

    /**
     * Subclasses have to overwrite this method and provide the resource which has to be streamed.
     * @param request
     * @param response
     * @return
     */
    protected abstract StreamResource getStreamResource(HttpServletRequest request, HttpServletResponse response);


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    /**
     * Returns true if the given accept header accepts the given value.
     * @param acceptHeader The accept header.
     * @param toAccept The value to be accepted.
     * @return True if the given accept header accepts the given value.
     */
    private static boolean accepts(String acceptHeader, String toAccept) {
        String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
        Arrays.sort(acceptValues);
        return Arrays.binarySearch(acceptValues, toAccept) > -1
            || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
            || Arrays.binarySearch(acceptValues, "*/*") > -1;
    }

    /**
     * Returns true if the given match header matches the given value.
     * @param matchHeader The match header.
     * @param toMatch The value to be matched.
     * @return True if the given match header matches the given value.
     */
    private static boolean matches(String matchHeader, String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1
            || Arrays.binarySearch(matchValues, "*") > -1;
    }

    /**
     * Returns a substring of the given string value from the given begin index to the given end
     * index as a long. If the substring is empty, then -1 will be returned
     * @param value The string value to return a substring as long for.
     * @param beginIndex The begin index of the substring to be returned as long.
     * @param endIndex The end index of the substring to be returned as long.
     * @return A substring of the given string value as long or -1 if substring is empty.
     */
    private static long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    /**
     * Copy the given byte range of the given input to the given output.
     * @param input The input to copy the given range to the given output for.
     * @param output The output to copy the given range from the given input for.
     * @param start Start of the byte range.
     * @param length Length of the byte range.
     * @throws IOException If something fails at I/O level.
     */
    private static void copy(InputStream input, OutputStream output, long start, long length)
        throws IOException
    {
        long toRead = -1;

        if(input instanceof ISeekableInputStream) {
            ISeekableInputStream sInput = (ISeekableInputStream)input;
            if (sInput.length() != length) {
                // Write partial range.
                sInput.seek(start);
                toRead = length;
            }
        }

        copy(input, output, toRead);

    }

    /**
     * Copy [toRead] bytes from the given input stream to the given output stream.
     *
     * Uses an internal buffered input steam to improve performance.
     *
     * @param input
     * @param output
     * @param toRead
     * @throws IOException
     */
    private static void copy(InputStream input, OutputStream output, long toRead) throws IOException {

        if(input == null) throw new ArgumentNullException("input");
        if(output == null) throw new ArgumentNullException("output");


        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        // Wrap the input stream into a buffer so we don't waste input-reads
        //input = new BufferedInputStream(input);

        int read;

        if(toRead >= 0){
            // Copy partially
            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
                } else {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        }else{
            // Copy whole input to output
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        }

    }


    /**
     * Close the given resource.
     * @param resource The resource to be closed.
     */
    private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ignore) {
                // Ignore IOException. If you want to handle this anyway, it might be useful to know
                // that this will generally only be thrown when the client aborted the request.
                log.debug("Failed to close stream!", ignore);
            }
        }
    }

}
