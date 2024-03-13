function cellClick(e) {
    var id = $(this).data("id");
    var oldDate = $(this).text();
    $(this).html('<form action="'+ context +'/admin/updates" method="post">' +
                 '<input type="hidden" name="id" value="'+ id  + '">' +
                 '<input type="text" name="date" value="'+ oldDate +
                 '">' +
                 '</form>');
    $(this).off('click');
}
