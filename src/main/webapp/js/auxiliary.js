// auxiliary functions for common usage
function showHide(element_id) {
	if (document.getElementById(element_id)) { 
		var obj = document.getElementById(element_id); 
		if (obj.style.display != "block") { 
			obj.style.display = "block"; 
		}
		else obj.style.display = "none";
	}
}   
