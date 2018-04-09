package com.mvc.polymorphic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

    public static String readFile (InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            buffer.append(line);
        }
        inputStream.close();
        return buffer.toString();
    }
}
