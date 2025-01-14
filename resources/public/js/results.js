function enterRow(e) {
    $('tr.selection').toggleClass('selection');
    var bottomOffset = 45;
    var popup = $('#popup');
    var offset = $(this).offset();
    var ttop = 0;
    var xOffset = document.documentElement.scrollTop;
    if ((popup.height() + offset.top + bottomOffset) > (xOffset + $(window).height())) {
        ttop = $(window).height() + xOffset - popup.height() - bottomOffset;
    } else {
        ttop = offset.top;
    }
    var id = $(this).data('id');
    $('#popup a').each(function () {
        var href = $(this).attr('href');
        $(this).attr('href', href.slice(0, href.indexOf('=') +1) + id);
    });
    popup.css({
        position: 'absolute',
        left: offset.left + $(this).width() + 8 + 'px',
        top: ttop + 'px'
    });
    popup.show();
    $(this).addClass('selection');
}
function clickRow(e) {
    var pref = $(this).find('td.pref').text();
    var num = $(this).find('td.num').text();
	var id = $(this).data('id');
    var t;
    var areq;
    $('tr.clicked').toggleClass('clicked');
    if (num) {
        $('#prefixfield').val(pref);
        $('#numfield').val(num);
        areq = pref + num.slice(0, num.indexOf('-'));
    } else {
        t = pref.match(/(.*?)(\.(УДЩ|\d{2})\.\d{1}\.\d{3}\.\d{1}(\.|\/)\d{2})(-\d{2})?/);
        $('#prefixfield').val(t[1]);
        $('#numfield').val(t[2]);
        areq = t[1] + t[2];
    }
    renderDocs(areq);
	renderParams(id);
    $(this).addClass('clicked');
    e.stopPropagation();
}

function clickProdRow(e) {
    var id = $(this).data('id');
    $('tr.clicked').toggleClass('clicked');
    if (id) {
        renderParams(id);
    }
    $(this).addClass('clicked');
    e.stopPropagation();
}

function renderDocs(fname) {
    $('#docs li').remove();
    $.getJSON(context + "/api/docs", {fname: fname},
        function(response){
           var list = $('#docs ul');
           list.html(createList(response.docs));
           $('#docs').show();
        });
}

function renderParams(id) {
	$('#params li').remove();
	    $.getJSON(context + "/api/params", {id: id},
        function(response){
           var list = $('#params ul');
           list.html(createParamsList(response, id));
           $('#params').show();
        });
}

function clickHeader(){
    var fname = $('h3[data-name]').data('name');
    var lastIdx = fname.lastIndexOf('-');
    if ((fname.length - 3) == lastIdx){
        renderDocs(fname.slice(0, lastIdx));
    } else {
        renderDocs(fname);
    }
}

function createList(docs) {
    var res = '';
    for (var i = 0; i < docs.length; i++) {
        res += '<li><a href="'+ context + '/docs?id=' +docs[i].id + 
            '">' + docs[i].fname + '</a></li>'
    }
    return res;
}

function createParamsList(params, id) {
	var res = '';
	if (params.length != 0) {
		for (var i = 0; i < params.length; i++) {
			res += '<li><p><b>' + params[i].name + '</b></p>' +
				'<p>' + params[i].value + '</p></li>'
		}
		res += '<li><a href="'+ context + '/unit/params/edit?id=' + id + 
				'">Редактировать параметры</a></li>'
	}
	return res;
}

function toDiff(e){
    e.preventDefault();
    var id = $(this).attr('href').slice(1);
    $('#diffForm').show();
    var id1 = $('#firstId');
    var id2 = $('#secondId');
    var tr = $('tr[data-id=' + id + ']');
    if (id1.val() == "") {
        id1.val(id);
        $('label[for="firstId"]').text(tr.find('td.pref').text() +
            tr.find('td.num').text());
    } else if (id2.val() == "") {
        id2.val(id);
        $('label[for="secondId"]').text(tr.find('td.pref').text() +
            tr.find('td.num').text());
    } else {
        id1.val(id2.val());
        $('label[for="firstId"]').text($('label[for="secondId"]').text());
        id2.val(id);
        $('label[for="secondId"]').text(tr.find('td.pref').text() +
            tr.find('td.num').text());
    }
}

function diff(e){
    console.log('diff');
    if ($('#firstId').val() == "" || $('#secondId').val() == "") {
        e.preventDefault();
        alert("Выберите еще узел для сравнения!");
    }
}

function joinBtn() {
    var tr = $('tr:first');
    $('th:contains(Обозначение)').append(
    '<button id="joinBtn" onclick="joinColumns()"title="Объединить">&lt;</button>');
    var btn =$('#joinBtn');
    btn.css({"position": "absolute",
    "top": "" + (tr.position().top + 1) + "px",
    "left": "" + (tr.position().left - 26) + "px"});
    btn.hide();
    tr.mouseenter(function(e){btn.show();});
    tr.mouseleave(function(e){btn.hide();});
}

function joinColumns() {
    var tr = $('tr:not(:first)');
    var pattern = /\.\d{1}\.\d{3}\.\d{1}(\.|\/)\d{2}/
    tr.each(function (index) {
        var num = $(this).find('td.num');
        if (num.length > 0) {
            if (num.text().search(pattern) != -1) {
                var pref = $(this).find('td.pref');
                pref.text(pref.text() + num.text());
                pref.prop('colspan',2);
                num.remove();
            }
        } else {
            var pref = $(this).find('td.pref');
            var t = pref.
                        text().
                        match(/(.*?)(\.(У.{1}Щ|\d{2})\.\d{1}\.\d{3}\.\d{1}(\.|\/)\d{2})(-\d{2})?/);
            $('<td class="num">' + t[2] + ((t[5])? t[5]:"") + '</td>').insertAfter(pref);
            pref.text(t[1]);
            pref.prop('colspan',1);
        }
    });
}
