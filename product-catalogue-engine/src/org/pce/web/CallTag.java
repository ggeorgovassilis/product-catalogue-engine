package org.pce.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

public class CallTag extends SimpleTagSupport implements DynamicAttributes {

	
	private static final Logger logger = Logger.getLogger(CallTag.class);
	
	private Object object;
    private String methodName;
    private String returnVar;
    private int returnScope;
    private boolean debugFlag;
    private List paramValues;

    public CallTag() {
    	returnScope = PageContext.PAGE_SCOPE;
        paramValues = new LinkedList();
	}
    
    public void setObject(Object object) throws JspException {
        if (object == null)
            throw new JspException("Null 'object' attribute in 'call' tag");
        this.object = object;
    }
    
    public void setMethod(String methodName) throws JspException {
        if (methodName == null)
            throw new JspException("Null 'method' attribute in 'call' tag");
        if (methodName.length() == 0)
            throw new JspException("Empty 'method' attribute in 'call' tag");
        this.methodName = methodName;
    }
    
    public void setReturn(String returnVar) throws JspException {
        if (returnVar == null)
            throw new JspException("Null 'return' attribute in 'call' tag");
        if (returnVar.length() == 0)
            throw new JspException("Empty 'return' attribute in 'call' tag");
        this.returnVar = returnVar;
    }
    
    public void setScope(String returnScope) throws JspException {
        if (returnScope.equalsIgnoreCase("page"))
            this.returnScope = PageContext.PAGE_SCOPE;
        else if (returnScope.equalsIgnoreCase("request"))
            this.returnScope = PageContext.REQUEST_SCOPE;
        else if (returnScope.equalsIgnoreCase("session"))
            this.returnScope = PageContext.SESSION_SCOPE;
        else if (returnScope.equalsIgnoreCase("application"))
            this.returnScope = PageContext.APPLICATION_SCOPE;
        else
            throw new JspException("Invalid 'scope' in 'call' tag: "
                + returnScope);
    }
    
    public void setDebug(boolean debugFlag) throws JspException {
        this.debugFlag = debugFlag;
    }
    
	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		paramValues.add(value);
		
	}


	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
        Object returnValue = MethodCaller.call(object, methodName, paramValues);
	    JspContext context = getJspContext();
	    if (returnVar != null) {
	        if (returnValue != null)
	            context.setAttribute(returnVar, returnValue, returnScope);
	        else
	            context.removeAttribute(returnVar, returnScope);
	    }
	    if (debugFlag) {
	        JspWriter out = context.getOut();
	        out.println("<!-- calltag debug info");
	        out.println("Class: " + object.getClass().getName());
	        out.print("Call: " + methodName + "(");
	        Iterator paramIterator = paramValues.iterator();
	        while (paramIterator.hasNext()) {
	            Object value = paramIterator.next();
	            out.print(value != null ? value.toString() : "null");
	            if (paramIterator.hasNext())
	                out.print(", ");
	        }
	        out.println(")");
	        if (returnVar != null)
	            out.println("Return: "
	                + (returnValue != null ? returnValue.toString() : "null"));
	        out.println("-->");
	    }
	}

	
	
}
