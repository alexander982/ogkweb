var searchHistory = '';

function initPage() {
    $('#searchForm').submit(formSubmit);
    loadSearchHistory();
    if (searchHistory) $('#history').show();
}

function formSubmit() {
    saveSearchReq({pref: $('#inputPrefix').val(),
                   num: $('#inputNumber').val(),
                   name: $('#inputName').val()});
}

function saveSearchReq(obj) {
    var historyLength = 10;
    if(searchHistory.split(';').length >= historyLength) {
        searchHistory = searchHistory.substring(0, searchHistory.lastIndexOf(';'));
        searchHistory = obj.pref + '+' + obj.num + '+' + obj.name + ';' + searchHistory;
    } else if (searchHistory) {
        searchHistory = '' + obj.pref + '+' + obj.num + '+' + obj.name + ';' + searchHistory;
    } else {
        searchHistory = '' + obj.pref + '+' + obj.num + '+' + obj.name;
    }
    localStorage.sHistory = searchHistory;
}

function loadSearchHistory() {
    searchHistory = localStorage.sHistory || '';
    var list = searchHistory.split(';');
    for (var i = 0; i < list.length; i++ ) {
        var pref = list[i].substring(0, list[i].indexOf('+'));
        var num = list[i].substring(list[i].indexOf('+') + 1, list[i].lastIndexOf('+'));
        var name = list[i].substring(list[i].lastIndexOf('+') + 1);
        $('#history li').append('<ul><a href="' + context + '/search/results?pref=' +
             encodeURI(pref) + '&num=' + encodeURI(num) + '&name=' + encodeURI(name) + '">' +
             pref + ' ' + num + ' ' + name + '</a></ul>');
    }
}