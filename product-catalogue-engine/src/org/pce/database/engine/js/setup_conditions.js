var __pce_all_attributes = javaListToArray(__pce_all_attributes_java);

function loadProductWithAttributes(p){
	var attributeNames = p.__orig.getAttributeNames();
	attributeNames = javaSetToArray(attributeNames);
	
	// reset all known attributes
	for (var i = 0;i<__pce_all_attributes.length;i++){
		var attributeName = ""+__pce_all_attributes[i];
		p[attributeName] = false;
	}
	for (var i = 0;i<attributeNames.length;i++){
		var attributeName = attributeNames[i];
		var attributeValue = p.__orig.getAttribute(attributeName);
		p[attributeName] = attributeValue;
	}
}

var Product = {
		__orig:__pce_product,
		Type:13
};


loadProductWithAttributes(Product);