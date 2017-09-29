function selectOneRow (e){
    $('tr.selection').toggleClass('selection');
    $(this).toggleClass('selection');
    var id = $(this).data('id');
    $('#popup a').each(function () {
        var href = $(this).attr('href');
        $(this).attr('href', href.slice(0, href.indexOf('=') +1) + id);
    });
    $('#popup').dialog('option', { position: {my: 'left top+12',
                                              of: e}
                                   });
    $('#popup').dialog('open');
}

function enterRow(e) {
    $('tr.selection').toggleClass('selection');
    var popup = $('#popup');
    var offset = $(this).offset();
    var ttop = 0;
    if ((popup.height() + offset.top + 5) > $(document).height()) {
        ttop = $(document).height() - popup.height() - 15;
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
