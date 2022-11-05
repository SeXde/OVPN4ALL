<script lang="ts">
	import { goto } from '$app/navigation';
    import ErrorMessage from '$lib/components/errorMessage.svelte';
    import {portValidator, gatewayValidator, netmaskValidator, fqdnValidator, emailValidator} from '$lib/helpers/validators';
    import Header from "$lib/components/header.svelte";
    import { postWithJWT } from '$lib/utils/requestUtils';


    let portTitleErrorMessage: string, gatewayTitleErrorMessage: string, netmaskTitleErrorMessage: string, portBodyErrorMessage: string, gatewayBodyErrorMessage: string, netmaskBodyErrorMessage: string, postError: string;
    let smtpHostTitleMessage: string
    let smtpHostErrorMessage: string
    let usernameBodyErrorMessage: string
    let usernameTitleErrorMessage: string
    let username: string
    let password: string
    let port: string, gateway:string, netmask: string, server: string, smtpHost: string, smtpPort: string;
    let validData: boolean = false;
    let isLoading: boolean = false;
    let validPort: boolean = false;
    let validSmtpHost: boolean = false;
    let validGateway: boolean = false;
    let validNetmask: boolean = false;
    let vpnSettings: boolean = true;
    let validEmailData: boolean = false;
    let validUsername: boolean = false
    let isTls: boolean = false;
    portTitleErrorMessage = gatewayTitleErrorMessage = netmaskTitleErrorMessage = portBodyErrorMessage = gatewayBodyErrorMessage = netmaskBodyErrorMessage = postError = null;

    const validatePort = (port: string) => {
        validPort = portValidator(port);
        portTitleErrorMessage = validPort ? null : "Port is not valid.";
        portBodyErrorMessage = validPort ? null : "Port must be a number between 0 and 65536.";
    }

    const validateGateway = () => {
        validGateway = gatewayValidator(gateway);
        gatewayTitleErrorMessage = validGateway ? null : "Gateway is not valid.";
        gatewayBodyErrorMessage = validGateway ? null : "Gateway should look like this: \"192.168.0.1\".";
    }

    const validateNetmask = () => {
        validNetmask = netmaskValidator(netmask);
        netmaskTitleErrorMessage = validNetmask ? null : "Netmask is not valid.";
        netmaskBodyErrorMessage = validNetmask ? null : "Netmask should look like this: \"255.255.255.0\".";
    }

    const validateSmtpHost = (): void => {
        validSmtpHost = fqdnValidator(smtpHost)
        smtpHostTitleMessage = validSmtpHost ? null : "Smtp host name is not valid.";
        smtpHostErrorMessage = validSmtpHost ? null : "Smtp host name should look like this: \"mail.mydomain.com\".";
    }

    const validateUsername = (): void => {
        validUsername = emailValidator(username)
        usernameTitleErrorMessage = validUsername ? null : "Usernameis not valid.";
        usernameBodyErrorMessage = validUsername ? null : "Username should look like this: \"myemail@mydomain.com\".";
    }

    const validateEmailInput = (): void => {
        validEmailData = validSmtpHost && validPort && validUsername
    }

    const validateInput = () => {validData = validPort && validGateway && validNetmask;}

    async function sendData(){
        
        isLoading = true;

        const data: string = JSON.stringify({
            port,
            gateway,
            subnet : netmask,
            server
        });
        const [res, error] = await postWithJWT('http://localhost:8082/api/setup', 201, data)
        console.log(res)
        console.log(error)
        isLoading = false;
        if (error) return;
        goto("/home")

    };

    const sendEmailData = async ():Promise<void> => {
        isLoading = true;

        const data: string = JSON.stringify({
            smtpHost,
            smtpPort,
            ttl : isTls,
            username,
            password
        });
        const [res, error] = await postWithJWT('http://localhost:8082/api/mail', 200, data)
        console.log(res)
        console.log(error)
        isLoading = false;
        if (error) return;
        goto("/users")

    }
    
</script>

<svelte:head>
	<title>OVPN4ALL - Setup</title>
	<meta name="description" content="OVPN4ALL setup page" />
</svelte:head>

<Header navbar={true}/>
<div class="h-full">
    <div class="w-1/12 bg-light_dark my-5 rounded-md">
        <div on:click={() => vpnSettings = true} class="bg-light_dark flex flex-col items-center justify-center mb-2 pb-2 border-b border-r border-t rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
            </svg>  
            <p>VPN Settings</p>	
        </div>
        <div on:click={() => vpnSettings = false} class="bg-light_dark flex flex-col items-center justify-center mb-2 pb-2 border-b border-r border-t rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
            </svg>               
            <p>Mail Settings</p>	
        </div>
    </div>
    <div class="flex items-center justify-center">
        {#if vpnSettings}
            <form on:change={validateInput} on:submit|preventDefault={sendData} class= "px-8 pt-6 pb-8 mb-4">
                <div class="flex flex-col items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 11-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 004.486-6.336l-3.276 3.277a3.004 3.004 0 01-2.25-2.25l3.276-3.276a4.5 4.5 0 00-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008z" />
                    </svg>  
                    <h2 class="text-center"> VPN Settings </h2>
                </div>
                <div class="relative my-3">
                    <input on:keyup={() => validatePort(port)} required bind:value={port} type="text" id="port" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="port" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">VPN port (444)</label>
                </div>
                {#if portTitleErrorMessage}
                    <ErrorMessage title={portTitleErrorMessage} body={portBodyErrorMessage} />
                {/if}
                <div class="relative my-3">
                    <input on:change={validateGateway} required bind:value={gateway} type="text" id="gateway" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="gateway" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Gateway (192.168.0.1)</label>
                </div>
                {#if gatewayTitleErrorMessage}
                    <ErrorMessage title={gatewayTitleErrorMessage} body={gatewayBodyErrorMessage} />
                {/if}
                <div class="relative my-3">
                    <input on:change={validateNetmask} required bind:value={netmask} type="text" id="netmask" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="netmask" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Subnet mask (255.255.255.0)</label>
                </div>
                <div class="relative my-3">
                    <input on:change={validateGateway} required bind:value={server} type="text" id="server" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="server" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Server public IP (205.23.4.1)</label>
                </div>
                {#if netmaskTitleErrorMessage}
                    <ErrorMessage title={netmaskTitleErrorMessage} body={netmaskBodyErrorMessage} />
                {/if}
                {#if validData}
                    <button type="submit" class="my-3 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
                        {#if isLoading}
                            <div class="border border-t-4 border-b-2 border-primary rounded-full  animate-spin">
                                <div class="p-2"></div>
                            </div>
                        {:else}
                            Save settings
                        {/if}
                    </button>
                {:else}
                    <button disabled type="submit" class="my-3 disabled:opacity-25 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">Save settings</button>
                {/if}
                {#if postError}
                    <ErrorMessage title={"Server error."} body={postError} />
                {/if}
            </form>
        {:else}
        <form on:change={validateEmailInput} on:submit|preventDefault={sendEmailData} class= "px-8 pt-6 pb-8 mb-4">
            <div class="flex flex-col items-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 11-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 004.486-6.336l-3.276 3.277a3.004 3.004 0 01-2.25-2.25l3.276-3.276a4.5 4.5 0 00-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008z" />
                </svg>  
                <h2 class="text-center"> Mail Settings </h2>
            </div>
            <div class="relative my-3">
                <input on:keyup={validateSmtpHost} required bind:value={smtpHost} type="text" id="smtpHost" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                <label for="smtpHost" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Smtp host (mail.domain.com)</label>
            </div>
            {#if smtpHostErrorMessage}
                <ErrorMessage title={smtpHostTitleMessage} body={smtpHostErrorMessage} />
            {/if}
            <div class="relative my-3">
                <input on:change={() => validatePort(smtpPort)} required bind:value={smtpPort} type="text" id="smtpPort" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                <label for="smtpPort" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Smtp port (25)</label>
            </div>
            {#if portTitleErrorMessage}
                    <ErrorMessage title={portTitleErrorMessage} body={portBodyErrorMessage} />
            {/if}
            <div class="relative my-3">
                <input on:change={validateUsername} required bind:value={username} type="text" id="username" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                <label for="username" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Email (myemail@domain.com)</label>
            </div>
            {#if usernameTitleErrorMessage}
                    <ErrorMessage title={usernameTitleErrorMessage} body={usernameBodyErrorMessage} />
            {/if}
            <div class="relative my-3">
                <input required bind:value={password} type="password" id="password" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                <label for="password" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Password</label>
            </div>
            <div class="ml-5 relative my-3">
				<label for="user-toggle" class="inline-flex relative items-center cursor-pointer">
					<input on:click={() => isTls = !isTls} type="checkbox" value="" id="user-toggle" class="sr-only peer">
					<div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 dark:peer-focus:ring-blue-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600"></div>
					<span class="ml-3 text-sm font-medium text-gray-900 dark:text-gray-300">STARTTLS</span>
				  </label>
			</div>
            {#if validEmailData}
                <button type="submit" class="my-3 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
                    {#if isLoading}
                        <div class="border border-t-4 border-b-2 border-primary rounded-full  animate-spin">
                            <div class="p-2"></div>
                        </div>
                    {:else}
                        Save settings
                    {/if}
                </button>
            {:else}
                <button disabled type="submit" class="my-3 disabled:opacity-25 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">Save settings</button>
            {/if}
            {#if postError}
                <ErrorMessage title={"Server error."} body={postError} />
            {/if}
        </form>
        {/if}
    </div>
  </div>
