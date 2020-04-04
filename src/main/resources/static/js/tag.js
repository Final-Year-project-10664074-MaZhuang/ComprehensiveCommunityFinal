$(function () {
    $("#sendBtn").click(addTags);
});

function addTags() {
    $("#sendModal").modal("hide");
    var tagName = $("#recipient-name").val();
    $.post(
        CONTEXT_PATH + "/addTags",
        {"tag": tagName},
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