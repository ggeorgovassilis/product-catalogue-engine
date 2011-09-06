package org.pce.database.excel;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.pce.database.ProductDatabase;
import org.pce.model.Entity;
import org.pce.utils.Utils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ProductDatabaseExcelImpl implements ProductDatabase {

	private HSSFWorkbook workbook;
	private Map<String, Entity> entities = new HashMap<String, Entity>();
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public ProductDatabaseExcelImpl(String file) throws Exception {
		InputStream is = Utils.getResourceAsString(file);
		if (is == null)
			throw new IllegalArgumentException("Couldn't resolve "+file+" on classpath");
		workbook = new HSSFWorkbook(is);
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			HSSFSheet sheet = workbook.getSheetAt(i);
			if (sheet == null)
				return;
			storeSheet(sheet);
		}
	}

	protected void storeRow(HSSFRow row, HSSFRow columnNames) {
		Entity entity = new Entity();
		entity.setCategory(row.getSheet().getSheetName());
		for (int c = 0; c <= columnNames.getLastCellNum(); c++) {
			String columnName = text(columnNames.getCell(c));
			String value = text(row.getCell(c));
			entity.setAttribute(columnName, value);
		}
		if (entities.containsKey(entity.getID()))
			throw new RuntimeException("Duplicate entity " + entity.getID());
		entities.put(entity.getID(), entity);
	}

	protected void storeSheet(HSSFSheet sheet) {
		HSSFRow headRow = sheet.getRow(0);
		for (int r = 1; r <= sheet.getLastRowNum(); r++) {
			HSSFRow row = sheet.getRow(r);
			storeRow(row, headRow);
		}
	}

	protected String text(HSSFCell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getRichStringCellValue().getString();
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return "" + df.format(cell.getDateCellValue());
			} else {
				return "" + cell.getNumericCellValue();
			}
		case Cell.CELL_TYPE_BOOLEAN:
			return "" + cell.getBooleanCellValue();
		case Cell.CELL_TYPE_FORMULA:
			return "" + cell.getCellFormula();
		default:
			return "";
		}
	}

	protected String text(HSSFSheet sheet, int row, int column) {
		return text(sheet.getRow(row).getCell(column));
	}

	@Override
	public Entity readEntity(String ID) {
		return entities.get(ID);
	}

	@Override
	public List<Entity> getAllEntities() {
		return new ArrayList<Entity>(entities.values());
	}

	@Override
	public List<Entity> getAllEntitiesInCategory(String category) {
		List<Entity> list = new ArrayList<Entity>();
		for (Entity e : getAllEntities())
			if (e.getCategory().equals(category))
				list.add(e);
		return list;
	}

	@Override
	public List<String> getAllCategories() {
		List<String> categories = new ArrayList<String>();
		for (Entity e : getAllEntities())
			if (!categories.contains(e.getCategory()))
				categories.add(e.getCategory());
		return categories;
	}

	@Override
	public boolean isCategory(String category) {
		return getAllCategories().contains(category);
	}

	@Override
	public List<String> getAllAttributeNames() {
		//TODO: replace iteration with a smarter approach, i.e. cache categories
		List<Entity> entities = getAllEntities();
		Set<String> attributes = new HashSet<String>();
		for (Entity e:entities)
			attributes.addAll(e.keySet());
		return new ArrayList<String>(attributes);
	}

}
