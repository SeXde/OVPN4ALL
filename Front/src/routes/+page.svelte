<script>
	import Header from '$lib/components/header.svelte';
</script>

<svelte:head>
	<title>OVPN4ALL - Welcome</title>
	<meta name="description" content="OVPN4ALL landing page" />
</svelte:head>

<Header navbar={false}/>
<div class="my-auto mx-auto w-80 justify-center rounded-lg border-x-2 border-light px-5 py-5 hover:border-primary hover:border-x-4">
	<p>OVPN4ALL allows you to create a VPN server using the <i>OpenVPN</i> implementation. It's completely free and easy to use.</p>
	<a href="/sign-in" class="mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
		Get started!
	</a>
</div>
