$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$(btn).text("Followed").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		$(btn).text("Follow him/her").removeClass("btn-secondary").addClass("btn-info");
	}
}