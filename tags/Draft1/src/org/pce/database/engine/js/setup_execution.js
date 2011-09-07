var context = {
		product:__pce_product,
		originalProduct:__pce_product,
		condition:new Array(),
		__pce_evaluate:function(context){
			var result = true;
			log("Evaluating "+context.condition.length);
			for (var i=0;i<context.condition.length;i++){
				var b=context.condition[i](context.product, context.attribute, true);
				result = result && b;
				}
			return result;
		}
};


var Product = {
		__noSuchMethod__:function(id, args){
			return context.product.getAttribute(id);
		}
};

context.__noSuchMethod__=function(id, args){
	var context = this;
	if (__pce_db.isCategory(id)){
		var list = __pce_db.getAllEntitiesInCategory(id);
		list = javaListToArray(list);
		var filteredList = new Array();
		if (context.condition.length>0){
			for (var i=0;i<list.length;i++){
				for (var c=0;c<context.condition.length;c++)
					if (context.condition[c](list[i], context.attribute))
						filteredList[filteredList.length] = list[i].getID();
			}}
		else{
			// keep IDs
			for (var i=0;i<list.length;i++)
				filteredList.push(list[i].getID());
		}
		filteredList.sort();
		filteredList = removeDuplicatesFromSortedList(filteredList);
		context.value = filteredList;
		return;
	}

	context.attribute = id;
	if (!context.value)
		context.value = context.product.getAttribute(id);
	log("property "+id+" is "+context.value);	
	return context;
}

function nop(context){
	return context;
}

function newsection(context){
	context.attribute = null;
	context.value = null;
	context.product = context.originalProduct;
	context.condition = new Array();  
	return context;
}

context.of = nop;
context.or = nop;
context.to=nop;
context.by=nop;
context.than=nop;
context.where=nop;
context.and=newsection;

context.abort = function(context){
	__pce_active = false;
	return context;
}

context.less = function(context){
	var comparisson = context.value;
	if (isNumber(comparisson))
		comparisson = parseFloat(comparisson);
	log("less than "+comparisson)
	context.condition.push(function(product, attribute){
		var value = product.getAttribute(attribute);
		return value < comparisson;
	});
	return context;
}

context.is = context.equal = function(context){
	var comparisson = context.value;
	if (isNumber(comparisson))
		comparisson = parseFloat(comparisson);
	log("equal "+comparisson)
	context.condition.push(function(product, attribute){
		var value = product.getAttribute(attribute);
		return value == comparisson;
	});
	return context;
}

context.startswith = function(context){
	var _comparisson = context.value;
	context.condition.push(function(product, attribute, getDataFromGlobal){
		var value = product.getAttribute(attribute);
		var result = (""+value).indexOf(_comparisson) == 0;
		log(attribute+"/"+value+"/"+_value+" starts with "+_comparisson+": "+result);
		return result;
	});
	return context;
}

context.DB= function(context){
	var id = context.value;
	log("read DB "+id);
	var e = __pce_db.readEntity(id);
	context.product = e;
	context.attribute = undefined;
	context.value = undefined;
	return context;
}

context.set = function(context){
	hardlog("set  "+context.attribute+" = "+context.value);
	context.originalProduct.setAttribute(context.attribute,context.value);
	return context;
}

context.literal = function(context, v){
	context.value = v;
	log("literal "+v);
	return context;
}


context.discount = function(context){
	var value = context.value;
	if (/\d+%/.test(value)){ // percentage
		var attr = context.attribute;
		var origValue = parseFloat(context.originalProduct.getAttribute(attr)); 
		value = origValue - origValue * 0.01 * parseFloat(value);
	} else {
		value = parseFloat(value);
	}
	hardlog("discount "+attr + " = "+value);
	context.originalProduct.setAttribute(""+attr, value);
	return context;
}

context.increase = function(context){
	var value = context.value;
	value = parseFloat(value);
	var attr = context.attribute;
	var currentValue = parseFloat(context.originalProduct.getAttribute(attr));
	if (!isNumber(currentValue))
		currentValue = 0;
	var newValue = ""+(currentValue+value);
	hardlog("increase "+attr + " of "+currentValue+" by "+value + " to "+newValue);
	context.originalProduct.setAttribute(attr, newValue);
	hardlog("which now is "+context.originalProduct.getAttribute(attr));
	return context;
}

