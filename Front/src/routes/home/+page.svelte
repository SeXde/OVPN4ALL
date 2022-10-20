<script lang="ts">
	import Header from "$lib/components/header.svelte";
	import Chart from '$lib/components/Chart.svelte';

	export let data
	let connected: boolean = true;
	let users: number = 5;
	
	let port: string = "---";
	let gateway: string = "---";
	let subnet: string = "---";
	let wanIp: string = "---";

	connected = data.setup != null

	if (data.setup != null) {
		port = data.setup.port;
		gateway = data.setup.gateway;
		subnet = data.setup.subnet;
		wanIp = "200.1.2.2";
	}
	

</script>

<svelte:head>
	<title>OVPN4ALL - Home</title>
	<meta name="description" content="OVPN4ALL setup page" />
</svelte:head>

<Header navbar={true}/>
	<div class="flex flex-col items-center my-auto mr-5">
		<div class="bg-light_dark px-5 py-5 border rounded-lg">
			<div class="flex flex-col items-center mb-2 pb-2 border-b">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" />
				  </svg>				  
				<p>Info</p>	
			</div>
			<p>			  
				VPN current state: 
				{#if connected}
					<span class="text-green-500">Connected</span>
				{:else}
					<span class="text-red-500">Disconnected</span>
				{/if}
			</p>
			{#if connected}
				<p>Users connected: <strong><i>{users}</i></strong></p>
			{/if}
		</div>
		<div class="bg-light_dark px-5 py-5 border rounded-lg mt-5">
			<div class="flex flex-col items-center mb-2 pb-2 border-b">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 11-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 004.486-6.336l-3.276 3.277a3.004 3.004 0 01-2.25-2.25l3.276-3.276a4.5 4.5 0 00-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008z" />
				</svg>
				<p>Settings</p>	
			</div>
			<p>					
				VPN port: <strong><i>{port}</i></strong>
			</p>
			<p>
				Gateway: <strong><i>{gateway}</i></strong>
			</p>
			<p>
				Subnet: <strong><i>{subnet}</i></strong>
			</p>
			<p>
				WAN ip: <strong><i>{wanIp}</i></strong>
			</p>
		</div>
		<div class="bg-light_dark px-5 py-5 border rounded-lg mt-5">
			<div class="flex flex-col items-center mb-2 pb-2 border-b">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M3.75 3v11.25A2.25 2.25 0 006 16.5h2.25M3.75 3h-1.5m1.5 0h16.5m0 0h1.5m-1.5 0v11.25A2.25 2.25 0 0118 16.5h-2.25m-7.5 0h7.5m-7.5 0l-1 3m8.5-3l1 3m0 0l.5 1.5m-.5-1.5h-9.5m0 0l-.5 1.5m.75-9l3-3 2.148 2.148A12.061 12.061 0 0116.5 7.605" />
				  </svg>
				<p>Usage</p>	
			</div>
			<Chart />
		</div>
		<div class="flex items-center align-middle mt-2">
			<button on:click={() => connected = !connected} type="submit" class="mr-3 my-3 mt-5 w-36 py-2 flex flex-col items-center justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
				{#if !connected}
					<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
						<path stroke-linecap="round" stroke-linejoin="round" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
						<path stroke-linecap="round" stroke-linejoin="round" d="M15.91 11.672a.375.375 0 010 .656l-5.603 3.113a.375.375 0 01-.557-.328V8.887c0-.286.307-.466.557-.327l5.603 3.112z" />
					  </svg>
					Turn on
				{:else}
					<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
						<path stroke-linecap="round" stroke-linejoin="round" d="M14.25 9v6m-4.5 0V9M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
					</svg>
					Shutdown
				{/if}
			</button>
			<button type="submit" class="my-3 mt-5 w-36 py-2 flex flex-col items-center justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 12L12 16.5m0 0L7.5 12m4.5 4.5V3" />
				</svg>
				Download logs  
			</button>
		</div>
	</div>