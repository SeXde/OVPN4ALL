<script lang='ts'>
	import { goto } from '$app/navigation';
    import Cookies from 'js-cookie';
    import { onMount } from 'svelte';
    export let navbar: boolean;

    const actions = {
        '/home' : () => document.getElementById('home-button').classList.add('text-secondary'),
        '/users' : () => document.getElementById('users-button').classList.add('text-secondary'),
        '/setup' : () => document.getElementById('setup-button').classList.add('text-secondary')
    }
    
    const logout  = () => {
        Cookies.remove('jwt')
        goto('/sign-in')
    }
    onMount(() => {  
        if (navbar){
            let action = window.location.pathname
            if (action === '/sign-up') action = '/users'
            actions[action]()
        } 
	});
    
</script>

<div class="flex flex-row h-[30] px-5 py-5 bg-light_dark border-b border-b-light">
    <h1 class="text-left bg-clip-text text-transparent bg-gradient-to-br from-secondary to-primary  md:text-6xl mr-5">OVPN4ALL</h1>
    {#if navbar}
    <div class="flex justify-center items-center w-full">
        <a class="mr-10 hover:underline hover:text-secondary flex flex-col items-center" id="home-button" href="/home">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 12l8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25" />
            </svg>		  
            Home
        </a>
        <a class="mr-10 hover:underline hover:text-secondary flex flex-col items-center" id="users-button" href="/users">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z" />
            </svg> 
            Users
        </a>
        <a class="mr-10 hover:underline hover:text-secondary flex flex-col items-center" id="setup-button" href="/setup">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 11-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 004.486-6.336l-3.276 3.277a3.004 3.004 0 01-2.25-2.25l3.276-3.276a4.5 4.5 0 00-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008z" />
            </svg>              
            Settings
        </a>
        <button on:click={logout} class="hover:underline hover:text-secondary flex flex-col items-center" id="logout-button">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15M12 9l-3 3m0 0l3 3m-3-3h12.75" />
              </svg>
            Logout
        </button>
    </div>
    {/if}
</div>
