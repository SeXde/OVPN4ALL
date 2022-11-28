<script type="ts">
    import chartjs from 'chart.js';
	import { onMount } from 'svelte';
    export let active: boolean;

	let chartValues: Array<number> = [4, 23, 3, 1, 3, 4, 10, 15, 3, 27, 33];
	let ctx;
	let chartCanvas;
    let chart;

	onMount(() => {
		  ctx = chartCanvas.getContext('2d');
            chart = new chartjs(ctx, {
                type: 'line',
                data: {
                labels: [],
                datasets: [{
                    label: 'Speed(MB)',
                    backgroundColor: '#cd35eb',
                    data: chartValues,
                    borderWidth: 1
                }]
                },
                options: {
                scales: {
                    xAxes: [],
                    yAxes: [{
                    ticks: {
                        beginAtZero:true
                    }
                    }]
                }
                }
            });

	});

    const updateChart = async (): Promise<void> => {
        chart.data.datasets.at(0).data.shift();
        chart.data.datasets.at(0).data.push(Math.random() * 30);
        console.log(chart.data.datasets.at(0));
    }

    onMount(() => {
        if (active) {
        updateChart();
        setInterval(() => {
            updateChart();
        }, 5000);
     }
    })


  </script>


<canvas bind:this={chartCanvas} id="myChart"></canvas>
