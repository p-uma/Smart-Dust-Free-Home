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
        pointHoverBackgroundColor: "rgba(255, 204, 1)",
        pointHoverBorderColor: "rgba(255, 255, 0, 1)",
        data: dustData
      }
    ]
  }

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
  ws.onmessage = function (message) {
    console.log('receive message' + message.data);
    try {
      var obj = JSON.parse(message.data);
      if(!obj.time || !obj.dustDensity) {
        return;
      }
      timeData.push(obj.time);
      dustData.push(obj.dustDensity);
      // only keep no more than 50 points in the line chart
      const maxLen = 50;
      var len = timeData.length;
      if (len > maxLen) {
        timeData.shift();
        dustData.shift();
      }

      myLineChart.update();
    } catch (err) {
      console.error(err);
    }
    //ws.send("Here's some text that the server is urgently awaiting!");
  }
});