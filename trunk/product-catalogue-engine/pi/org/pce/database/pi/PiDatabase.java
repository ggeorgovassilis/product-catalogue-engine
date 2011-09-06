package org.pce.database.pi;

import gr.open.pi.Attribute;
import gr.open.pi.Category;
import gr.open.pi.PIConstants;
import gr.open.pi.ProductInformer;
import gr.open.pi.Reports;
import gr.open.pi.Reports.ProductListItem;
import gr.open.pi.Reports.ReportResult;
import gr.open.pi.StoreException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pce.database.ProductDatabase;
import org.pce.model.Entity;
import org.pce.utils.Utils;

public class PiDatabase implements ProductDatabase {

	
	private static final Logger logger = Logger.getLogger(PiDatabase.class);
	private ProductInformer pi;
	private final List<Attribute> attributes = new ArrayList<Attribute>();
	
	public PiDatabase(ProductInformer pi) {
		this.pi = pi;
	}
	
	@Override
	public Entity readEntity(String ID) {
		List<Integer> productIds = new ArrayList<Integer>();
		productIds.add(Integer.parseInt(ID));
		try {
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_ERP_CODE));
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_NAME));
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_PRICE));
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_DESCRIPTION));
			ReportResult rr = pi.getReports().getProductListByIds(productIds, attributes, 0, false, 0, 10, 1);
			List<ProductListItem> results = rr.getResults();
			if (results.isEmpty())
				return null;
			ProductListItem item = results.get(0);
			Entity e = productListItem2Entity(item);
			return e;
		} catch (StoreException e) {
			throw new RuntimeException(e);
		}
	}

	private Entity productListItem2Entity(ProductListItem item) throws StoreException {
		if (item==null) {
			return null;
		}
		Entity e = new Entity();
		for (Object attr:item.getAttributeMap().keySet()) {
			if (attr==null) {
				continue;
			}
			else if (item.getAttributeValue(attr.toString())==null) {
				e.setAttribute(attr.toString(), null);
			}
			else {
				e.setAttribute(attr.toString(), item.getAttributeValue(attr.toString()).toString());
			}
		}
		Category c = pi.getCategories().getCategory(item.getCategoryId());
		e.setCategory(Utils.stripSpacesAndSlashes(c.getFullName()));
		e.setAttribute("ID",String.valueOf(item.getId()));
		return e;
	}

	@Override
	public List<Entity> getAllEntities() {
		//TODO: implement
		return new ArrayList<Entity>();
	}

	@Override
	public List<Entity> getAllEntitiesInCategory(String categoryName) {
		logger.debug("CATEGORY NAME:" + categoryName);
		List<Entity> results = new ArrayList<Entity>();
		
		Category category = getCategoryByName(categoryName);
		if (category == null) {
			return results;
		}
		List<Attribute> attributes = new ArrayList<Attribute>();
		try {
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_ERP_CODE));
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_NAME));
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_PRICE));
			attributes.add(pi.getAttributes().getAttribute(PIConstants.ATTR_DESCRIPTION));
			ReportResult rr = pi.getReports().getProductList(category, null, null, 
					ProductInformer.VISIBILITY_ACTIVE, null, null, null, null, null, 
		            Reports.NO_LOGS, attributes, true, false, 
		            null, Reports.NO_SORT, false, 0, Reports.ALL_RESULTS, true, Reports.PRODUCTS_ACTIVE);

			List<ProductListItem> plis = rr.getResults();
			for (ProductListItem pli : plis) {
				if (pli == null) {
					continue;
				}
				Entity e = productListItem2Entity(pli);
				results.add(e);
			}
		} catch (StoreException e) {
			throw new RuntimeException(e);
		}
		
		return results;
	}

	private Category getCategoryByName(String categoryName) {
		try {
			List<Category> categories = pi.getCategories().getCategories();
			for (Category category : categories) {
				String realCategoryName = Utils.undoStripSpacesAndSlashes(categoryName);
				logger.debug("realCategoryName:" + realCategoryName);
				if (category.getFullName().equals(realCategoryName)) {
					return category;
				}
				if (category.getLevel()==2) {
					for (Category category2 : (List<Category>)category.getChildCategories()) {
						if (category2.getFullName().equals(realCategoryName)) {
							return category2;
						}
						if (category.getLevel()==3) {
							for (Category category3 : (List<Category>)category2.getChildCategories()) {
								if (category3.getFullName().equals(realCategoryName)) {
									return category3;
								}
							}
						}
					}
				}
			}
		} catch (StoreException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public List<String> getAllCategories() {
		List<String> results = new ArrayList<String>();
		try {
			List<Category> categories = pi.getCategories().getCategories();
			for (Category category : categories) {
				results.add(category.getFullName());
			}
		} catch (StoreException e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	@Override
	public boolean isCategory(String category) {
		return getAllCategories().contains(category);
	}

	@Override
	public List<String> getAllAttributeNames() {
		List<String> attributes = new ArrayList<String>();
		attributes.add(PIConstants.ATTR_ERP_CODE);
		attributes.add(PIConstants.ATTR_NAME);
		attributes.add(PIConstants.ATTR_PRICE);
		attributes.add(PIConstants.ATTR_DESCRIPTION);
		return attributes;
	}
}
