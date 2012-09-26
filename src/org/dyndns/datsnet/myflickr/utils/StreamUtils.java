package org.dyndns.datsnet.myflickr.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StreamUtils {
    public static long CopyStream(InputStream is, OutputStream os)
    {
    	long transByte = 0;

        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
              transByte += count;
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return transByte;
    }

    public static String inputStreemToString(InputStream in) throws IOException{

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuffer buf = new StringBuffer();
        String str;
        while ((str = reader.readLine()) != null) {
                buf.append(str);
                buf.append("\n");
        }
        return buf.toString();
    }
}