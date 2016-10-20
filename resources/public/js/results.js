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
