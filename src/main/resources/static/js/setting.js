$(function () {
    $("#uploadForm").submit(upload);
});

function upload() {
    $.ajax({
        url:"http://upload-na0.qiniup.com",
        method:"post",
        processData:false,
        contentType:false,
        data:new FormData($("#uploadForm")[0]),
        success:function (data) {
            if(data&&data.code==0){
                //update header url
                $.post(
                  CONTEXT_PATH+"/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function (data) {
                        data = $.parseJSON(data);
                        if(data.code==0){
                            window.location.reload();
                        }else {
                            alert(data.msg);
                        }
                    }
                );
            }else {
                alert("File upload failed")
            }
        }
    });

    return false;
}