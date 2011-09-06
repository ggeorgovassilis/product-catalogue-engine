function log(message){
	__pce_logger.log(__pce_logging_priority, "script: "+message);
}

function date(s){
	var parts = /(\d\d)\/(\d\d)\/(\d\d\d\d)/.exec(s);
	if (!parts)
		throw s+" is not a date";
	var s = parts[3]+parts[2]+parts[1];
	return s;
}

function javaListToArray(list){
	var a = new Array();
	for (var i=0;i<list.size();i++)
		a[a.length] = list.get(i);
	return a;
}

function javaSetToArray(list){
	var a = new Array();
	for (var ite=list.iterator();ite.hasNext();)
		a[a.length] = ite.next();
	return a;
}


function isNumber(x){
	return ""+parseFloat(x)!="NaN";
}

function removeDuplicatesFromSortedList(list){
	var result = new Array();
	var oldValue = "_____noway";
	for (var i=0;i<list.length;i++){
		var value = list[i];
		if (value == oldValue)
			continue;
		result[result.length] = value;
		oldValue = value;
	}
	return result;
}

