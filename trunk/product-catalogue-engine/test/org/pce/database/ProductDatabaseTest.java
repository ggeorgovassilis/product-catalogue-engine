package org.pce.database;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assert.*;
import org.junit.Test;
import org.pce.database.engine.RulesEngine;
import org.pce.database.engine.js.RulesEngineJsImpl;
import org.pce.database.excel.ProductDatabaseExcelImpl;
import org.pce.model.Entity;

public class ProductDatabaseTest {

	static ProductDatabase database;
	static RulesEngine rulesEngine;

	@BeforeClass
	public static void setup() throws Exception {
		File f = new File("./doc/database2.xls");
		URL url = f.toURI().toURL();
// Uncomment to use public database hosted on google docs
//		URL url = new URL("https://docs.google.com/spreadsheet/pub?hl=en_US&hl=en_US&key=0Am9D7ThqOUJidGxqdjBrSTlTUnMyMEhiZVdTcktYdEE&output=xls");
		database = new ProductDatabaseExcelImpl(url);
		rulesEngine = new RulesEngineJsImpl(database);
	}

	@Test
	public void readProduct() throws Exception {
		Entity e = database.readEntity("OF1");
		assertNotNull(e);
		assertEquals("OF1", e.getID());
		assertEquals("SOHO 1",e.getAttribute("Name"));
		assertEquals("01/01/2011",e.getAttribute("ValidFrom"));

		e = database.readEntity("MP3");
		assertNotNull(e);
		assertEquals("MP3", e.getID());
		assertEquals("B10",e.getAttribute("Name"));
		assertEquals("20.0",e.getAttribute("Price"));
	}

	@Test
	public void computePriceOF1() throws Exception {
		// Price should be the price of the associated mobile phone (200)
		Entity e = database.readEntity("OF1");
		Entity result = rulesEngine.evaluateEntity(e);
		assertEquals(200.0, Double.parseDouble(result.getAttribute("Price")),0.01);
	}
	
	@Test
	public void computePriceXmasOffers() throws Exception {
		// Price should be the price of the associated mobile phone 120 reduced by the 20% XMAS offer
		Entity e = database.readEntity("OF2");
		Entity result = rulesEngine.evaluateEntity(e);
		assertEquals("96", result.getAttribute("Price"));
	}
	
	@Test
	public void computeMemoryCardsForOF2() throws Exception {
		// Should return a sorted list of memory cards with capacity <= 8
		Entity e = database.readEntity("OF2");
		Entity result = rulesEngine.evaluateEntity(e);
		assertEquals("MC1,MC2,MC3", result.getAttribute("MemoryCardID"));
	}

	@Test
	public void overridePrice() throws Exception {
		// Price should be overridden by 33
		Entity e = database.readEntity("OF4");
		Entity result = rulesEngine.evaluateEntity(e);
		assertEquals("33", result.getAttribute("Price"));
	}
}
