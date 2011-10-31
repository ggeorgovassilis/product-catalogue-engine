package org.pce.database;

import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assert.*;
import org.junit.Test;
import org.pce.database.engine.RulesEngine;
import org.pce.database.engine.js.RulesEngineJsImpl;
import org.pce.database.excel.ProductDatabaseExcelImpl;
import org.pce.database.gdata.ProductDatabaseGDataImpl;
import org.pce.model.Entity;

public class ProductDatabaseTestGData extends ProductDatabaseTest{

	@BeforeClass
	public static void setup() throws Exception {
		database = new ProductDatabaseGDataImpl(System.getProperty("pcegdatauser"), System.getProperty("pcegdatapwd"),"database2");
		rulesEngine = new RulesEngineJsImpl(database);
	}

}
