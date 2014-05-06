
jQuery(function($) {

//$(document).ready(function(){
   resize();
   
   $("#jogger")
   	.mousedown(jogStart)
   	.mousemove(jogChange)
   	.mouseup(jogStop)
   	.mouseleave(jogStop);
});

$(window).resize(function(){
   resize();
});

// Ensure that the page is laid out correctly
function resize() {
    var diameter = window.innerWidth * 0.8;
    if (window.innerHeight * 0.8 < diameter) {
        diameter = window.innerHeight * 0.8;
    }
    if (diameter < 250) {
        diameter = 250;
    }
	
	var canvas = $("#jogger")[0];
	canvas.width = canvas.height = diameter;
	drawJogger(); 
}

function drawJogger() {
	var canvas = $("#jogger")[0];
	canvas.width = canvas.width;

	var ctx = canvas.getContext('2d');
	var canvas_size = [canvas.width, canvas.height];
    var radius = Math.min(canvas_size[0], canvas_size[1]) / 2;
    var center = [canvas_size[0]/2, canvas_size[1]/2];
    
    // Draw the axes
	ctx.beginPath();
    ctx.moveTo(center[0]-radius, center[1]);
    ctx.lineTo(center[0]+radius, center[1]);
    ctx.moveTo(center[0], center[1]-radius);
    ctx.lineTo(center[0], center[1]+radius);
    ctx.moveTo(center[0]-radius, center[1]);
    ctx.arc(center[0], center[1], radius, 0, 2*3.1415);
    ctx.strokeStyle = 'rgb(20,20,220)';
    ctx.stroke();
    
    // Draw the current jog speed indicator 
    if (jogInterval) {
    	var jx = center[0]+jogVector.x*radius;
    	var jy = center[1]+jogVector.y*radius;

    	ctx.beginPath();
        ctx.moveTo(jx-radius/10, jy);
        ctx.arc(jx, jy, radius/10, 0, 2*3.1415);
        ctx.fillStyle = "rgba(200,0,0,0.5)";    	
        ctx.fill();    	
    }    
}

function eventToVector(e) {
	var jogger = $("#jogger");
	var canvas = jogger[0];
	var ctx = canvas.getContext('2d');
	var canvas_size = [canvas.width, canvas.height];
    var radius = Math.min(canvas_size[0], canvas_size[1]) / 2;
    var center = [canvas_size[0]/2, canvas_size[1]/2];
    
    var res = new Object();
    res.x = ((e.pageX-jogger.offset().left)-center[0]) / radius;
    res.y = -(center[1]-(e.pageY-jogger.offset().top)) / radius;
    
    return res;   
}

var JOG_INTERVAL = 50;
var jogInterval;
var jogVector;

// Called on #jogger.mousedown
function jogStart(e){
	jogVector = eventToVector(e);
	if (!jogInterval) {
		jogInterval = setInterval(sendJogCommand, JOG_INTERVAL);
		drawJogger();
	}
}

// Called on #jogger.mousemove
function jogChange(e){
	if (jogInterval) {
		jogVector = eventToVector(e);
		drawJogger();
	}
}

//Called on #jogger.mouseup
function jogStop(e){	
	if (jogInterval != null) {
		clearInterval(jogInterval);
		jogInterval = null;
	}
	if (jogVector != null) {	
		jogVector.x = 0;
		jogVector.y = 0;
		jogVector.z = 0;
		jogVector.a = 0;
		sendJogCommand();
	}
	drawJogger();
}

// Called every 100 ms to tell the server to jog in the direction of the jogVector
function sendJogCommand() {
	$.ajax({
		url: "/api/jog/jog",
		
		data: JSON.stringify({
			//x: jogVector.x,
			//y: jogVector.y,
			z: -jogVector.y,
			//a: jogVector.y,
		}, null, "\t"),
		
		success: function(json) {
			// TODO: Maybe we should provide some sort indication that it worked, perhaps read out the current speed and position.
			/*
			if (console) {
				console.log("jogged: ", json);
			}
			*/
		},
		
		type: "POST",
		dataType: "json",
		contentType: "application/json; charset=utf-8",		
	});	
}
