package org.pce.database.gdata;

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
import org.pce.database.ProductDatabase;
import org.pce.model.Entity;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class ProductDatabaseGDataImpl implements ProductDatabase {

	private final static Logger logger = Logger
			.getLogger(ProductDatabaseGDataImpl.class);
	private Map<String, Entity> entities = new HashMap<String, Entity>();
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	private SpreadsheetService service;
	private FeedURLFactory factory;
	private String identifier;
	private long timestamp;
	private long expiration = 30000;

	public ProductDatabaseGDataImpl(String user, String password,
			String identifier) throws Exception {
		factory = FeedURLFactory.getDefault();
		service = new SpreadsheetService("gdata-sample-spreadhsheetindex");
		//service.setUserCredentials(user, password);
		this.identifier = identifier;
	}

	private void loadDbIfModified() {
		try {
			long now = System.currentTimeMillis();
			if (timestamp+expiration>now)
				return;
			timestamp = now;
			_loadDbIfModified();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void _loadDbIfModified() throws Exception {
		String url = "https://docs.google.com/spreadsheet/ccc?key=0Am9D7ThqOUJidGxqdjBrSTlTUnMyMEhiZVdTcktYdEE";
		SpreadsheetFeed feed = service.getFeed(new URL(url), SpreadsheetFeed.class);
		List<SpreadsheetEntry> list = feed.getEntries();
		for (SpreadsheetEntry e : list) {
			if (identifier.equals(e.getTitle().getPlainText())) {
				loadSpreadsheet(e);
				return;
			}
		}
	}

	private void readEntities(WorksheetEntry worksheet) throws Exception {
		List<String> headers = new ArrayList<String>();
		URL cellFeedUrl = worksheet.getCellFeedUrl();
		CellQuery cellQuery = new CellQuery(cellFeedUrl);

		CellFeed topRowCellFeed = service.query(cellQuery, CellFeed.class);
		List<CellEntry> cellEntries = topRowCellFeed.getEntries();
		int currentRow = 1;
		Entity entity = null;
		for (CellEntry entry : cellEntries) {
			Cell cell = entry.getCell();
			if (cell.getRow()==1){
				headers.add(cell.getValue());
				continue;
			}
			if (currentRow!=cell.getRow()){
				currentRow = cell.getRow();
				if (entity!=null)
					entities.put(entity.getID(), entity);
				entity = new Entity();
				entity.setCategory(worksheet.getTitle().getPlainText());
			}
			entity.setAttribute(headers.get(cell.getCol()-1), cell.getValue());
		}
		if (entity!=null)
			entities.put(entity.getID(), entity);
	}

	private void loadSpreadsheet(SpreadsheetEntry e) throws Exception {
		for (WorksheetEntry w : e.getWorksheets()) {
			readEntities(w);
		}
	}

	@Override
	public Entity readEntity(String ID) {
		loadDbIfModified();
		Entity e = entities.get(ID);
		logger.info("Looking for "+ID+" and found "+e);
		return e;
	}

	@Override
	public List<Entity> getAllEntities() {
		loadDbIfModified();
		return new ArrayList<Entity>(entities.values());
	}

	@Override
	public List<Entity> getAllEntitiesInCategory(String category) {
		loadDbIfModified();
		List<Entity> list = new ArrayList<Entity>();
		for (Entity e : getAllEntities())
			if (e.getCategory().equals(category))
				list.add(e);
		return list;
	}

	@Override
	public List<String> getAllCategories() {
		loadDbIfModified();
		List<String> categories = new ArrayList<String>();
		for (Entity e : getAllEntities())
			if (!categories.contains(e.getCategory()))
				categories.add(e.getCategory());
		return categories;
	}

	@Override
	public boolean isCategory(String category) {
		loadDbIfModified();
		return getAllCategories().contains(category);
	}

	@Override
	public List<String> getAllAttributeNames() {
		loadDbIfModified();
		// TODO: replace iteration with a smarter approach, i.e. cache
		// categories
		List<Entity> entities = getAllEntities();
		Set<String> attributes = new HashSet<String>();
		for (Entity e : entities)
			attributes.addAll(e.keySet());
		return new ArrayList<String>(attributes);
	}

	public void refreshDB() {
		loadDbIfModified();
	}

}
