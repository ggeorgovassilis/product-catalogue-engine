package org.pce.database.pi;

import gr.open.pi.Attribute;
import gr.open.pi.Binary;
import gr.open.pi.Category;
import gr.open.pi.PIConstants;
import gr.open.pi.Product;
import gr.open.pi.ProductInformer;
import gr.open.pi.Reports;
import gr.open.pi.Reports.ProductListItem;
import gr.open.pi.Reports.ReportResult;
import gr.open.pi.StoreException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.pce.database.ProductDatabase;
import org.pce.model.Entity;
import org.pce.utils.Utils;

public class PiDatabase implements ProductDatabase {

	
	private static final Logger logger = Logger.getLogger(PiDatabase.class);
	private ProductInformer pi;
	
	public PiDatabase(ProductInformer pi) {
		this.pi = pi;
	}
	
	@Override
	public Entity readEntity(String ID) {
		//PI does only numbers
		if (!Utils.isNumber(ID))
			return null;
		ID = Utils.makeInteger(ID);
		List<Integer> productIds = new ArrayList<Integer>();
		productIds.add(Integer.parseInt(ID));
		try {
			ReportResult rr = pi.getReports().getProductListByIds(productIds, getAttributes(), 0, false, 0, 10, 1);
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
//		e.setCategory(Utils.stripSpacesAndSlashes(c.getFullName()));
		e.setCategory(Utils.normalizeCategoryName(c.getFullName()));
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
		try {
			ReportResult rr = pi.getReports().getProductList(category, null, null, 
					ProductInformer.VISIBILITY_ACTIVE, null, null, null, null, null, 
		            Reports.NO_LOGS, getAttributes(), true, false, 
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
	
	private List<Category> getCategoriesRecursively(Category category) throws StoreException{
		List<Category> categories = new ArrayList<Category>();
		if (category.isLeaf())
			return categories;
		List<Category> origCategories = category.getChildCategories();
		categories.addAll(origCategories);
		for (Category c:origCategories){
			categories.addAll(getCategoriesRecursively(c));
		}
		return categories;
	}

	private Category getCategoryByName(String categoryName) {
		try {
			String realCategoryName = Utils.undoStripSpacesAndSlashes(categoryName);
			logger.debug("looking for category:" + realCategoryName);
			List<Category> allCategories = new ArrayList<Category>();
			List<Category> categories = pi.getCategories().getCategories();
			for (Category category : categories) {
				allCategories.add(category);
				allCategories.addAll(getCategoriesRecursively(category));
			}
			for (Category category : allCategories) {
				String normName = Utils.normalizeCategoryName(category.getFullName());
				if (normName.equals(categoryName)) {
					return category;
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
			List<Category> origCategories = pi.getCategories().getCategories();
			List<Category> categories = new ArrayList<Category>(origCategories);
			for (Category category : origCategories) {
				categories.addAll(getCategoriesRecursively(category));
			}
			for (Category c:categories) {
				// excluding static categories (they don't contain products)
				if (!c.isStatic()) {
					results.add(c.getFullName());
				}
			}
		} catch (StoreException e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	@Override
	public boolean isCategory(String category) {
		String normCategory = Utils.normalizeCategoryName(category);
		List<String> categoryNames = getAllCategories(); 
		for (String c:categoryNames){
			String nc = Utils.normalizeCategoryName(c); 
			if (nc.equals(normCategory))
				return true;
		}
		return false;
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
	
	public ProductInformer getPi() {
		return this.pi;
	}
	
	public void streamBinary(HttpServletResponse response, int productId, int variant) {
		try {
			
			Product p = pi.getProducts().getProduct(productId);
			Attribute a = pi.getAttributes().getAttribute(PIConstants.ATTR_IMAGE);
			Binary b = p.getBinary(a);
			
			response.setContentType(b.getContentType());
			b.write(variant, response.getOutputStream());
		}
		catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}
	
	public List<Attribute> getAttributes() {
		List<Attribute> results = new ArrayList<Attribute>();
		for (String attributeName : getAllAttributeNames()) {
			try {
				results.add(pi.getAttributes().getAttribute(attributeName));
			} catch (StoreException e) {
				throw new RuntimeException(e);
			}
		}
		return results;
	}
}
