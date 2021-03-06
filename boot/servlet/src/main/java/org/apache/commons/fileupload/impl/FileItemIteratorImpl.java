/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.fileupload.impl;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.util.LimitedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.lang.String.format;

/**
 * The iterator, which is returned by
 * {@link BaseFileUpload#getItemIterator(UploadContext)}.
 */
public class FileItemIteratorImpl implements FileItemIterator {
	private final BaseFileUpload baseFileUpload;
	private final UploadContext ctx;
	private long sizeMax, fileSizeMax;


	@Override
	public long getSizeMax() {
		return sizeMax;
	}

	@Override
	public void setSizeMax(long sizeMax) {
		this.sizeMax = sizeMax;
	}

	@Override
	public long getFileSizeMax() {
		return fileSizeMax;
	}

	@Override
	public void setFileSizeMax(long fileSizeMax) {
		this.fileSizeMax = fileSizeMax;
	}

	/**
     * The multi part stream to process.
     */
    private MultipartStream multiPartStream;

    /**
     * The notifier, which used for triggering the
     * {@link ProgressListener}.
     */
    private MultipartStream.ProgressNotifier progressNotifier;

    /**
     * The boundary, which separates the various parts.
     */
    private byte[] multiPartBoundary;

    /**
     * The item, which we currently process.
     */
    private FileItemStreamImpl currentItem;

    /**
     * The current items field name.
     */
    private String currentFieldName;

    /**
     * Whether we are currently skipping the preamble.
     */
    private boolean skipPreamble;

    /**
     * Whether the current item may still be read.
     */
    private boolean itemValid;

    /**
     * Whether we have seen the end of the file.
     */
    private boolean eof;

    /**
     * Creates a new instance.
     *
     * @param baseFileUpload Main processor.
     * @param requestContext The request context.
     * @throws FileUploadException An error occurred while
     *   parsing the request.
     * @throws IOException An I/O error occurred.
     */
    public FileItemIteratorImpl(BaseFileUpload baseFileUpload, UploadContext requestContext)
        throws FileUploadException, IOException {
        this.baseFileUpload = baseFileUpload;
        sizeMax = baseFileUpload.getSizeMax();
        fileSizeMax = baseFileUpload.getFileSizeMax();
        ctx = Objects.requireNonNull(requestContext, "requestContext");
        skipPreamble = true;
        findNextItem();
    }

    protected void init(BaseFileUpload baseFileUpload, RequestContext pRequestContext)
            throws FileUploadException, IOException {
        String contentType = ctx.getContentType();
        if ((null == contentType)
                || (!contentType.toLowerCase(Locale.ENGLISH).startsWith(BaseFileUpload.MULTIPART))) {
            throw new InvalidContentTypeException(
                    format("the request doesn't contain a %s or %s stream, content type header is %s",
                           BaseFileUpload.MULTIPART_FORM_DATA, BaseFileUpload.MULTIPART_MIXED, contentType));
        }

        final long requestSize = ctx.contentLength();

        InputStream input; // N.B. this is eventually closed in MultipartStream processing
        if (sizeMax >= 0) {
            if (requestSize != -1 && requestSize > sizeMax) {
                throw new SizeLimitExceededException(
                    format("the request was rejected because its size (%s) exceeds the configured maximum (%s)",
                            requestSize, sizeMax),
                        requestSize, sizeMax);
            }
            // N.B. this is eventually closed in MultipartStream processing
            input = new LimitedInputStream(ctx.getInputStream(), sizeMax) {
                @Override
                protected void raiseError(long pSizeMax, long pCount)
                        throws IOException {
                    FileUploadException ex = new SizeLimitExceededException(
                    format("the request was rejected because its size (%s) exceeds the configured maximum (%s)",
                            pCount, pSizeMax),
                           pCount, pSizeMax);
                    throw new FileUploadIOException(ex);
                }
            };
        } else {
            input = ctx.getInputStream();
        }

        String charEncoding = baseFileUpload.getHeaderEncoding();
        if (charEncoding == null) {
            charEncoding = ctx.getCharacterEncoding();
        }

        multiPartBoundary = baseFileUpload.getBoundary(contentType);
        if (multiPartBoundary == null) {
            if (input != null) {
                // avoid possible resource leak
                input.close();
            }
            throw new FileUploadException("the request was rejected because no multipart boundary was found");
        }

        progressNotifier = new MultipartStream.ProgressNotifier(baseFileUpload.getProgressListener(), requestSize);
        try {
            multiPartStream = new MultipartStream(input, multiPartBoundary, progressNotifier);
        } catch (IllegalArgumentException iae) {
            if (input != null) {
                // avoid possible resource leak
                input.close();
            }
            throw new InvalidContentTypeException(
                    format("The boundary specified in the %s header is too long", BaseFileUpload.CONTENT_TYPE), iae);
        }
        multiPartStream.setHeaderEncoding(charEncoding);
    }

    public MultipartStream getMultiPartStream() throws FileUploadException, IOException {
    	if (multiPartStream == null) {
    		init(baseFileUpload, ctx);
    	}
    	return multiPartStream;
    }

    /**
     * Called for finding the next item, if any.
     *
     * @return True, if an next item was found, otherwise false.
     * @throws IOException An I/O error occurred.
     */
    private boolean findNextItem() throws FileUploadException, IOException {
        if (eof) {
            return false;
        }
        if (currentItem != null) {
            currentItem.close();
            currentItem = null;
        }
        final MultipartStream multi = getMultiPartStream();
        while (true) {
            boolean nextPart;
            if (skipPreamble) {
                nextPart = multi.skipPreamble();
            } else {
                nextPart = multi.readBoundary();
            }
            if (!nextPart) {
                if (currentFieldName == null) {
                    // Outer multipart terminated -> No more data
                    eof = true;
                    return false;
                }
                // Inner multipart terminated -> Return to parsing the outer
                multi.setBoundary(multiPartBoundary);
                currentFieldName = null;
                continue;
            }
            FileItemHeaders headers = baseFileUpload.getParsedHeaders(multi.readHeaders());
            if (currentFieldName == null) {
                // We're parsing the outer multipart
                String fieldName = baseFileUpload.getFieldName(headers);
                if (fieldName != null) {
                    String subContentType = headers.getHeader(BaseFileUpload.CONTENT_TYPE);
                    if (subContentType != null
                            &&  subContentType.toLowerCase(Locale.ENGLISH)
                                    .startsWith(BaseFileUpload.MULTIPART_MIXED)) {
                        currentFieldName = fieldName;
                        // Multiple files associated with this field name
                        byte[] subBoundary = baseFileUpload.getBoundary(subContentType);
                        multi.setBoundary(subBoundary);
                        skipPreamble = true;
                        continue;
                    }
                    String fileName = baseFileUpload.getFileName(headers);
                    currentItem = new FileItemStreamImpl(this, fileName,
                            fieldName, headers.getHeader(BaseFileUpload.CONTENT_TYPE),
                            fileName == null, getContentLength(headers));
                    currentItem.setHeaders(headers);
                    progressNotifier.noteItem();
                    itemValid = true;
                    return true;
                }
            } else {
                String fileName = baseFileUpload.getFileName(headers);
                if (fileName != null) {
                    currentItem = new FileItemStreamImpl(this, fileName,
                            currentFieldName,
                            headers.getHeader(BaseFileUpload.CONTENT_TYPE),
                            false, getContentLength(headers));
                    currentItem.setHeaders(headers);
                    progressNotifier.noteItem();
                    itemValid = true;
                    return true;
                }
            }
            multi.discardBodyData();
        }
    }

    private long getContentLength(FileItemHeaders pHeaders) {
        try {
            return Long.parseLong(pHeaders.getHeader(BaseFileUpload.CONTENT_LENGTH));
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Returns, whether another instance of {@link FileItemStream}
     * is available.
     *
     * @throws FileUploadException Parsing or processing the
     *   file item failed.
     * @throws IOException Reading the file item failed.
     * @return True, if one or more additional file items
     *   are available, otherwise false.
     */
    @Override
    public boolean hasNext() throws FileUploadException, IOException {
        if (eof) {
            return false;
        }
        if (itemValid) {
            return true;
        }
        try {
            return findNextItem();
        } catch (FileUploadIOException e) {
            // unwrap encapsulated SizeException
            throw (FileUploadException) e.getCause();
        }
    }

    /**
     * Returns the next available {@link FileItemStream}.
     *
     * @throws NoSuchElementException No more items are
     *   available. Use {@link #hasNext()} to prevent this exception.
     * @throws FileUploadException Parsing or processing the
     *   file item failed.
     * @throws IOException Reading the file item failed.
     * @return FileItemStream instance, which provides
     *   access to the next file item.
     */
    @Override
    public FileItemStream next() throws FileUploadException, IOException {
        if (eof || (!itemValid && !hasNext())) {
            throw new NoSuchElementException();
        }
        itemValid = false;
        return currentItem;
    }

	@Override
	public List<FileItem> getFileItems() throws IOException {
		final List<FileItem> items = new ArrayList<>();
		while (hasNext()) {
			final FileItemStream fis = next();
			final FileItem fi = baseFileUpload.getFileItemFactory().createItem(fis.getFieldName(), fis.getContentType(), fis.isFormField(), fis.getName());
			items.add(fi);
		}
		return items;
	}

}