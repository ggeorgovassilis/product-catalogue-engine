package org.pce.database;

import java.util.Collection;
import java.util.List;

import org.pce.model.Entity;

public interface ProductDatabase {

	Entity readEntity(String ID);
	List<Entity> getAllEntities();
	List<Entity> getAllEntitiesInCategory(String category);
	List<String> getAllCategories();
	boolean isCategory(String category);
	List<String> getAllAttributeNames();
}
