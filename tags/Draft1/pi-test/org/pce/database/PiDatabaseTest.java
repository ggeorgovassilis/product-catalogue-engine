package org.pce.database;

import java.util.logging.Logger;

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
		//setupFakePi();
		setupTheRealPi();
		ProductDatabaseExcelImpl excel = new ProductDatabaseExcelImpl("database2.xls");
		compositeDatabase = new CompositeDatabase();
		compositeDatabase.addDatabase(piDatabase);
		compositeDatabase.addDatabase(excel);
		engine = new RulesEngineJsImpl(compositeDatabase);
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

	@Test
	public void testBundle1(){
		Entity e = compositeDatabase.readEntity("Bundle1");
		e = engine.evaluateEntity(e);
		assertEquals("283.74",e.getAttribute("Price"));
		assertEquals("17532,17533,17534,17535,17536,23410,2940,2943,37817,39996,39997,42982,43143,46733,46953,46955,47353,47354,47501,49682,50049,50050,50764,52639,52640,52641,52642,52643,52644,52645,52646,52672,52673", e.getAttribute("CaseID"));
	}

	@Test
	public void testBundle2(){
		Entity laptop = compositeDatabase.readEntity("52199");
		Entity laptopCase = compositeDatabase.readEntity("17535");
		double laptopPrice = Double.parseDouble(laptop.getAttribute("price"));
		double casePrice = Double.parseDouble(laptopCase.getAttribute("price"));
		double combinedPrice =  0.9*(laptopPrice+ casePrice);
		System.out.println("laptop price "+laptopPrice); //283.74
		System.out.println("case price "+casePrice); //50
		System.out.println("combined price "+combinedPrice);

		Entity bundle = compositeDatabase.readEntity("Bundle2");
		bundle = engine.evaluateEntity(bundle);
		
		assertEquals(combinedPrice,Double.parseDouble(bundle.getAttribute("Price")),0.2);
	}
}
