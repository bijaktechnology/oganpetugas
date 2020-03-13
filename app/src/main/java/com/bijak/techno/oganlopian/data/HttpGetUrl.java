package com.bijak.techno.oganlopian.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zetro on 19/08/2017.
 */

public class HttpGetUrl {
    /**
     * Get json data from API Server
     * @param url
     * @return
     * @throws IOException
     */
    public String HttpGet(String url)throws IOException {
        InputStream inputStream=null;
        String result="";

        URL url1 = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.connect();

        // receive response as inputStream
        inputStream = conn.getInputStream();

        // convert inputstream to string
        if(inputStream != null) {
            result = convertInputStreamToString(inputStream);
            //show(result);
        }else {
            result = "Did not work!";
        }
        return  result;
    }
// Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    /**
     * Convert output from API to string
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
