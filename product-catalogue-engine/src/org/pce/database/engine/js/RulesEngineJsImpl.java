package org.pce.database.engine.js;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.pce.database.ProductDatabase;
import org.pce.database.engine.RulesEngine;
import org.pce.model.Entity;
import org.pce.utils.Utils;

public class RulesEngineJsImpl implements RulesEngine {

	private ProductDatabase database;
	private Logger logger = Logger.getLogger("pce");
	private ThreadLocal<Session> session = new ThreadLocal<Session>();
	
	private Session getSession(){
		Session s = session.get();
		if (s == null){
			s = new Session();
			session.set(s);
			s.cx = Context.enter();
			s.scope = s.cx.initStandardObjects();
		}
		return s;
	}
	
	public RulesEngineJsImpl(ProductDatabase database) {
		this.database = database;
	}

	private void log(Entity e, String message) {
		if (e.debug())
			logger.debug(message);
		else
			logger.trace(message);
	}

	private void putCommonObjectsIntoScope(Entity product) {
		Session s = getSession();
		s.scope.put("__pce_logger", s.scope, logger);
		s.scope.put("__pce_db", s.scope, database);
		s.scope.put("__pce_product", s.scope, product);
		s.scope.put("__pce_all_attributes_java", s.scope, database.getAllAttributeNames());
		s.scope.put("__pce_active", s.scope, true);
	}

	protected Result evaluateExpression(Entity product, String expression) {
		putCommonObjectsIntoScope(product);
		String script = "";
		try {
			Session s = getSession();
			script = Utils.readResource("org/pce/database/engine/js/setup_common.js");
			script+= Utils.readResource("org/pce/database/engine/js/setup_execution.js");
			String jsScript = s.jsPreparser.parse(expression);
			script += "var __pce_result = (" + jsScript + ")";
			script += ";";
			log(product, "Running expression: " + expression);
			String result = ""
					+ s.cx.evaluateString(s.scope, script, "<cmd>", 1, null);
			result = "" + s.scope.get("__pce_result", s.scope);
			log(product, "result " + result);
			boolean signActive = "true".equals(s.scope.get("__pce_active", s.scope)
					.toString());
			Result r = new Result();
			r.value = result;
			r.abort = !signActive;
			return r;
		} catch (Exception e) {
			logger.error(expression + "\n" + script);
			throw new RuntimeException(e);
		}
	}

	protected Result evaluateCondition(Entity product, String expression) {

		log(product, "Evaluating condition for " + product.getID() + ": "
				+ expression);
		Session s = getSession();
		putCommonObjectsIntoScope(product);
		String script = Utils.readResource("org/pce/database/engine/js/setup_common.js");
		script+= Utils.readResource("org/pce/database/engine/js/setup_conditions.js");
		for (String attribute : product.getAttributeNames())
			s.scope.put(attribute, s.scope, product.getAttribute(attribute));
		try {
			String jsScript = s.jsPreparser.parseCondition(expression);
			log(product, "Condition translated to " + jsScript);
			script += "\nvar __pce_tmp_result = false;\n";
			script += "with (Product){ __pce_tmp_result = (" + jsScript + ");}\n";
			script +="(__pce_tmp_result)";
			String result = ""
					+ s.cx.evaluateString(s.scope, script, "<cmd>", 1, null);
			boolean signActive = "true".equals(s.scope.get("__pce_active", s.scope)
					.toString());
			Result r = new Result();
			r.value = result;
			r.abort = !signActive;
			log(product, "Expression evaluated to " + r.value);
			return r;
		} catch (Exception e) {
			logger.error(expression + "\n" + Utils.numberLines(script));
			throw new RuntimeException(e);
		}
	}

	@Override
	public Entity evaluateEntity(Entity e) {
		Entity copy = e.clone(e);
		Session s = getSession();
		List<Entity> rules = database.getAllEntitiesInCategory("Rules");
		for (Entity rule : rules) {
			s.scope.put("__pce_logging_priority", s.scope, rule.debug()?Level.DEBUG:Level.TRACE);
			log(rule, "Evaluating condition " + rule.getID());
			Result result = evaluateCondition(copy,
					rule.getAttribute("Applies When"));
			boolean evaluation = Boolean.parseBoolean(result.value);
			log(rule, "Evaluation result for condition" + rule.getID() + ": "
					+ evaluation);
			if (!evaluation)
				continue;
			result = evaluateExpression(copy, rule.getAttribute("Actions"));
			log(rule, "Evaluation value for rule " + rule.getID() + ": "
					+ result.value);
			if (result.abort) {
				log(rule, "Aborting rules");
				break;
			}
		}
		return copy;
	}

}
