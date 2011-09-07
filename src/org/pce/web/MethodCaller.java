package org.pce.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.lang.jstl.Coercions;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.apache.taglibs.standard.lang.jstl.Logger;

/**
 * The MethodCaller class uses the Java Reflection API to invoke JavaBean methods. 
 * It also uses a few classes taken from the Apache JSTL implementation to handle 
 * type conversions.
 */
public class MethodCaller {

	
	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MethodCaller.class);
	
	/**
	 * The findMethod() static method gets the array of Method instances describing the 
	 * JavaBean class's public methods. Then, it iterates over this array and 
	 * returns the first Method that has the given name and the given number of parameters.
	 * @param object
	 * @param methodName
	 * @param paramCount
	 * @return
	 * @throws JspException
	 */
	private static Method findMethod(Object object, String methodName, int paramCount) throws JspException {
		Class clazz = object.getClass();
		Method pubMethods[] = clazz.getMethods();
		for (int i = 0; i < pubMethods.length; i++) {
			Method m = pubMethods[i];
			if (methodName.equals(m.getName()) && m.getParameterTypes().length == paramCount)
				return m;
		}
		throw new JspException("Method not found: " + clazz.getName() + "." + methodName + "()");
	}

	/**
	 * The coerceParamValuesToParamTypes() method coerces the <ct:call>'s dynamic 
	 * attribute values to the JavaBean method's parameter types. 
	 * The Coercions class's coerce() method developed by Apache does the hard work.
	 * @param method
	 * @param paramValues
	 * @return
	 * @throws JspException
	 */
	private static Object[] coerceParamValuesToParamTypes(Method method, List paramValues) throws JspException {
		Class paramTypes[] = method.getParameterTypes();
		Object coercedValues[] = new Object[paramTypes.length];
		Iterator paramIterator = paramValues.iterator();
		Logger logger = new Logger(System.err);
		for (int i = 0; i < paramTypes.length; i++) {
			Object paramValue = paramIterator.next();
			Class paramClass = paramTypes[i];
			if (paramValue == null || paramValue.getClass() != paramClass)
				try {
					paramValue = Coercions.coerce(paramValue, paramClass, logger);
				} catch (ELException e) {
					throw new JspException(e.getMessage(), e.getRootCause());
				}
			coercedValues[i] = paramValue;
		}
		return coercedValues;
	}

	/**
	 * The call() method receives as parameters the JavaBean object, the name of 
	 * the method that must be invoked, and a list containing the parameter values. 
	 * It invokes findMethod() to get the Method instance. It invokes coerceParamValuesToParamTypes() 
	 * to apply type conversions. And finally, it invokes the JavaBean method.
	 * @param object
	 * @param methodName
	 * @param paramValues
	 * @return
	 * @throws JspException
	 */
	public static Object call(Object object, String methodName, List paramValues) throws JspException {
		Method method = findMethod(object, methodName, paramValues.size());
		Object args[] = coerceParamValuesToParamTypes(method, paramValues);

		try {
			logger.debug("CALLING METHOD " + method + " WITH FIRST ARG " + args[0]);
			Object result = method.invoke(object, args);
			logger.debug("RETURNED " + object);
			return result;
		} catch (InvocationTargetException e) {
			throw new JspException(e.getTargetException());
		} catch (IllegalAccessException e) {
			throw new JspException(e);
		} catch (IllegalArgumentException e) {
			throw new JspException(e);
		}
	}
}
