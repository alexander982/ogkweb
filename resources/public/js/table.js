function insertToForm(row, event) {
    var tbl = row.parentElement
    
    var rows = tbl.getElementsByTagName('tr')
    
    for (i = 0; i < rows.length; i++) {
	rows[i].className = "";
	//console.log(rows[i])
    }
    var pref = document.getElementsByName('pref')
    var num = document.getElementsByName('num')
    var contId = document.getElementsByName('cont-id')

    if (pref || num) {
	for (i = 0; i < pref.length; i++) {
	    pref[i].value = row.cells[0].childNodes[0].data
	}
	for (i = 0; i < num.length; i++){
	    num[i].value = row.cells[1].childNodes[0].data
	}
    }

    if (contId) {
	for (i = 0; i < contId.length; i++) {
	    contId[i].value = row.cells[0].childNodes[0].data
	}
    }
    row.className = "selection";
    showForm(row, event)
}

function showForm(row, event) {
    var frm = document.getElementById('hidenForms')
    if (frm) {
	frm.style.top = event.clientY + getScrollOffset().y + "px"
	frm.style.left = event.clientX + getScrollOffset().x + "px"
	console.log(frm.offsetTop)
    }
    //frm.getElementsByName('pref')[0].value = row.cells[0].childNodes[0].data
    //frm.getElementsByName('num')[0].value = row.cells[1].childNodes[0].data
}

function getScrollOffset () {
    if (window.pageXOffset != null) 
        return {x:window.pageXOffset, y:window.pageYOffset}
    if (document.compatMode == "CSS1Compat")
        return {x:document.documentElement.scrollLeft,
                y:document.documentElement.scrollTop};
    return {x:document.body.scrollLeft, y:document.body.scrollTop}
}
