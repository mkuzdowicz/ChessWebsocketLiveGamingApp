/**
 * bestPlayers site js
 */
var TITLE = "number of won chess games";

var CHART_TYPE = "column";

var DATA_INPUT = {
	title : {
		text : TITLE
	},
	data : [ {
		type : CHART_TYPE,
		dataPoints : []
	} ]
}

populateChartWithUsersData();

function populateChartWithUsersData() {
	for (var i = 0; i < USERS_JSON_ARR.length; i++) {
		var userData = {
			label : USERS_JSON_ARR[i].username,
			y : USERS_JSON_ARR[i].numberOfWonChessGames
		}
		DATA_INPUT.data[0].dataPoints.push(userData);
	}
}

$(document).ready(function() {
	var chart = new CanvasJS.Chart("bestChessGamersChart", DATA_INPUT);

	chart.render();
});