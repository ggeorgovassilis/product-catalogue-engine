package org.pce.web;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.pce.database.ProductDatabase;
import org.pce.model.Entity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CategoryController {
	
	
	private static final Logger logger = Logger.getLogger(CategoryController.class);
	@Resource(name="entityDatabase")
	ProductDatabase database;
	
	@RequestMapping(value="/category/{category}", method=RequestMethod.GET)
	public String getView(@PathVariable String category, Model model) throws Exception{
		logger.debug("CATEGORY:" + category);
		List<Entity> entities = database.getAllEntitiesInCategory(category);
		if (entities == null) {
			throw new ResourceNotFoundException(category);
		}
		model.addAttribute("entities", entities);
		model.addAttribute("category", category);

		return "category";
	} 
	
	@RequestMapping(value="/categories", method=RequestMethod.GET)
	public String getView(Model model) throws Exception{

		List<String> categories = database.getAllCategories();
		if (categories == null) {
			throw new ResourceNotFoundException("All categories");
		}
		model.addAttribute("categories", categories);
		return "categories";
	}
}
