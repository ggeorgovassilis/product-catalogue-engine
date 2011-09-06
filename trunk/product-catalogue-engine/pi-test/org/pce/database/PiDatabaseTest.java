package org.pce.database;

import gr.open.pi.PIConstants;
import gr.open.pi.ProductInformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pce.database.engine.RulesEngine;
import org.pce.database.engine.js.RulesEngineJsImpl;
import org.pce.database.excel.ProductDatabaseExcelImpl;
import org.pce.database.pi.PiDatabase;
import org.pce.model.Entity;
import static org.junit.Assert.*;

public class PiDatabaseTest {

	private final static String ENTITY_ID = "51181";
	
	private ProductDatabase piDatabase;
	private CompositeDatabase compositeDatabase;
	private RulesEngine engine;

	private void setupTheRealPi() throws Exception{
		ProductInformer pi = new ProductInformer("root", "root");
		piDatabase = new PiDatabase(pi);
	}
	
	private void setupFakePi(){
		Entity e = new Entity();
		e.setID(ENTITY_ID);
		e.setAttribute(PIConstants.ATTR_NAME, "Sony Vaio VPC-W12J1E/T");
		e.setAttribute(PIConstants.ATTR_PRICE, "324.39");
		e.setAttribute(PIConstants.ATTR_ERP_CODE, "20251730");

		piDatabase = Mockito.mock(ProductDatabase.class);
		Mockito.when(piDatabase.readEntity(ENTITY_ID)).thenReturn(e);
	}

	@Before
	public void setup() throws Exception{
		setupFakePi();
		ProductDatabaseExcelImpl excel = new ProductDatabaseExcelImpl("database2.xls");
		compositeDatabase = new CompositeDatabase();
		compositeDatabase.addDatabase(piDatabase);
		compositeDatabase.addDatabase(excel);
		engine = new RulesEngineJsImpl(excel);
	}
	
	@Test
	public void test() {
		Entity e = piDatabase.readEntity("51181");
		assertEquals("Sony Vaio VPC-W12J1E/T",e.getAttribute(PIConstants.ATTR_NAME).toString());
		assertEquals("324.39",e.getAttribute(PIConstants.ATTR_PRICE).toString());
		assertEquals("20251730",e.getAttribute(PIConstants.ATTR_ERP_CODE).toString());
		
	}
	
	@Test
	public void testCompositeDb() {
		Entity e = piDatabase.readEntity(ENTITY_ID);
		engine.evaluateEntity(e);
	}

}
