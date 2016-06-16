package com.runningtop;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhu on 2016/6/16.
 */
public class RunningUtil {
    public static final String TAG = "PKG";
    private HashSet<String> psSet = new HashSet<>();

    public String getTopPS()
    {
        try
        {
            HashSet<String> lastSet = psSet;
            psSet = getPSSet();
            for (String pkg : psSet)
            {
                if (!lastSet.contains(pkg) && !pkg.startsWith("android."))
                {
                    Log.e(TAG, "launcher PackageName:" +pkg);
                    return pkg;
                }
            }
        } catch (Throwable throwable)
        {
        }
        return null;
    }

    private HashSet<String> getPSSet()
    {
        HashSet<String> set = new HashSet<>();
        String result = executeCMD("ps -P")[0];
        String[] lines = result.split("\\n");
        Pattern pattern = Pattern.compile("([\\w\\d_]+(\\.[\\w\\d_]+)+)");
        for (String line : lines)
        {
            if (line.startsWith("u0_") && line.contains(" fg "))
            {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                {
                    set.add(matcher.group(1));
                }
            }
        }
        return set;
    }


    public static String[] executeCMD(String cmd)
    {
        String[] result = new String[]{"", ""};
        try
        {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = null;
            InputStream errorStream = null;
            ByteArrayOutputStream outputStream = null;
            try
            {
                inputStream = process.getInputStream();
                errorStream = process.getErrorStream();
                outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, length);
                }
                result[0] = new String(outputStream.toByteArray());

                outputStream.reset();
                while ((length = errorStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, length);
                }
                result[1] = new String(outputStream.toByteArray());
            } finally
            {
                inputStream.close();
                errorStream.close();
                outputStream.close();
            }
        } catch (Throwable throwable)
        {
        }
        return result;
    }


}
