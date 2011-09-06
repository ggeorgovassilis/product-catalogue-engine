package org.pce.web;

import org.pce.database.ProductDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BaseController {
	
	
	@RequestMapping(value="/welcome", method=RequestMethod.GET)
	public String getView(Model model) throws Exception{

		return "welcome";
	}

}
