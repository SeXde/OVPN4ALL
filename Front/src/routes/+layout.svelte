<script lang='ts'>
	import '../app.css';
	import { onMount } from 'svelte';
	import Cookies from 'js-cookie';
	import { goto } from '$app/navigation';

	const API_URL: string = 'http://localhost:8082/api'

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
			goto('/sign-in')
		}
		
	}

	onMount(() => {
		let currentPath = window.location.pathname;
		console.log('klklklklk: ',currentPath)
		if (currentPath !== "/" && currentPath !== "/sign-in") {
			canIAccess()
		}
	});
	
</script>


<div class="w-full min-h-[100vh] flex flex-col bg-dark text-light">
	<slot />
</div>
