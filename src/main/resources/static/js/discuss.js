$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "postId": postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? 'Liked' : 'Likes');
            } else {
                alert(data.msg);
            }
        }
    );
}

//Sticky
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#topBtn").attr("disabled", "disabled")
            } else {
                alert(data.msg);
            }
        }
    );
}

//Set to essence
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#wonderfulBtn").attr("disabled", "disabled")
            } else {
                alert(data.msg);
            }
        }
    );
}

//delete
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                location.href = CONTEXT_PATH + "/discussIndex";
            } else {
                alert(data.msg);
            }
        }
    );
}
var second = 0;
window.setInterval(function () {
    if($("#isLogin").val()!=null){
        //console.log("not null");
        second++;
    }else {
        //console.log($("#isLogin").val());
    }
});

window.onbeforeunload = function () {
    if($("#isLogin").val()!=null){
        if (second >= 10000) {
            $.post(
                CONTEXT_PATH + "/discuss/visitTime",
                {"postId": $("#postId").val(), "second": second / 1000, "AuthorID": $("#AuthorID").val()},
                function (data) {
                    data = $.parseJSON(data);
                    if (data.code == 0) {

                    } else {
                        alert(data.msg);
                    }
                }
            );
        } else {
            console.log("eeeee");
        }
    }
};