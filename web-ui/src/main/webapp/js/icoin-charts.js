$(document).ready(function () {
    $.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=aapl-ohlcv.json&callback=?', function (data) {

        // split the data set into ohlc and volume
        var ohlc = [],
            volume = [],
            dataLength = data.length;

        for (i = 0; i < dataLength; i++) {
            ohlc.push([
                data[i][0], // the date
                data[i][1], // open
                data[i][2], // high
                data[i][3], // low
                data[i][4] // close
            ]);

            volume.push([
                data[i][0], // the date
                data[i][5] // the volume
            ])
        }

// set the allowed units for data grouping
        var groupingUnits = [
            [
                'week',                         // unit name
                [1]                             // allowed multiples
            ],
            [
                'month',
                [1, 2, 3, 4, 6]
            ]
        ];

// create the chart
        $('#charts').highcharts('StockChart', {

            rangeSelector: {
                selected: 1
            },

            title: {
                text: 'AAPL Historical'
            },

            yAxis: [
                {
                    title: {
                        text: 'OHLC'
                    },
                    height: 200,
                    lineWidth: 2
                },
                {
                    title: {
                        text: 'Volume'
                    },
                    top: 300,
                    height: 100,
                    offset: 0,
                    lineWidth: 2
                }
            ],

            plotOptions: {
                candlestick: {
                    color: '#FF0000',
                    upColor: '#00FF00'
                }
            },

            series: [
                {
                    type: 'candlestick',
                    name: 'AAPL',
                    data: ohlc,
                    dataGrouping: {
                        units: groupingUnits
                    }
                },
                {
                    type: 'column',
                    name: 'Volume',
                    data: volume,
                    yAxis: 1,
                    dataGrouping: {
                        units: groupingUnits
                    }
                }
            ]
        });
    });
});