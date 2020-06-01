var stompClient = null;
$(function () {
    $("#sendBtn").click(sendMessage);
    $(".close").click(delete_msg);
    connect();
});

function connect() {
    var socket = new SockJS("/chat");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connect: " + frame);
        stompTopic();
    });
}

function stompTopic() {
    var aHeader = $("#aheader").val();
    var str = "";
    var userId = $("#userId").val();
    var Words = document.getElementById("words");
    console.log(userId);
    stompClient.subscribe('/OneToOne/' + userId + '/alone',
        function (response) {
            var message = JSON.parse(response.body);
            var TalkWords = message.content;
            console.log("message: " + TalkWords);
            str = '<div class="atalk"><img src= ' + aHeader +
                ' class="mr-4 rounded-circle user-header"\n' +
                '                                         alt="profile picture">' +
                '<span>' + TalkWords + '</span></div>';
            Words.innerHTML = Words.innerHTML + str;
        });
}

function sendMessage() {
    var bHeader = $("#bheader").val();
    var content = $("#talkwords").val();
    var postValue = {};
    var fromId = $("#userId").val();
    var toId = $("#recipient-id").val();
    postValue.content = content;
    postValue.toId = toId;
    postValue.fromId = fromId;
    var Words = document.getElementById("words");
    var str = "";
    if (content.value == "") {
        // Popup when message is empty
        alert("Message cannot be empty");
        return;
    }
    //If Who.value is 0n then A says
    str = '<div class="btalk"><span>' + content + '</span>' +
        '<img src=' + bHeader +
        ' class="mr-4 rounded-circle user-header"\n' +
        '                                         alt="profile picture">' +
        '</div>';
    Words.innerHTML = Words.innerHTML + str;
    stompClient.send("/reply", {}, JSON.stringify(postValue));
    $("#talkwords").val("");
    var toName = $("#toName").val();
    console.log("tar name: " + toName);
    $.post(
        CONTEXT_PATH + "/letter/send",
        {"toName": toName, "content": content, "online": 1},
        function (data) {
            data = $.parseJSON(data);
            if (data.code != 0) {
                $("#hintBody").text(data.msg);
                $("#hintModal").modal("show");
                setTimeout(function () {
                    $("#hintModal").modal("hide");
                    location.reload();
                }, 2000);
            }
        }
    );
}
function delete_msg() {
    $(this).parents(".media").remove();
}