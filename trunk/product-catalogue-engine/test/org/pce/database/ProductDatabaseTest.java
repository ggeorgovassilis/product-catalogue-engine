package org.pce.database;

import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;
import org.pce.database.engine.RulesEngine;
import org.pce.database.engine.js.RulesEngineJsImpl;
import org.pce.database.excel.ProductDatabaseExcelImpl;
import org.pce.model.Entity;

public class ProductDatabaseTest {

	ProductDatabase database;
	RulesEngine rulesEngine;

	@Before
	public void setup() throws Exception {
		database = new ProductDatabaseExcelImpl("./doc/database2.xls");
		rulesEngine = new RulesEngineJsImpl(database);
	}

	@Test
	public void readProduct() throws Exception {
		Entity e = database.readEntity("OF1");
		assertNotNull(e);
		assertEquals("OF1", e.getID());
		assertEquals("SOHO 1",e.getAttribute("Name"));
		assertEquals("01/01/2011",e.getAttribute("ValidFrom"));
	}

	@Test
	public void computePriceOF1() throws Exception {
		// Price should be the price of the associated mobile phone (200)
		Entity e = database.readEntity("OF1");
		Entity result = rulesEngine.evaluateEntity(e);
		assertEquals("200.0", result.getAttribute("Price"));
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
