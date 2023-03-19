<script lang="ts">
	import Header from '$lib/components/header.svelte';
	import { PUBLIC_SERVER_URL } from '$env/static/public';
	import ErrorMessage from '$lib/components/errorMessage.svelte';
	import {emailValidator} from '$lib/helpers/validators';
	import { goto } from '$app/navigation';
	import { isErrorOverlayOpen } from '$lib/stores/OverlayStore';
	import ErrorOverlay from '$lib/components/ErrorOverlay.svelte';

	export let data;
	let [error, firstUser] = data.firstUser;
	let username: string;
	let password: string;
	let repeatPassword: string;
	let email: string;
	let formError: string;
	let validData: boolean;
	let isLoading: boolean;
	const forbidenPasswords: Set<string> = new Set()
	forbidenPasswords.add("'")
	let roles = [];
	roles.push({'roleName': 'ROLE_ADMIN'});
	const checkDefault =  () => {

		validData = username != null && username.length != 0;
		if (!validData) {
			formError = "Username cannot be empty.";
			return;
		}

		validData = email != null && email.length != 0 && emailValidator(email);
		if (!validData) {
			formError = "Email is not valid.";
			return;
		}

		validData = password != null && password.length != 0 ;
		if (!validData) {
			formError = "Password cannot be empty.";
			return;
		}

		validData = !forbidenPasswords.has(password);
		if (!validData) {
			formError = "Password cannot contain character: \"'\"";
			return;
		}

		validData = password === repeatPassword;
		if (!validData) {
			formError = "Passwords do not match."
			return;
		}

		formError = null;
	}

	const saveFirstUser = async () => {
		isLoading = true;
		const data: string = JSON.stringify({
			name : username,
			email,
			password,
			roles
		});
		await fetch(`${PUBLIC_SERVER_URL}/api/users/firstUser`, {
			method: 'POST',
			headers: { 
				'Content-Type': 'application/json'
			},
			body: data
		}).then(res => {
			if (!res.ok) {
				return res.json();
			} else {
				return null;
			}
		}).then(res => {
			if (res) {
				error = res.message;
			}
		}).catch(() => {
			error = "Cannot contact with server";
		});
		isLoading = false;
		if (!error) {
			goto("/sign-in");
		} else {
			isErrorOverlayOpen.set(true);
		}
	}
	console.log("firstUser: ", firstUser);

</script>

<svelte:head>
	<title>OVPN4ALL - Welcome</title>
	<meta name="description" content="OVPN4ALL landing page" />
</svelte:head>

<Header navbar={false}/>
{#if $isErrorOverlayOpen}
		<ErrorOverlay errorTitle="Error saving user" errorMessage={error}/>
{/if}
<div class="flex flex-col justify-center items-center my-auto">
	<div class="flex flex-col justify-center items-center w-80 rounded-lg border-x-2 border-light px-5 py-5 hover:border-primary hover:border-x-4">
		<p>OVPN4ALL allows you to create a VPN server using the <i>OpenVPN</i> implementation. It's completely free and easy to use.</p>
		{#if !firstUser}
			<a href="/sign-in" class="mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
				Get started!
			</a>
		{/if}
	</div>
	{#if firstUser}
	<div class="flex flex-col items-center justify-center my-auto">
		<form on:change={checkDefault} on:submit|preventDefault={saveFirstUser} class= "px-8 pt-6 pb-8 mb-4">
			<div class="flex flex-col items-center">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M15 9h3.75M15 12h3.75M15 15h3.75M4.5 19.5h15a2.25 2.25 0 002.25-2.25V6.75A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25v10.5A2.25 2.25 0 004.5 19.5zm6-10.125a1.875 1.875 0 11-3.75 0 1.875 1.875 0 013.75 0zm1.294 6.336a6.721 6.721 0 01-3.17.789 6.721 6.721 0 01-3.168-.789 3.376 3.376 0 016.338 0z" />
				  </svg>
				  <h2 class="text-center text-2xl">First user</h2>
			</div>	  
			<div class="relative my-3">
				<input required bind:value={username} type="text" id="username" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
				<label for="username" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Username</label>
			</div>
			<div class="relative my-3">
				<input required bind:value={email} type="text" id="email" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
				<label for="email" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Email</label>
			</div>	  
			<div class="relative my-3">
				<input required bind:value={password} type="password" id="password" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
				<label for="password" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Password</label>
			</div>	  
			<div class="relative my-3">
				<input required bind:value={repeatPassword} type="password" id="repeatPassword" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
				<label for="repeatPassword" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Repeat password</label>
			</div>
			{#if formError}
				<ErrorMessage title={"Form error"} body={formError} />
			{/if}
			<div class="flex flex-row">
				{#if validData}
					<button type="submit" class="my-3 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
						{#if isLoading}
							<div class="border border-t-4 border-b-2 border-primary rounded-full  animate-spin">
								<div class="p-2"></div>
							</div>
						{:else}
							Add
						{/if}
					</button>
				{:else}
					<button disabled type="submit" class="my-3 disabled:opacity-25 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">Add</button>
				{/if}
					<a class="ml-3 my-3 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors" href="/users">
						Cancel
					</a>
			</div>
		</form>
	  </div>
	{/if}
</div>

