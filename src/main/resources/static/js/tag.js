$(function () {
    $("#sendBtn").click(addTags);
    $("#categoryBtn").click(addCategory);
});

function addCategory() {
    $("#addCategory").modal("hide");
    var categoryName=$("#Category-name").val();
    $.post(
        CONTEXT_PATH + "/addCategory",
        {"category": categoryName},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintBody").text("Send Successful!!");
            } else {
                $("#hintBody").text(data.msg);
            }
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    );
}

function addTags() {
    $("#sendModal").modal("hide");
    var tagName = $("#recipient-name").val();
    var selectCategory=$("#category-text").val();
    $.post(
        CONTEXT_PATH + "/addTags",
        {"tag": tagName,"category":selectCategory},
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