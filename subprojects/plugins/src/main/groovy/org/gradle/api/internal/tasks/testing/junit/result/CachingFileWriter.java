/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.testing.junit.result;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * by Szczepan Faber, created at: 11/19/12
 */
public class CachingFileWriter {

    final LinkedHashMap<File, OutputStream> openFiles = new LinkedHashMap<File, OutputStream>();
    private final Lock lock = new ReentrantLock();
    private final int openFilesCount;

    public CachingFileWriter(int openFilesCount) {
        this.openFilesCount = openFilesCount;
    }

    public void closeAll() {
        for (OutputStream outputStream : openFiles.values()) {
            IOUtils.closeQuietly(outputStream);
        }
        openFiles.clear();
    }

    public void write(File file, String text) {
        OutputStream out = null;
        //there are more effective ways of synchronizing below
        //however, this fat lock seems to be very effective anyway (negligible overhead according to the profiler)
        lock.lock();
        try {
            if (openFiles.containsKey(file)) {
                out = openFiles.get(file);
            } else {
                out = new FileOutputStream(file, true);
                openFiles.put(file, out);
                if (openFiles.size() > openFilesCount) {
                    //remove first
                    Iterator<Map.Entry<File, OutputStream>> iterator = openFiles.entrySet().iterator();
                    IOUtils.closeQuietly(iterator.next().getValue());
                    iterator.remove();
                }
            }
            out.write(text.getBytes());
        } catch (IOException e) {
            IOUtils.closeQuietly(out);
            throw new RuntimeException("Problems writing to file: " + file, e);
        } finally {
            lock.unlock();
        }
    }
}
