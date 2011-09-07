package org.pce.database.engine;

import org.pce.model.Entity;

public interface RulesEngine {

	Entity evaluateEntity(Entity e);
	
}