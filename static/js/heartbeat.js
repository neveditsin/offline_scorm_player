function heartbeat() {
	setTimeout("heartbeat()",5000);
	//console.log("heartbeat");
	const Parameters={
		method:"GET"
	};
	fetch("http://localhost:8080/lrs/hb/hb", Parameters)
	.then((out) => {
		//console.log('Output: ', out);
	}).catch(err => console.error(err));
}
heartbeat();