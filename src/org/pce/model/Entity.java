package org.pce.model;

import java.util.HashMap;
import java.util.Set;

public class Entity extends HashMap<String, String>{


	public Object __noSuchMethod__;
	
	public Entity clone(Entity e){
		Entity copy = new Entity();
		copy.putAll(this);
		return copy;
	}
	
	public boolean hasAttribute(String name){
		return this.containsKey(name);
	}
	
	public Set<String> getAttributeNames(){
		return keySet();
	}
	public String getAttribute(String name){
		return get(name);
	}
	
	public int getAttributeInt(String name){
		return (int)Double.parseDouble(getAttribute(name));
	}

	public void setAttribute(String name, String value){
		put(name, value);
	}
	
	public String getCategory(){
		return getAttribute("__category");
	}
	
	public void setCategory(String category){
		setAttribute("__category", category);
	}
	
	public String getID(){
		return getAttribute("ID");
	}
	
	public void setID(String id){
		setAttribute("ID", id);
	}
	
	@Override
	public String toString() {
		String s = getID()+"{";
		String prefix="";
		for (String attr:keySet()){
			String a = getAttribute(attr);
			s+=attr+"="+a+prefix;
			prefix=",";
		}
		s+="}";
		return super.toString();
	}
	
	public boolean debug(){
		String v = getAttribute("Debug");
		return "yes".equals(v);
	}
		
}
