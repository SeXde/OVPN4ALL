<script lang="ts">
	import Header from "$lib/components/header.svelte";
    import ErrorMessage from "$lib/components/errorMessage.svelte";
	import { deleteWithJWT } from "$lib/utils/requestUtils";
    import { fly } from 'svelte/transition';

    export let data;
    let searchedUser: string = "";
    let filteredUsers = [];
    let [users, error] = data.users;
    let isOrderByName: boolean, isOrderByDate: boolean, isOrderByMail: boolean;
    let isDeleteError: boolean = false;
    let deleteError: string = null;
    isOrderByName = isOrderByDate = isOrderByMail = false;
    $: {
        if (searchedUser) {
        filteredUsers = users.filter(user => user.name.toLowerCase().includes(searchedUser.toLocaleLowerCase()));
        isOrderByName = isOrderByDate = false;
        } else {
            filteredUsers = [... users];
        }
    }

    const orderByName = () => {
        isOrderByName = !isOrderByName;
        if (isOrderByName) {
            filteredUsers = filteredUsers.sort((a, b) => a.name.localeCompare(b.name));
        } else {
            filteredUsers = filteredUsers.sort((a, b) => b.name.localeCompare(a.name));
        }
    }

    const orderByMail = () => {
        isOrderByMail = !isOrderByMail;
        if (isOrderByMail) {
            filteredUsers = filteredUsers.sort((a, b) => a.email.localeCompare(b.email));
        } else {
            filteredUsers = filteredUsers.sort((a, b) => b.email.localeCompare(a.email));
        }
    }

    const orderByDate = () => {
        isOrderByDate = !isOrderByDate;
        if (isOrderByDate) {
            filteredUsers = filteredUsers.sort((a, b) => a.createdAt.localeCompare(b.createdAt));
        } else {
            filteredUsers = filteredUsers.sort((a, b) => b.createdAt.localeCompare(a.createdAt));
        }
    }

    const deleteUser = async (userId: number): Promise<void> => {
        const [{}, error] = await deleteWithJWT('http://localhost:8082/api/users/' + userId, 200)
        if (error) deleteError = error.message
        console.log("Error = ", error)
        isDeleteError = deleteError !== null
        if (!isDeleteError) {
            users = users.filter(user => user.id != userId)
            filteredUsers = filteredUsers.filter(user => user.id != userId)
        }
    }

</script>

<svelte:head>
	<title>OVPN4ALL - Users</title>
	<meta name="description" content="OVPN4ALL setup page" />
</svelte:head>

<Header navbar={true}/>
<div class="overflow-x-auto relative shadow-md sm:rounded-lg my-5">
    <div class="flex justify-between items-center pb-4 bg-transparent">
        <label for="table-search" class="sr-only">Search</label>
        <div class="relative ml-5">
            <div class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
                <svg class="w-5 h-5 text-gray-500 dark:text-gray-400" aria-hidden="true" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"></path></svg>
            </div>
            <input bind:value={searchedUser} type="text" id="table-search-users" class="block p-2 pl-10 w-80 text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-secondary dark:focus:border-secondary" placeholder="Search for users">
        </div>
        {#if isDeleteError}
            <ErrorMessage  title="Delete error" body={deleteError}/>
        {/if}
        <a href="/sign-up" class="mr-5 my-3 mt-5 w-36 py-2 flex flex-col items-center justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 7.5v3m0 0v3m0-3h3m-3 0h-3m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.766z" />
              </svg>              
            Add user  
        </a>
    </div>
    <table class="w-full text-sm text-left text-gray-500 dark:text-gray-400 px-5">
        <thead class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
            <tr>
                <th scope="col" class="p-4">
                </th>
                <th scope="col" class="py-3 px-6">
                    <div on:click={orderByName} class="flex flex-col items-center hover:underline hover:text-secondary hover:cursor-pointer">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M15 9h3.75M15 12h3.75M15 15h3.75M4.5 19.5h15a2.25 2.25 0 002.25-2.25V6.75A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25v10.5A2.25 2.25 0 004.5 19.5zm6-10.125a1.875 1.875 0 11-3.75 0 1.875 1.875 0 013.75 0zm1.294 6.336a6.721 6.721 0 01-3.17.789 6.721 6.721 0 01-3.168-.789 3.376 3.376 0 016.338 0z" />
                        </svg>                          
                        Name
                    </div>
                </th>
                <th scope="col" class="py-3 px-6">
                    <div on:click={orderByMail} class="flex flex-col items-center hover:underline hover:text-secondary hover:cursor-pointer">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                            <path stroke-linecap="round" d="M16.5 12a4.5 4.5 0 11-9 0 4.5 4.5 0 019 0zm0 0c0 1.657 1.007 3 2.25 3S21 13.657 21 12a9 9 0 10-2.636 6.364M16.5 12V8.25" />
                          </svg>                                                 
                        Email
                    </div>
                </th>
                <th scope="col" class="py-3 px-6 flex justify-center">
                    <div on:click={orderByDate} class="flex flex-col items-center hover:underline hover:text-secondary hover:cursor-pointer">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 012.25-2.25h13.5A2.25 2.25 0 0121 7.5v11.25m-18 0A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75m-18 0v-7.5A2.25 2.25 0 015.25 9h13.5A2.25 2.25 0 0121 11.25v7.5m-9-6h.008v.008H12v-.008zM12 15h.008v.008H12V15zm0 2.25h.008v.008H12v-.008zM9.75 15h.008v.008H9.75V15zm0 2.25h.008v.008H9.75v-.008zM7.5 15h.008v.008H7.5V15zm0 2.25h.008v.008H7.5v-.008zm6.75-4.5h.008v.008h-.008v-.008zm0 2.25h.008v.008h-.008V15zm0 2.25h.008v.008h-.008v-.008zm2.25-4.5h.008v.008H16.5v-.008zm0 2.25h.008v.008H16.5V15z" />
                        </svg>                   
                        Creation time
                    </div>
                </th>
                <th scope="col" class="py-3 px-6">
                    <div class="flex flex-col items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
                        </svg>                      
                        Role
                    </div>
                </th>
                <th scope="col" class="py-3 px-6">
                    <div class="flex flex-col items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 13.5l10.5-11.25L12 10.5h8.25L9.75 21.75 12 13.5H3.75z" />
                          </svg>                  
                        Action
                    </div>
                </th>
            </tr>
        </thead>
        <tbody>
            {#each filteredUsers as user}
            <tr class="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
                <td class="p-4 w-4">
                </td>
                <th scope="row" class="flex flex-col items-center py-4 px-6 text-gray-900 whitespace-nowrap dark:text-white">
                    <p class="text-base font-semibold">{user.name}</p>
                </th>
                <td class="py-4 px-6 text-center">
                    {user.email}
                </td>
                <td class="py-4 px-6 text-center">
                    {user.createdAt}
                </td>
                <td class="py-4 px-6 text-center">
                    {#if user.admin}
                        Administrator
                    {:else}
                        VPN user
                    {/if}
                </td>
                <td class="py-4 px-6">
                    <div class="flex justify-center">
                        <div class="flex flex-col items-center mr-4 hover:underline hover:text-secondary hover:cursor-pointer">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 12L12 16.5m0 0L7.5 12m4.5 4.5V3" />
                            </svg>
                            Download ovpn
                        </div>
                        <div on:click={() => deleteUser(user.id)} class="flex flex-col items-center ml-4 text-red-500 hover:underline hover:text-secondary hover:cursor-pointer">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M22 10.5h-6m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.766z" />
                            </svg>
                            Delete
                        </div>
                    </div>
                </td>
            </tr>
            {/each}
        </tbody>
    </table>
</div>
