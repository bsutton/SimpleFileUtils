/*
 * FileUtils class
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Ricardo Lorenzo <unshakablespirit@gmail.com>
 */
package com.ricardolorenzo.file.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import com.ricardolorenzo.file.lock.FileLock;
import com.ricardolorenzo.file.lock.FileLockException;
import com.ricardolorenzo.file.security.FileSummation;

/**
 * 
 * @author Ricardo Lorenzo
 * 
 */
public class FileUtils {
    /**
     * Compress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static byte[] compress(final int type, final byte[] input) throws IOException, NoSuchMethodException {
        final ByteArrayInputStream is = new ByteArrayInputStream(input);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            IOStreamUtils.compress(type, is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
        return os.toByteArray();
    }

    /**
     * Compress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static void compress(final int type, final File input, final File output) throws IOException,
            NoSuchMethodException {
        final FileInputStream is = new FileInputStream(input);
        final FileOutputStream os = new FileOutputStream(output);
        try {
            IOStreamUtils.compress(type, is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
    }

    /**
     * Copy the file content into another
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static final void copyFile(final File f1, final File f2) throws IOException, FileLockException {
        if (f1.exists() && f1.isDirectory()) {
            if (!f2.exists()) {
                f2.mkdirs();
            }
            for (final File f : f1.listFiles()) {
                copyFile(f, new File(f2.getAbsolutePath() , f.getName()));
            }
        } else if (f1.exists() && f1.isFile()) {
            final FileLock fl = new FileLock(f2);
            try {
                fl.lock();
                final InputStream is = new BufferedInputStream(new FileInputStream(f1));
                final OutputStream os = new BufferedOutputStream(new FileOutputStream(f2));

                try {
                    IOStreamUtils.write(is, os);
                } finally {
                    IOStreamUtils.closeQuietly(is);
                    IOStreamUtils.closeQuietly(os);
                }
            } finally {
                fl.unlock();
            }
        }
    }

    /**
     * Decompress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static byte[] decompress(final int type, final byte[] input) throws IOException, NoSuchMethodException {
        final ByteArrayInputStream is = new ByteArrayInputStream(input);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        decompress(type, is, os);
        return os.toByteArray();
    }

    /**
     * Decompress bytes into different formats
     * 
     * @throws NoSuchMethodException
     *             , IOException
     * */
    public static void decompress(final int type, final File input, final File output) throws IOException,
            NoSuchMethodException {
        final FileInputStream is = new FileInputStream(input);
        final FileOutputStream os = new FileOutputStream(output);
        decompress(type, is, os);
    }

    private static void decompress(final int type, final InputStream is, final OutputStream os)
            throws NoSuchMethodException, IOException {
        try {
            IOStreamUtils.compress(type, is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
    }

    public static void emptyFile(final File file) throws IOException, FileLockException {
        BufferedOutputStream os = null;
        final FileLock fl = new FileLock(file);
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            fl.lock();
            os.write("".getBytes());
        } finally {
            fl.unlockQuietly();
            IOStreamUtils.closeQuietly(os);
        }
    }

    public static byte[] readFile(final File file) throws IOException {
        FileInputStream is = null;
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            is = new FileInputStream(file);
            IOStreamUtils.write(is, os);
        } finally {
            IOStreamUtils.closeQuietly(is);
            IOStreamUtils.closeQuietly(os);
        }
        return os.toByteArray();
    }

    public static String readFileAsString(final File file) throws IOException {
        return new String(readFile(file));
    }

    public static boolean updateFile(final File f1, final File f2) throws FileNotFoundException,
            NoSuchAlgorithmException, IOException, FileLockException {
        if (f1.exists()) {
            if (!FileSummation.compare(f1, f2)) {
                copyFile(f1, f2);
                return true;
            }
        }
        return false;
    }

    public static boolean writeFile(final File file, final byte[] content) throws IOException, FileLockException {
        if (content == null) {
            return false;
        }

        FileOutputStream os = null;
        final FileLock fl = new FileLock(file);
        try {
            fl.lock();
            final ByteArrayInputStream is = new ByteArrayInputStream(content);
            try {
                os = new FileOutputStream(file);
                IOStreamUtils.write(is, os);
                return true;
            } catch (final IOException e) {
                return false;
            } finally {
                IOStreamUtils.closeQuietly(is);
                IOStreamUtils.closeQuietly(os);
            }
        } finally {
            fl.unlock();
        }
    }

    public static boolean writeFile(final File file, final String content) throws IOException, FileLockException {
        if (content == null) {
            return false;
        }
        return writeFile(file, content.getBytes());
    }
}
