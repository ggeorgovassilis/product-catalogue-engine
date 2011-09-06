package org.pce.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

public class Utils implements ServletContextAware{
	
	public static ServletContext servletContext;
	
	public static String numberLines(String s){
		String result = "";
		String lines[] = s.split("\n");
		for (int line = 0;line<lines.length;line++){
			result = result + pad(""+(line+1), 3)+": "+lines[line]+"\n";
		}
		return result;
	}
	
	public static InputStream getResourceAsString(String classpath){
		InputStream in = Utils.class.getResourceAsStream(classpath);
		if (in!=null)
			return in;
		in = Utils.class.getClassLoader().getResourceAsStream(
				classpath);
		if (in!=null)
			return in;
		in = ClassLoader.getSystemResourceAsStream(classpath);
		if (in!=null)
			return in;
		in = ClassLoader.getSystemClassLoader().getResourceAsStream(classpath);
		if (in!=null)
			return in;
		in = servletContext.getResourceAsStream(classpath);
		return in;
	}

	public static String readResource(String classpath) {
		try {
			InputStream in = getResourceAsString(classpath); 
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i;
			while (-1 != (i = in.read()))
				baos.write(i);
			in.close();
			return new String(baos.toByteArray(), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static String denull(String s) {
		return s == null ? "" : s;
	}

	public static boolean inList(String text, String[] list) {
		for (String s : list)
			if (s.equalsIgnoreCase(text))
				return true;
		return false;
	}
	
	public static String stripSpacesAndSlashes(String s) {
		if (s == null) return s;
		s = s.replaceAll(" ","_");
		return s.replaceAll("/","-");
	}
	
	public static String undoStripSpacesAndSlashes(String s) {
		if (s == null) return s;
		s = s.replaceAll("_"," ");
		return s.replaceAll("-","/");
	}
	
	public static String pad(String s, int length){
		while (s.length()<length)
			s+=" ";
		return s;
	}

	@Override
	public void setServletContext(ServletContext context) {
		servletContext = context;
	}

}
