package org.pce.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.pce.database.excel.ProductDatabaseExcelImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ExcelDbAccessInterceptor extends HandlerInterceptorAdapter {

	private final static Logger logger = Logger.getLogger(ExcelDbAccessInterceptor.class);
	
	@Autowired
	ProductDatabaseExcelImpl entityDatabaseExcel;
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.info("REFRESHING EXCEL DB...");
		entityDatabaseExcel.refreshDB();
		logger.info("REFRESHING EXCEL DB DONE!");
		return true;
	}
}
