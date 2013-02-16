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
		url = './JsonPresenter?type=single&q=/ComicsDB/comics/' + value + '/'
				+ document.getElementById('comic_' + value).value;
		break;
	case 'field':
		url = './JsonPresenter?type=array&q=/ComicsDB/comics/'
				+ document.getElementById('comic_param').value + '/'
				+ methods.options[methods.selectedIndex].value + '/'
				+ document.getElementById('comic_value').value + '/';
		break;
	}
	if (!confirm(url))
		return false;
	location.href = url;
}

function startPolling(url, containerDivId, interval) {
	alert('Set polling...');
	setInterval(pollJsonService(url, containerDivId), interval);
}

function pollJsonService(url, containerDivId) {
	alert('Polling...');
	$.ajax({
		type : "GET",
		url : url,
		cache : false,
		timeout : 50000 /* ms */,
		success : function(data) {
			alert('Polling OK...');
			var parent = document.getElementById(containerDivId);
			parent.appendChild('<p>' + data + '</p>');
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error on polling...');
			var parent = document.getElementById(containerDivId);
			parent.appendChild('<p>' + textStatus + '<br />' + errorThrown
					+ '</p>');
		}
	});
}