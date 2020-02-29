$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//post
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			//show popup
			$("#hintModal").modal("show");
			//2s
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//refresh page
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}