package org.pce.web;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.pce.database.ProductDatabase;
import org.pce.database.engine.RulesEngine;
import org.pce.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EntityController {
	
	@Resource(name="entityDatabase")
	ProductDatabase database;
	
	@Autowired
	RulesEngine rulesEngine;
	
	@RequestMapping(value="/entity/{id}", method=RequestMethod.GET)
	public String getView(@PathVariable String id, Model model) throws Exception{

		Entity entity = database.readEntity(id);
		entity = rulesEngine.evaluateEntity(entity);
		if (entity == null) {
			throw new ResourceNotFoundException(id);
		}
		model.addAttribute("entity", entity);
		model.addAttribute("category", entity.getCategory());
		putCategoriesInModel(model);
		return "entity";
	}
	
	@RequestMapping(value="/entities", method=RequestMethod.GET)
	public String getView(Model model) throws Exception{

		Collection<Entity> entities = database.getAllEntities();
		if (entities == null) {
			throw new ResourceNotFoundException("All entities");
		}
		model.addAttribute("entities", entities);
		putCategoriesInModel(model);
		return "entities";
	}
	
	private void putCategoriesInModel(Model model) {
		List<String> categories = database.getAllCategories();
		if (categories == null) {
			throw new ResourceNotFoundException("All categories");
		}
		model.addAttribute("categories", categories);
	}
}
