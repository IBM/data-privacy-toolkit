/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static Collection<String> loadLines(String fileName) throws FileNotFoundException{
        List<String> list;

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        list = br.lines().collect(Collectors.toList());

        return list;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return new String(buffer.toByteArray());
    }

    public static byte[] inputStreamToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
}

