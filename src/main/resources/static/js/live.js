$(document).ready(function () {
  var timeData = [],
    dustData = [];
  var data = {
    labels: timeData,
    datasets: [
      {
        fill: false,
        label: 'Dust Density',
        yAxisID: 'dustData',
        borderColor: "rgba(255, 255, 255, 1)",
        pointBoarderColor: "rgba(255, 255, 255, 1)",
        backgroundColor: "rgba(255, 255, 255, 0.4)",
        pointHoverBackgroundColor: "rgba(255, 255, 255)",
        pointHoverBorderColor: "rgba(255, 255, 255, 1)",
        data: dustData
      }
    ]
  }
  var maxDust=0, minDust=5000, avgDust=0, airPurifier="OFF", cnt=0, dust=0;
  $('#max-dust').html(maxDust);
  $('#min-dust').html("0");
  $('#avg-dust').html(avgDust);
  $('#air-purifier').html(airPurifier);

  var basicOption = {
    title: {
      display: true,
      text: 'Real-time Dust Data',
      fontSize: 20
    },
    scales: {
      yAxes: [{
        id: 'dustData',
        type: 'linear',
        scaleLabel: {
          labelString: 'Dust Density(C)',
          display: true
        },
        position: 'left',
      }]
    }
  }

  //Get the context of the canvas element we want to select
  var ctx = document.getElementById("myChart").getContext("2d");
  var optionsNoAnimation = { animation: false }
  var myLineChart = new Chart(ctx, {
    type: 'line',
    data: data,
    options: basicOption
  });

  var ws = new WebSocket('ws://' + location.host+'/live');
  ws.onopen = function () {
    console.log('Successfully connect WebSocket');
  }
  var selDate=new Date();
  var xx = String(selDate).split('T');
  selDate = xx[0];
  ws.onmessage = function (message) {
    console.log('receive message' + message.data);
    try {
      var obj = JSON.parse(message.data);
      if(!obj.date || !obj.dustDensity) {
        return;
      }
      console.log(message.data);
      obj.dustDensity=Number((obj.dustDensity).toFixed(2));
      console.log(obj.date);
      timeData.push(obj.date);
      dustData.push(obj.dustDensity);
      if(maxDust<obj.dustDensity){
    	  maxDust = obj.dustDensity;
    	  $('#max-dust').html(maxDust);
      }
      
      if(minDust>obj.dustDensity){
    	  minDust = obj.dustDensity;
    	  $('#min-dust').html(minDust);
      }
      
      dust+=obj.dustDensity;
      cnt++;
      avgDust=dust/cnt;
      avgDust=Number((avgDust).toFixed(2));
      if(avgDust>0.22){
    	  if(airPurifier=="OFF"){
    		  $('#air-progress').removeClass('bg-red');
        	  $('#air-progress').addClass('bg-green');
        	  $.ajax({
      	        type: "GET",
      	        contentType: "application/json",
      	        url: "/notification",
      	        data: {type:"1"},
      	        dataType: 'json',
      	        cache: false,
      	        timeout: 600000,
      	        success: function (data) {
      	        	
      	        },error: function (e) {
    	        	console.log("Error");
    	        }
    	     });
        	  airPurifier="ON";// add notifications
    	  }
      }else{
    	  if(airPurifier=="ON"){
    		  $('#air-progress').removeClass('bg-green');
        	  $('#air-progress').addClass('bg-red');
        	  
        	  $.ajax({
        	        type: "GET",
        	        contentType: "application/json",
        	        url: "/notification",
        	        data: {type:"2"},
        	        dataType: 'json',
        	        cache: false,
        	        timeout: 600000,
        	        success: function (data) {
        	        	
        	        },error: function (e) {
        	        	console.log("Error");
        	        }
          	 });
        	  airPurifier="OFF";
    	  }
      }
      
      $('#avg-dust').html(avgDust);
      $('#air-purifier').html(airPurifier);
      
      console.log(avgDust);
      console.log(airPurifier);
      console.log(minDust);
      console.log(maxDust);
     
      // only keep no more than 50 points in the line chart
      const maxLen = 50;
      var len = timeData.length;
      if (len > maxLen) {
        timeData.shift();
        dustData.shift();
      }
      myLineChart.options.title.text = 'Real Time Dust Data';
      myLineChart.update();
    } catch (err) {
      console.error(err);
    }
    //ws.send("Here's some text that the server is urgently awaiting!");
  }
});