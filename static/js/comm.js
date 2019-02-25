function startHeartbeat() {
	if (typeof(Worker) !== "undefined") {
	if (typeof(w) == "undefined") {
	  w = new Worker("js/heartbeat.js");
	}
	w.onmessage = function(event) {
	  document.getElementById("result").innerHTML = event.data;
	};
	} else {
		
		const Parameters={
		method:"GET"
		};
		fetch("http://localhost:8080/lrs/hb/hboff", Parameters)
		.then((out) => {
			//console.log('Output: ', out);
		}).catch(
		err => {
			startHeartbeat();
			console.error(err);
		}
		);
		alert("You browser does not support workers. Please shut down server manually (console screen) when you finish");
	}

}
startHeartbeat();

