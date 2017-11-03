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
    $('#prefixfield').attr('value', $(this).find('td.pref').text());
    $('#numfield').attr('value', $(this).find('td.num').text());
    e.stopPropagation();
}
