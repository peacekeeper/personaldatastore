function listen() {
	$.get("/Packet", {}, function(data) {
		$("#packet").text(data.content);
		listen();
	});
};

$(document).ready(function(){
	listen();
	$("#packet").text("listening...");
});
