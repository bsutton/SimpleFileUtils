/*
 * FileLock class
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
package com.ricardolorenzo.file.lock;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Ricardo Lorenzo
 * 
 */
public class FileLock {
    private File lock_file;

    public FileLock(final File file) {
        lock_file = new File(file.getAbsolutePath(), ".lock");
    }

    public void lock() throws FileLockException {
        try {
            checkLock();
        } catch (InterruptedException e) {
            throw new FileLockException("fail to sleep thread in check");
        }
        try {
            lock_file.createNewFile();
        } catch (IOException e) {
            throw new FileLockException("cannot create file lock");
        }
    }

    public void unlock() throws FileLockException {
        if (lock_file.exists()) {
            if (!lock_file.delete()) {
                throw new FileLockException("cannot delete file lock");
            }
        }
    }

    public void unlockQuietly() {
        if (lock_file.exists()) {
            if (!lock_file.delete()) {
                // nothing
            }
        }
    }

    private void checkLock() throws FileLockAlreadyLockedException, InterruptedException {
        for (int i = 16; --i >= 0;) {
            if (!lock_file.exists()) {
                return;
            }
            Thread.sleep(250L);
        }
        throw new FileLockAlreadyLockedException("permanently locked file");
    }
}
