var thisActionId;
function setActionId(actionId){
	thisActionId = actionId;
}

function mockOpenApiClick(){

	var actionId = thisActionId;
	var action = ws.getAction(actionId);
	
	var data = $('#mockDataPreviewFloater-container').val();
	console.log(data);
	
	$.ajax({
		type : 'POST',
		url : '/mock/mockOpenApi.do',
		data : {
			"actionUrl" : action.requestUrl,
			"description" : action.description,
        	"apiData" : data,
        },
		dataType : "json",
		success : function(data){
			alert("成功：" + JSON.stringify(data));
		},
		error: function(data){
			alert("失败：" + JSON.stringify(data));
		},
		
	});
	
}