function insertToForm(row) {
    var tbl = row.parentElement
    
    var rows = tbl.getElementsByTagName('tr')
    
    for (i = 0; i < rows.length; i++) {
	rows[i].className = "";
	//console.log(rows[i])
    }
    var pref = document.getElementsByName('pref')
    var num = document.getElementsByName('num')

    for (i = 0; i < pref.length; i++) {
	pref[i].value = row.cells[0].childNodes[0].data
    }
    for (i = 0; i < num.length; i++){
	num[i].value = row.cells[1].childNodes[0].data
    }

    row.className = "selection";
}
