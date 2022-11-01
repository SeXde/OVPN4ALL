<script lang="ts">
	import { goto } from '$app/navigation';
	import ErrorMessage from '$lib/components/errorMessage.svelte';
	import Header from "$lib/components/header.svelte";
	import {emailValidator} from '$lib/helpers/validators';
	import { postWithJWT } from '$lib/utils/requestUtils';
	
	type RoleDTO = {
		roleName: string
	}

	let username: string, password: string, repeatPassword: string;
	let postError: string = null;
	let formError: string = null;
	let validData: boolean = false;
	let isLoading: boolean = false;
	let isAdmin: boolean = false;
	let isUser: boolean = false
	let email: string;
	let roles: Array<RoleDTO> = []
	const forbidenPasswords: Set<string> = new Set()
	forbidenPasswords.add("'")
	
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

		validData = isAdmin || isUser
		if (!validData) {
			formError = "User must have some role."
			return;
		}

		formError = null;
	}

	async function sendData() {

		isLoading = true;
		if (isAdmin) roles.push({roleName: 'ROLE_ADMIN'})
		if (isUser) roles.push({roleName: 'ROLE_USER'})
		const data: string = JSON.stringify({
			name : username,
			email,
			password,
			roles
		});

		postError = null;
		const[, error] = await postWithJWT('http://localhost:8082/api/users', 201, data)
		postError = error
		isLoading = false;
		if (postError) return;
        goto("/users");
		
	}

</script>

<svelte:head>
	<title>OVPN4ALL - Sign-Up</title>
	<meta name="description" content="OVPN4ALL landing page" />
</svelte:head>

<Header navbar={true}/>
<div class="flex flex-col items-center justify-center my-auto">
    <form on:change={checkDefault} on:submit|preventDefault={sendData} class= "px-8 pt-6 pb-8 mb-4">
		<div class="flex flex-col items-center">
			<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
				<path stroke-linecap="round" stroke-linejoin="round" d="M15 9h3.75M15 12h3.75M15 15h3.75M4.5 19.5h15a2.25 2.25 0 002.25-2.25V6.75A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25v10.5A2.25 2.25 0 004.5 19.5zm6-10.125a1.875 1.875 0 11-3.75 0 1.875 1.875 0 013.75 0zm1.294 6.336a6.721 6.721 0 01-3.17.789 6.721 6.721 0 01-3.168-.789 3.376 3.376 0 016.338 0z" />
			  </svg>
			  <h2 class="text-center text-2xl">New user</h2>
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
		<div class="flex flex-row items-center justify-center">
			<div class="relative my-3">
				<label for="admin-toggle" class="inline-flex relative items-center cursor-pointer">
					<input on:click={() => isAdmin = !isAdmin} type="checkbox" value="" id="admin-toggle" class="sr-only peer">
					<div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 dark:peer-focus:ring-blue-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600"></div>
					<span class="ml-3 text-sm font-medium text-gray-900 dark:text-gray-300">Administrator</span>
				  </label>
			</div>
			<div class="ml-5 relative my-3">
				<label for="user-toggle" class="inline-flex relative items-center cursor-pointer">
					<input on:click={() => isUser = !isUser} type="checkbox" value="" id="user-toggle" class="sr-only peer">
					<div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 dark:peer-focus:ring-blue-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600"></div>
					<span class="ml-3 text-sm font-medium text-gray-900 dark:text-gray-300">User</span>
				  </label>
			</div>
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
        {#if postError}
            <ErrorMessage title={"Server error."} body={postError} />
        {/if}
    </form>
  </div>