package org.pce.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

import org.pce.model.Entity;

public class CompositeDatabase implements ProductDatabase{

	private List<ProductDatabase> databases = new ArrayList<ProductDatabase>();

	public void addDatabase(ProductDatabase pd) {
		databases.add(pd);
	}
	
	@Override
	public Entity readEntity(String ID) {
		for (ProductDatabase pd:databases) {
			Entity e = pd.readEntity(ID);
			if (e!=null)
				return e;
		}
		return null;
	}

	public List<ProductDatabase> getDatabases() {
		return databases;
	}

	public void setDatabases(List<ProductDatabase> databases) {
		this.databases = databases;
	}

	@Override
	public List<Entity> getAllEntities() {
		List<Entity> allEntities = new ArrayList<Entity>();
		for (ProductDatabase pd:databases) {
			allEntities.addAll(pd.getAllEntities());
		}
		return allEntities;
	}

	@Override
	public List<Entity> getAllEntitiesInCategory(String category) {
		List<Entity> allEntities = new ArrayList<Entity>();
		for (ProductDatabase pd:databases) {
			allEntities.addAll(pd.getAllEntitiesInCategory(category));
		}
		return allEntities;	
	}

	@Override
	public List<String> getAllCategories() {
		List<String> cats = new ArrayList<String>();
		for (ProductDatabase pd:databases) {
			cats.addAll(pd.getAllCategories());
		}
		return cats;
	}

	@Override
	public boolean isCategory(String category) {
		for (ProductDatabase pd:databases) {
			if (pd.isCategory(category))
				return true;
		}
		return false;
	}

	@Override
	public List<String> getAllAttributeNames() {
		Set<String> set = new HashSet<String>();
		for (ProductDatabase pd:databases) {
			set.addAll(pd.getAllAttributeNames());
		}
		return new ArrayList<String>(set);
	}
	
}
