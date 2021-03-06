/*
 * FileSummation class
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
package com.ricardolorenzo.file.security;

/**
 * Enables you to compare files using MD5 summation
 * 
 * @author: Ricardo Lorenzo
 * @version 0.1
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ricardolorenzo.file.io.IOStreamUtils;

public class FileSummation {
    public static boolean compare(final byte[] data1, final byte[] data2) throws NoSuchAlgorithmException, IOException {
        if (getMD5Summation(data1).equals(getMD5Summation(data2))) {
            return true;
        }
        return false;
    }

    public static boolean compare(final File file1, final File file2) throws NoSuchAlgorithmException, IOException {
        if (getMD5Summation(file1).equals(getMD5Summation(file2))) {
            return true;
        }
        return false;
    }

    public static String getMD5Summation(final byte[] data) throws NoSuchAlgorithmException, IOException {
        final InputStream is = new ByteArrayInputStream(data);
        return getMD5Summation(is);
    }

    public static String getMD5Summation(final File file) throws NoSuchAlgorithmException, IOException {
        final InputStream is = new FileInputStream(file);
        return getMD5Summation(is);
    }

    private static String getMD5Summation(final InputStream is) throws NoSuchAlgorithmException, IOException {
        final MessageDigest digest = MessageDigest.getInstance("MD5");
        final byte[] buffer = new byte[8192];
        int read = 0;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            final byte[] _md5sum = digest.digest();
            final BigInteger bi = new BigInteger(1, _md5sum);
            return bi.toString(16);
        } finally {
            IOStreamUtils.closeQuietly(is);
        }
    }
}
