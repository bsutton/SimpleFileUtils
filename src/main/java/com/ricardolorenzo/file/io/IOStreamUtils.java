package com.ricardolorenzo.file.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class IOStreamUtils {
    public final static int COMPRESSION_GZIP = 1;
    public final static int COMPRESSION_DEFLATE = 2;
    public final static int COMPRESSION_ZIP = 3;

    public static final void closeQuietly(final InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (NullPointerException e) {
            // nothing
        } catch (IOException e) {
            // nothing
        }
    }

    public static final void closeQuietly(final OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (NullPointerException e) {
            // nothing
        } catch (IOException e) {
            // nothing
        }
    }

    /**
     * Compress stream into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static void compress(final int type, final InputStream is, final OutputStream os) throws IOException,
            NoSuchMethodException {
        switch (type) {
            case COMPRESSION_GZIP: {
                GZIPOutputStream gzipped = new GZIPOutputStream(os);
                try {
                    write(is, gzipped);
                    gzipped.flush();
                } finally {
                    closeQuietly(gzipped);
                }
            }
            case COMPRESSION_DEFLATE: {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                DeflaterOutputStream deflated = new DeflaterOutputStream(output, new Deflater(
                        Deflater.DEFAULT_COMPRESSION, true));
                try {
                    write(is, deflated);
                    deflated.flush();
                } finally {
                    closeQuietly(deflated);
                }
            }
            case COMPRESSION_ZIP: {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ZipOutputStream zipped = new ZipOutputStream(output);
                try {
                    write(is, zipped);
                    zipped.flush();
                } finally {
                    closeQuietly(zipped);
                }
            }
            default:
                throw new NoSuchMethodException();
        }
    }

    /**
     * Decompress stream from different formats
     * 
     * @throws DataFormatException
     * @throws NoSuchMethodException
     * */
    public static void decompress(final int type, final InputStream is, final OutputStream os) throws IOException,
            NoSuchMethodException {
        switch (type) {
            case COMPRESSION_GZIP: {
                GZIPInputStream gzipped = new GZIPInputStream(is);
                try {
                    write(gzipped, os);
                } finally {
                    closeQuietly(gzipped);
                }
            }
            case COMPRESSION_DEFLATE: {
                InflaterOutputStream inflated = new InflaterOutputStream(os, new Inflater(false));
                try {
                    write(is, inflated);
                    inflated.flush();
                } finally {
                    closeQuietly(inflated);
                }

            }
            case COMPRESSION_ZIP: {
                ZipInputStream zipped = new ZipInputStream(is);
                try {
                    write(zipped, os);
                } finally {
                    closeQuietly(zipped);
                }
            }
            default:
                throw new NoSuchMethodException();
        }
    }

    public static final void write(final InputStream is, final OutputStream os) throws IOException {
        byte[] buffer = new byte[2048];
        for (int lenght = is.read(buffer); lenght > 0; lenght = is.read(buffer)) {
            os.write(buffer, 0, lenght);
        }
    }

    public static final void write(final String content, final OutputStream os) throws IOException {
        os.write(content.getBytes());
    }
}
