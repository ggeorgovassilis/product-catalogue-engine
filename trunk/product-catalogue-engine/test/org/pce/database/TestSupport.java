package org.pce.database;

import org.junit.Test;
import org.pce.database.engine.js.JsPreparser;

import static org.junit.Assert.*;

public class TestSupport {

	private JsPreparser parser = new JsPreparser();
	
	private void compare(String original, String transformed){
		assertEquals(transformed, parser.parse(original));
	};

	private void condition(String original, String transformed){
		assertEquals(transformed, parser.parseCondition(original));
	};

	@Test
	public void testJsParser() {
		compare("set price to 50",
				"context.set(context.price(context.to(context.literal(context,'50'))))");

		compare("set price to [MobilePhoneId]",
				"context.set(context.price(context.to(context.DB(context.MobilePhoneId(context)))))");

		compare("set price to [MobilePhoneId] and add duration 1month",
				"context.set(context.price(context.to(context.DB(context.MobilePhoneId(context.and(context.add(context.duration(context.literal(context,'1month')))))))))");

		compare("Type is \'Mobile Phone ID\'",
				"context.Type(context.is(context.literal(context,\'Mobile Phone ID\')))");

		compare("Type is 'Mobile Phone Offer' and Name starts with 'XMAS'",
				"context.Type(context.is(context.literal(context.and(context.Name(context.starts(context.with(context.literal(context,'XMAS'))))),\'Mobile Phone Offer\')))");
	}
	
	@Test
	public void testConditionParser(){
		condition("Price is 2",	"Price == 2");
		condition("Price is 2 and Name is 'XMAS'",	"Price == 2 && Name == 'XMAS'");
		condition("Price > 10 or (Name is 'New XMAS' and Price < 20)",	"Price > 10 || (Name == 'New XMAS' && Price < 20)");
		condition("Type is 'Mobile Phone Offer' and Name startswith('XMAS')",	"Type == 'Mobile Phone Offer' && Name.indexOf('XMAS') == 0 ");
	}
	
	@Test
	public void funWithRegexp(){
		
		assertTrue("startswith (123)".matches("startswith\\s*\\(.*\\)"));
		assertEquals(".indexOf(123)==0", "startswith(123)".replaceAll("startswith\\s*\\((.*)\\)", ".indexOf($1)==0"));
		assertTrue("some test phrase".matches(".*test.*"));
	}
}
