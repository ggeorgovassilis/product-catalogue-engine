package org.pce.database.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.pce.database.ProductDatabase;
import org.pce.model.Entity;
import org.pce.utils.Utils;
import org.springframework.core.io.FileSystemResource;

public class ProductDatabaseExcelImpl implements ProductDatabase {

	private final static Logger logger = Logger
			.getLogger(ProductDatabaseExcelImpl.class);
	private HSSFWorkbook workbook;
	private URL path;
	private Map<String, Entity> entities = new HashMap<String, Entity>();
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	long lastModifiedExcelDbTimestamp;
	long refreshInterval = 10000;

	public ProductDatabaseExcelImpl(URL url) throws Exception {
		this.path = url;
		loadExcelDbIfModified();
	}

	private void loadExcelDbIfModified() {
		try {
			_loadExcelDbIfModified();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void _loadExcelDbIfModified() throws IOException {

		long now = System.currentTimeMillis();
		if (lastModifiedExcelDbTimestamp+refreshInterval>now){
			return;
		}
		InputStream is = path.openStream();

		entities = new HashMap<String, Entity>();
		workbook = new HSSFWorkbook(is);
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			HSSFSheet sheet = workbook.getSheetAt(i);
			if (sheet == null)
				return;
			storeSheet(sheet);
		}
		is.close();
		lastModifiedExcelDbTimestamp = now;
	}

	protected void storeRow(HSSFRow row, HSSFRow columnNames) {
		Entity entity = new Entity();
		entity.setCategory(row.getSheet().getSheetName());
		for (int c = 0; c <= columnNames.getLastCellNum(); c++) {
			String columnName = text(columnNames.getCell(c));
			if (Utils.isEmpty(columnName))
				continue;
			String value = text(row.getCell(c));
			entity.setAttribute(columnName, value);
		}
		if (Utils.isEmpty(entity.getID()))
			return;
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
		loadExcelDbIfModified();
		return entities.get(ID);
	}

	@Override
	public List<Entity> getAllEntities() {
		loadExcelDbIfModified();
		return new ArrayList<Entity>(entities.values());
	}

	@Override
	public List<Entity> getAllEntitiesInCategory(String category) {
		loadExcelDbIfModified();
		List<Entity> list = new ArrayList<Entity>();
		for (Entity e : getAllEntities())
			if (e.getCategory().equals(category))
				list.add(e);
		return list;
	}

	@Override
	public List<String> getAllCategories() {
		loadExcelDbIfModified();
		List<String> categories = new ArrayList<String>();
		for (Entity e : getAllEntities())
			if (!categories.contains(e.getCategory()))
				categories.add(e.getCategory());
		return categories;
	}

	@Override
	public boolean isCategory(String category) {
		loadExcelDbIfModified();
		return getAllCategories().contains(category);
	}

	@Override
	public List<String> getAllAttributeNames() {
		loadExcelDbIfModified();
		// TODO: replace iteration with a smarter approach, i.e. cache
		// categories
		List<Entity> entities = getAllEntities();
		Set<String> attributes = new HashSet<String>();
		for (Entity e : entities)
			attributes.addAll(e.keySet());
		return new ArrayList<String>(attributes);
	}

	public void refreshDB() throws IOException {
		loadExcelDbIfModified();
	}

}
