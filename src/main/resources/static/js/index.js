$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");

    //Set the csrf token to the request message header before sending the ajax request
    /*	var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function (e,xhr,options) {
            xhr.setRequestHeader(header,token);
        });*/

    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    var tag = $("#tag-name").val();
    //post
    $.post(
        CONTEXT_PATH + "/discuss/add",
        {"title": title, "content": content, "tag": tag},
        function (data) {
            data = $.parseJSON(data);
            $("#hintBody").text(data.msg);
            //show popup
            $("#hintModal").modal("show");
            //2s
            setTimeout(function () {
                $("#hintModal").modal("hide");
                //refresh page
                if (data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
        }
    );
}

function showSelectTag() {
    $("#selectTags").show();
}

function selectTag(e) {
    var value = e.getAttribute("data-tag");
    console.log(value);
    var tag = $("#tag-name").val();
    console.log(tag);
    if (tag.split(',').indexOf(value) == -1) {
        if (tag) {
            $("#tag-name").val(tag + ',' + value);
        } else {
            $("#tag-name").val(value);
        }
    }
}