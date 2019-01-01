$(document).ready(function () {
	
	$('#ok-refresh').on('click',function(){
		document.location.reload();
	});
	var timeData = [], dustData = [];
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
	        pointHoverBackgroundColor: "rgba(255, 204, 1)",
	        pointHoverBorderColor: "rgba(255, 255, 0, 1)",
	        data: dustData
	      }
	    ]
	  }

	  var basicOption = {
	    title: {
	      display: true,
	      text: 'Future Dust Data on ',
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
	  
	  var maxDust=0, minDust=5000, avgDust=0, airPurifier="OFF", cnt=0, dust=0;
	  $('#max-dust').html(maxDust);
	  $('#min-dust').html("0");
	  $('#avg-dust').html(avgDust);
	  $('#air-purifier').html(airPurifier);
	  var stDate = new Date();
	$("#datepicker-group").datepicker({
	    format: "mm/dd/yyyy",
	    autoclose: true,
	    clearBtn: true,
	    startDate: '+1d'
	    
	  });
	$('#modal').modal({
		 backdrop: 'static',
		    keyboard: false
	});
	var selDate;
	$('#get-data').on('click',function(){
		var v=0;
		var date=$('#date').val();
		selDate = date;
		console.log(date);
		$('#modal').modal('hide');
		$('#pleaseWaitDialog').modal();
		$.ajax({
	        type: "GET",
	        contentType: "application/json",
	        url: "/getFutureData",
	        data: {date:date},
	        dataType: 'json',
	        cache: false,
	        timeout: 600000,
	        success: function (data) {
	        	console.log(data.length);
	        	for(var i=0;i<data.length;i++){
	        		try {
        			  var obj = data[i];
        		      if(!obj.date || !obj.density) {
        		        return;
        		      }
        		      obj.density=Number((obj.density).toFixed(2));
        		      var timeTokens = obj.date.split('T');
        		      timeTokens=timeTokens[1];
        		      timeTokens = timeTokens.split(':');
        		      //console.log(timeTokens[0]+":"+timeTokens[1]);
        		      timeData.push(timeTokens[0]+":"+timeTokens[1]);
        		      dustData.push(obj.density);
        		      
        		      if(maxDust<obj.density){
        		    	  maxDust = obj.density;
        		    	  $('#max-dust').html(maxDust);
        		      }
        		      
        		      if(minDust>obj.density){
        		    	  minDust = obj.density;
        		    	  $('#min-dust').html(minDust);
        		      }
        		      
        		      dust+=obj.density;
        		      cnt++;
        		      v=1;
        		      avgDust=dust/cnt;
        		      avgDust=Number((avgDust).toFixed(2));
        		      if(avgDust>0.20){
        		    	  airPurifier="ON"; //TODO
        		      }else{
        		    	  airPurifier="OFF";
        		      }
        		      $('#avg-dust').html(avgDust);
        		      $('#air-purifier').html(airPurifier);
        		      
        		      
        		      // only keep no more than 50 points in the line chart
        		      const maxLen = 50;
        		      var len = timeData.length;
        		      if (len > maxLen) {
        		        timeData.shift();
        		        dustData.shift();
        		      }
        		      myLineChart.options.title.text = 'Future Dust Data on ' + selDate;
        		      myLineChart.update();
        		      
        		    } catch (err) {
        		      console.error(err);
        		    }
	        	}
	        },
	        error: function (e) {
	        	console.log("Error");
	        }
	    });
		console.log(timeData.length);
		setTimeout(function(){;}, 500);
		$('#pleaseWaitDialog').modal('hide');
		
	});

  //Get the context of the canvas element we want to select
  var ctx = document.getElementById("myChart").getContext("2d");
  var optionsNoAnimation = { animation: false }
  var myLineChart = new Chart(ctx, {
    type: 'line',
    data: data,
    options: basicOption
  });
});