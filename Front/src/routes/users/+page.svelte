<script lang="ts">
	import Header from "$lib/components/header.svelte";
	import { deleteWithJWT, getWithJWT } from "$lib/utils/requestUtils";
	import { goto } from "$app/navigation";
    import { saveAs } from 'file-saver';
    import Cookies from 'js-cookie';
	import Spinner from "$lib/components/Spinner.svelte";
    import { isErrorOverlayOpen, isInfoOverlayOpen, isModalOverlayOpen } from "$lib/stores/OverlayStore";
	import ErrorOverlay from "$lib/components/ErrorOverlay.svelte";
	import InfoOverlay from "$lib/components/InfoOverlay.svelte";
	import ModalOverlay from "$lib/components/ModalOverlay.svelte";

    interface Role {
        roleName: string;
    }

    interface User {
        id: number;
        name: string;
        email: string;
        roles: Array<Role>;
        createdAt: string;
    }

    interface UsersPage {
        users: Array<User>;
        currentUsers: number;
        currentPage: number;
        totalUsers: number;
        totalPages: number;
    }

    const transFormUsers = (users: Array<any>):Array<any> => {
        return users.map(user => {
            let roles = user.roles.map(role => {
                let lowerRole = role.roleName.replace('ROLE_', '').toLowerCase()
                return lowerRole.substring(0, 1).toUpperCase() + lowerRole.substring(1)
            }).join(', ')
            return {
                name : user.name,
                email : user.email,
                id : user.id,
                createdAt : user.createdAt,
                roles,
                isUser : roles.includes('User')
            }
        })
    }

    const generatePages =  (): void => {
        const offset: number = 3;
        let currPage: number = usersPage.currentPage + 1;
        let rightNum: number =  currPage + offset;
        leftPages = [];
        rightPages = [];
        if (currPage > offset) {
            for (let i = currPage - offset; i < currPage; i++) leftPages.push(i);
        } else if (currPage - 1 === 1){
            leftPages = [1];
        } else {
            for (let i = 1; i < currPage; i++) leftPages.push(i);
        }
        if (rightNum > usersPage.totalPages && currPage + 1 !== usersPage.totalPages) {
            for (let i = currPage + 1; i <= usersPage.totalPages; i++) rightPages.push(i);
        } else if(rightNum > usersPage.totalPages){
            rightPages = [currPage + 1];
        } else {
            for (let i = currPage + 1; i <= rightNum; i++) rightPages.push(i);
        }
    }

    export let data
    let searchedUser: string = ""
    let values : Array<number> = [5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140];
    let filteredUsers = []
    let usersPage: UsersPage;
    let errorBody: any;
    let defaultLimit = 10;
    let limit: number = defaultLimit;
    [usersPage, errorBody] = data.users;
    let error: string;
    let errorTitle, infoTitle, infoMessage: string;
    let modalAction;
    let modalContent;
    let modalParams;
    if (errorBody != null) {
        error = errorBody.message;
        if (error.includes("token")) {
            goto("/sign-in");
        }
        errorTitle = "Cannot get users";
        isErrorOverlayOpen.set(true);
    }
    let users = usersPage.users
    let isOrderByName: boolean, isOrderByDate: boolean, isOrderByMail: boolean, noUsers: boolean, noPrev
    isOrderByName = isOrderByDate = isOrderByMail = false;
    noPrev = true
    noUsers = users === null || users.length < 10
    users = [... transFormUsers(users)]
    let leftPages: Array<number>;
    let rightPages: Array<number>;
    let loading: boolean = false;
    generatePages();

    $: {
        if (searchedUser) {
        filteredUsers = users.filter(user => user.name.toLowerCase().includes(searchedUser.toLocaleLowerCase()))
        isOrderByName = isOrderByDate = false
        } else {
            filteredUsers = [... users]
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
        loading = true;
        let usersPage;
        await fetch('http://localhost:8082/api/users/' + userId, {
            method: 'DELETE',
                mode: 'cors',
                headers: {
                    Authorization: 'Bearer '+Cookies.get('jwt')
                }

        }).then(async res => {
            if (res.ok) {
                usersPage = await res.json();
            } else {
                return res.json();
            }
        }).then(res => {
            if (res) {
                error = res.message;
            }
        }).catch( () => {
            error = "Cannot contact with server";
        });

        if (!error) {
            users = [... transFormUsers(usersPage.users)];
            filteredUsers = [... transFormUsers(usersPage.users)];
            limit = defaultLimit;
            generatePages();
        } else {
            loading = false;
            errorTitle = `Cannot delete user ${userId}`;
            isErrorOverlayOpen.set(true);
        }
        loading = false;
    }

    const fetchPage = async(page: number, changeUsersPerPage: boolean): Promise<void> => {
        if (changeUsersPerPage || (page < usersPage.totalPages && page >= 0 && page !== usersPage.currentPage)) {
            loading = true;
            [usersPage, errorBody] = await getWithJWT(`http://localhost:8082/api/users?page=${page}&limit=${limit}`, 200)
            if (errorBody !== null ) {
                error = errorBody.message;
                if (error === 'invalid token') goto('/sign-in')
            }
            if (!errorBody) {
                users = [... transFormUsers(usersPage.users)]
                noUsers = usersPage.users === null || usersPage.users.length < 10
                generatePages();
            } else {
                errorTitle = `Cannot fetch page ${page}`;
                isErrorOverlayOpen.set(true);
            }
            loading = false;
        }
    }

    const downloadUserConfig = async (userId: number, userName: string): Promise<void> => {
        loading = true;
        errorTitle = "Cannot download vpn file";
        await fetch('http://localhost:8082/api/users/' + userId + '/ovpn', {
                method: 'GET',
                mode: 'cors',
                headers: {
                    Authorization: 'Bearer '+Cookies.get('jwt')
                }
        }).then(async res => {
            if (res.ok) {
                saveAs(new File([await res.blob()], `${userName}.ovpn`, {type: `${res.headers.get('content-type')};charset=utf-8`}))
                return null
            } else if (res.status === 404) {
                infoTitle = "VPN config not detected";
                infoMessage = "Please, fill vpn configuration";
                isInfoOverlayOpen.set(true);
                return null;
            } else if (res.status === 403) {
                goto("/sign-in");
            }
            else {
                return res.json()
            }
        })
        .then(res => {
            if (res) {
                error = res.message
                isErrorOverlayOpen.set(true);
            }
        })
        .catch(() => {
            error = "Cannot connect to the server"
            isErrorOverlayOpen.set(true);
        })
        loading = false;
    }

    const sendVPN = async (data): Promise<void> => {
        const name = data.name;
        const email = data.email;
        loading = true;
        errorTitle = "Cannot send vpn file";
        await fetch(`http://localhost:8082/api/mail/${email}/file/${name}`, {
            method: 'GET',
            mode: 'cors',
            headers: {
                Authorization: 'Bearer '+Cookies.get('jwt')
            }
        }).then(async res => {
            if (res.ok) {
                return null
            } else if (res.status === 404) {
                infoTitle = "Email config not detected";
                infoMessage = "Please, fill email configuration";
                isInfoOverlayOpen.set(true);
            } else if (res.status === 403) {
                goto("/sign-in");
            } 
            else {
                return res.json()
            }
        })
        .then(res => {
            if (res) {
                error = res.message
                isErrorOverlayOpen.set(true);
            }
        })
        .catch(() => {
            error = "Cannot connect to the server";
            isErrorOverlayOpen.set(true);
        });
        loading = false;
    }


</script>

<svelte:head>
	<title>OVPN4ALL - Users</title>
	<meta name="description" content="OVPN4ALL setup page" />
</svelte:head>

<Header navbar={true}/>
{#if $isErrorOverlayOpen}
	<ErrorOverlay errorTitle={errorTitle} errorMessage={error} />
{/if}
{#if $isInfoOverlayOpen}
	<InfoOverlay infoTitle={infoTitle} infoMessage={infoMessage} link="/setup" linkMessage="Go to config setup" />
{/if}
{#if $isModalOverlayOpen}
    <ModalOverlay content={modalContent} action={modalAction} params={modalParams}/>
{/if}
<div class="overflow-x-auto relative shadow-md sm:rounded-lg my-5 hover:cursor-{loading ? 'wait' : 'default'}">
    <div class="flex justify-between items-center pb-4 bg-transparent">
        <label for="table-search" class="sr-only">Search</label>
        <div class="relative ml-5">
            <div class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
                <svg class="w-5 h-5 text-gray-500 dark:text-gray-400" aria-hidden="true" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"></path></svg>
            </div>
            <input bind:value={searchedUser} type="text" id="table-search-users" class="block p-2 pl-10 w-80 text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-secondary dark:focus:border-secondary" placeholder="Search for users">
        </div>
        <Spinner loading={loading}></Spinner>
        <a href="/sign-up" class="mr-5 my-3 mt-5 w-36 py-2 flex flex-col items-center justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 7.5v3m0 0v3m0-3h3m-3 0h-3m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.766z" />
            </svg>              
            Add user  
        </a>
    </div>
    <div class="flex-col mx-10">
        <table class="w-full text-sm text-left text-gray-500 dark:text-gray-400 px-5">
            <thead class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                <tr>
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
                    <td class="flex flex-col items-center py-4 px-6 text-gray-900 whitespace-nowrap dark:text-white">
                        <p class="text-base font-semibold">{user.name}</p>
                    </td>
                    <td class="py-4 px-6 text-center">
                        {user.email}
                    </td>
                    <td class="py-4 px-6 text-center">
                        {user.createdAt}
                    </td>
                    <td class="py-4 px-6 text-center">
                       {user.roles}
                    </td>
                    <td class="py-4 px-6">
                        <div class="flex justify-center">
                            {#if user.isUser}
                                <div on:click={() => downloadUserConfig(user.id, user.name)} class="flex flex-col items-center mr-4 hover:underline hover:text-secondary hover:cursor-pointer">
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                                        <path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 12L12 16.5m0 0L7.5 12m4.5 4.5V3" />
                                    </svg>
                                    Download ovpn
                                </div>
                                <div on:click={() => {modalContent = `You're going to send an email to '${user.email}', is it correct?`;modalAction = sendVPN; modalParams = {'name': user.name, 'email': user.email}; isModalOverlayOpen.set(true);}} class="flex flex-col items-center mr-4 hover:underline hover:text-secondary hover:cursor-pointer">
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                                        <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
                                    </svg>                              
                                    Send ovpn
                                </div>
                            {/if}
                            <div on:click={() => {modalContent = `You're going to delete user '${user.name}', is it correct?`;modalAction = deleteUser; modalParams = user.id; isModalOverlayOpen.set(true);}} class="flex flex-col items-center text-red-500 hover:underline hover:text-secondary hover:cursor-pointer">
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
    <div class="flex items-center justify-center mt-5 p-5">
        <svg on:click={() => fetchPage(0, false)} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 hover:underline hover:text-secondary hover:cursor-pointer">
            <path stroke-linecap="round" stroke-linejoin="round" d="M18.75 19.5l-7.5-7.5 7.5-7.5m-6 15L5.25 12l7.5-7.5" />
        </svg>
        <svg on:click={() => fetchPage(usersPage.currentPage - 1, false)}  xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 hover:underline hover:text-secondary hover:cursor-pointer">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
        </svg>
        {#each leftPages as pageNumber}
            <span on:click={() => fetchPage(pageNumber - 1, false)} class="mx-2 hover:underline hover:text-secondary hover:cursor-pointer">
                {pageNumber}
            </span>
        {/each}
        <span class="bg-white text-dark rounded-full px-2 mx-2 hover:underline hover:bg-secondary hover:text-primary hover:cursor-pointer">
            {usersPage.currentPage + 1}
        </span>
        {#each rightPages as pageNumber}
            <span on:click={() => fetchPage(pageNumber - 1, false)} class="mx-2 hover:underline hover:text-secondary hover:cursor-pointer">
                {pageNumber}
            </span>
        {/each}
        <svg on:click={() => fetchPage(usersPage.currentPage + 1, false)} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 hover:underline hover:text-secondary hover:cursor-pointer">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
        </svg>
        <svg on:click={() => fetchPage(usersPage.totalPages - 1, false)} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 hover:underline hover:text-secondary hover:cursor-pointer">
            <path stroke-linecap="round" stroke-linejoin="round" d="M11.25 4.5l7.5 7.5-7.5 7.5m-6-15l7.5 7.5-7.5 7.5" />
        </svg>
        <div class="ml-5">
            Users per page: 
            <select bind:value={limit} name="select" class="bg-dark">
                {#each values as value}
                    <option  on:click={() => {fetchPage(0, true)}} class="tex-white" value="{value}">{value}</option>
                {/each}
            </select>
        </div>
    </div>
    <p class="text-center">{usersPage.totalUsers} users registered</p>
</div>
