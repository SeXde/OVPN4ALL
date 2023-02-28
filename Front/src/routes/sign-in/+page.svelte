<script lang="ts">
	import { goto } from '$app/navigation';
	import ErrorOverlay from '$lib/components/ErrorOverlay.svelte';
	import Header from "$lib/components/header.svelte";
	import { isErrorOverlayOpen } from '$lib/stores/OverlayStore';
	import { logAndSetToken } from '$lib/utils/requestUtils';

	let username: string, password: string;
	let postError: string | null = null;
	let validData: boolean = false;
	let isLoading: boolean = false;
	
	const checkDefault =  () => {
		validData = username != null && password != null && username.length != 0 && password.length != 0;
	}

	async function sendData() {

		postError = null;
		isLoading = true;
		const data: string = JSON.stringify({
        'name' : username,
        'password' : password,
    });
    
		isLoading = true;
		postError = await logAndSetToken(username, password);
		isLoading = false;
		if (postError) {
			isErrorOverlayOpen.set(true);
			return;
			goto("/home");
		}
		goto("/home");
	}

</script>

<svelte:head>
	<title>OVPN4ALL - Sign-In</title>
	<meta name="description" content="OVPN4ALL landing page" />
</svelte:head>

<Header navbar={false}/>
{#if $isErrorOverlayOpen}
	<ErrorOverlay errorTitle="Sign in error" errorMessage={postError}/>
{/if}
<div class="mx-auto my-auto w-full max-w-xs">
    <form on:change={checkDefault} on:submit|preventDefault={sendData} class= "px-8 pt-6 pb-8 mb-4">
        <div class="flex flex-col items-center">
			<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
				<path stroke-linecap="round" stroke-linejoin="round" d="M15.75 5.25a3 3 0 013 3m3 0a6 6 0 01-7.029 5.912c-.563-.097-1.159.026-1.563.43L10.5 17.25H8.25v2.25H6v2.25H2.25v-2.818c0-.597.237-1.17.659-1.591l6.499-6.499c.404-.404.527-1 .43-1.563A6 6 0 1121.75 8.25z" />
			</svg>
			<h2 class="text-center text-2xl"> Sign in</h2>
		</div>
        <div class="relative my-3">
            <input required bind:value={username} type="text" id="username" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
            <label for="username" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Username</label>
        </div>
        <div class="relative my-3">
            <input required bind:value={password} type="password" id="password" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
            <label for="password" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Password</label>
        </div>
        {#if validData}
            <button type="submit" class="my-3 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
                {#if isLoading}
                    <div class="border border-t-4 border-b-2 border-primary rounded-full  animate-spin">
                        <div class="p-2"></div>
                    </div>
	            {:else}
					Sign in
	            {/if}
            </button>
        {:else}
            <button disabled type="submit" class="my-3 disabled:opacity-25 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">Sign in</button>
        {/if}
    </form>
  </div>