package com.gn.util.findfastfood.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

public class AppLog
{
	
	private static boolean doLog = true;
	private static Handler myHandler;
	private static String preText = "";
	
	public static void setPreText(String pt)
	{
		preText = pt;
	}
	
	public static void enableLog(boolean enable)
	{
		Log.i("Top50", "enableLog: " + enable);
		doLog = enable;
	}
	
	public static void i(String tag, String msg)
	{
		if (doLog)
		{
			Log.i(tag , preText+msg);
		}
	}

	public static void e(String tag, String msg)
	{
		if (doLog)
		{
			Log.e(tag , preText+msg);
		}
	}

	public static void w(String tag , String msg)
	{
		if (doLog)
		{
			Log.w(tag, preText+msg);
		}
	}

	public static void d(String tag , String msg)
	{
		if (doLog)
		{
			Log.d(tag, preText+msg);
		}
	}

	public static void v(String tag , String msg)
	{
		if (doLog)
		{
			Log.v(tag, preText+msg);
		}
	}
	
	public static void timeTracK(String fnc)
	{
		if (doLog)
		{
			Date d = new Date();
			CharSequence s  = DateFormat.format("h:mm:ss", d.getTime());
			Log.v("AdLogTimeTest", "Function - "+fnc+": Time = "+s);
		}
	}
	
	public static void printStackTrace(String tag, Exception e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stacktrace = sw.toString();
		Log.d(tag,stacktrace);
	}
	
	public static void setHandler(Handler hd)
	{
		myHandler = hd;
	}
}
