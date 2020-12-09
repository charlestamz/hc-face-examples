function messageFormat(text, obj) {
	if (!obj) {
		return text;
	}
	for ( var key in obj) {
		text = text.replace(new RegExp("#" + key + "#", "g"), obj[key]);
	}
	return text;
}
String.prototype.format = function(obj) {
	return messageFormat(this, obj);
};
String.prototype.juicer = function(obj) {
	return juicer && juicer(this, obj);
};
function formToObj(form) {
	var o = {};
	$("input,textarea,select", form).each(function(index) {
		var val = null;
		if ($(this).is(":radio")) {
			if (!$(this).is(":checked")) {
				return true;
			}
		}
		if ($(this).is(":checkbox")) {
			val = $(this).is(":checked");
		} else {
			val = $(this).val();
		}
		if (o[this['name']]) {
			o[this['name']] = o[this['name']] + "," + val;
		} else {
			o[this['name']] = val;
		}
		if (!val) {
			val = null;
		}
	});
	return o;
}
//改进上面的方法，复选框时返回选中框的val
function formToObjWithCheckboxVal(form) {
	var o = {};
	$("input,textarea,select", form).each(function(index) {
		var val = null;
		if ($(this).is(":radio")) {
			if ($(this).is(":checked")) {
				val = $(this).val();
			}
		}else if ($(this).is(":checkbox")) {
			if($(this).is(":checked")){
				val = $(this).val();
			}else{
				val = null;
			}
		} else {
			val = $(this).val();
		}
		
		if(null != val){
			if (o[this['name']]) {
				o[this['name']] = o[this['name']] + "," + val;
			} else {
				o[this['name']] = val;
			}
			if (!val) {
				val = null;
			}
		}
	});
	return o;
}

function objToForm(form, obj,untostr) {
	for ( var name in obj) {
		$("input[name='" + name + "'],textarea[name='" + name + "'],select[name='" + name + "']", form).val(obj[name]==null?null:untostr?obj[name]:obj[name].toString());
	}
}
