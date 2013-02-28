function getBy() {
	var radios = document.getElementsByName('getBy');
	var value = 'id';
	for ( var i = 0; i < radios.length; i++) {
		if (radios[i].type === 'radio' && radios[i].checked) {
			value = radios[i].value;
			break;
		}
	}
	var url = '';
	var methods = document.getElementById('method');
	switch (value) {
	case 'id':
		url = './JsonPresenter?type=single&q=/ComicsDB/comics/'
				+ value
				+ '/'
				+ encodeURIComponent(document.getElementById('comic_' + value).value);
		break;
	case 'field':
		url = './JsonPresenter?type=array&q=/ComicsDB/comics/'
				+ encodeURIComponent(document.getElementById('comic_param').value)
				+ '/'
				+ encodeURIComponent(methods.options[methods.selectedIndex].value)
				+ '/'
				+ encodeURIComponent(document.getElementById('comic_value').value)
				+ '/';
		break;
	}
	if (!confirm(url))
		return false;
	location.href = url;
}

function pollJsonService(url, containerDivId) {
	$.ajax({
		type : "GET",
		url : url,
		cache : false,
		timeout : 50000 /* ms */,
		success : function(data) {
			if (data) {
				var parent = document.getElementById(containerDivId);
				parent.innerHTML += '<p>' + stringify(data) + '</p>';
				if (data.progressIndex >= 100) {
					stopPolling();
					parent.innerHTML += '<h1>Operation completed!</h1>';
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			var parent = document.getElementById(containerDivId);
			parent.innerHTML += '<p>' + textStatus + '<br />' + errorThrown
					+ '<br />' + XMLHttpRequest + '</p>';
		}
	});
}

function delJsonService(url) {
	alert('Calling \'' + url + '\' with DELETE HTTP method');
	$.ajax({
		type : "DELETE",
		url : url,
		cache : false,
		timeout : 50000 /* ms */,
		success : function(data) {
			alert('Synchronization deleted successfully!');
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(+textStatus + '\n' + errorThrown + '\n' + XMLHttpRequest);
		}
	});
}
