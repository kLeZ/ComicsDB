function selectOnFocus(radioID) {
	document.getElementById(radioID).checked = true;
}

function postRequest(formId) {
	var frm = document.getElementById(formId);
	frm.submit();
	startPolling(frm.getAttribute('action'), 'messageStack', 5000);
}
var counter = 0;
var poll;
function startPolling(url, containerDivId, interval) {
	poll = self.setInterval(function() {
		pollJsonService(url, containerDivId);
	}, interval);
}

function stopPolling() {
	alert('Stop polling');
	poll = self.clearInterval(poll);
	counter = 0;
}

function stopPolling(formId, containerDivId) {
	stopPolling();
	var frm = document.getElementById(formId);
	delJsonService(frm.getAttribute('action'));
	document.getElementById(containerDivId).innerHTML = '';
}

function stringify(object, padding, margin) {
	var o = (typeof object == 'object' || typeof object == 'function')
			&& object != null ? object : null;
	var p = typeof padding == 'boolean' && padding ? true : false;
	var m = typeof margin == 'number' && margin > 0 && p ? margin : 0;
	if (o != null) {
		var s = '';
		for ( var v in o) {
			s += typeof o[v] === 'object' ? (o[v] ? ((typeof o[v].length === 'number'
					&& !(o[v].propertyIsEnumerable('length')) && typeof o[v].splice === 'function') ? (m > 0 ? Array(
					m).join(' ')
					: '')
					+ v
					+ ':'
					+ (p ? ' ' : '')
					+ '['
					+ (p ? 'rn' : '')
					+ stringify(o[v], p, (m > 0 ? m : 1) + v.length + 4)
					+ (p != true ? '' : 'rn'
							+ Array((m > 0 ? m : 1) + v.length + 2).join(' '))
					+ '],' + (p ? 'rn' : '')
					: (m > 0 ? Array(m).join(' ') : '')
							+ v
							+ ':'
							+ (p ? ' ' : '')
							+ '{'
							+ (p ? 'rn' : '')
							+ stringify(o[v], p, (m > 0 ? m : 1) + v.length + 4)
							+ (p != true ? '' : 'rn'
									+ Array((m > 0 ? m : 1) + v.length + 2)
											.join(' ')) + '},'
							+ (p ? 'rn' : ''))
					: (m > 0 ? Array(m).join(' ') : '') + v + ':'
							+ (p ? ' ' : '') + o[v] + ',' + (p ? 'rn' : ''))
					: (m > 0 ? Array(m).join(' ') : '')
							+ v
							+ ':'
							+ (p ? ' ' : '')
							+ (typeof o[v] == 'string' ? '\''
									+ o[v].replace(/'/g, '\'') + '\'' : o[v])
							+ ',' + (p ? 'rn' : '');
		}
		;
		o = s.length > 0 && p != true ? s.substring(0, s.length - 1)
				: (s.length > 2 ? s.substring(0, s.length - 3) : s);
	} else {
		o = object;
	}
	;
	return o;
};