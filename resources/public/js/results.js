function enterRow(e) {
    $('tr.selection').toggleClass('selection');
    var bottomOffset = 15;
    var popup = $('#popup');
    var offset = $(this).offset();
    var ttop = 0;
    if ((popup.height() + offset.top + bottomOffset) > $(document).height()) {
        ttop = $(document).height() - popup.height() - bottomOffset;
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
    $('#prefixfield').val($(this).find('td.pref').text());
    $('#numfield').val($(this).find('td.num').text());
    e.stopPropagation();
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