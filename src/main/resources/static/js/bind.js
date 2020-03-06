$(function(){
	$("form").submit(check_data);
	$("input").focus(clear_error);
});