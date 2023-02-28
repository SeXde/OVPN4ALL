<script lang='ts'>
	import '../app.css';
	import { onMount } from 'svelte';
	import Cookies from 'js-cookie';
	import { goto } from '$app/navigation';
	import { fade } from 'svelte/transition';
	import { isErrorOverlayOpen } from '$lib/stores/OverlayStore';
	

	const API_URL: string = 'http://localhost:8082/api'
	let isReady = false;
	export const canIAccess = async () => {
		let token: any = Cookies.get('jwt')
		if (!token) goto('/sign-in')
		const response = await fetch(API_URL+"/users/token", {
			method: 'GET',
			mode: 'cors',
			headers: {
				Authorization: 'Bearer '+token
			}
		})

		if (response.status !== 200) {
			isErrorOverlayOpen.set(false);
			goto('/sign-in')
		}
		
	}

	onMount(() => {
		let currentPath = window.location.pathname;
		if (currentPath !== "/" && currentPath !== "/sign-in") {
			canIAccess()
		}
		isReady = true
	});
	
</script>

{#if isReady}
<div transition:fade class="w-full min-h-[100vh] flex flex-col bg-dark text-light">
		<slot />
</div>
{/if}
