<script lang="ts">
	import { goto } from '$app/navigation';
    import ErrorMessage from '$lib/components/errorMessage.svelte';
    import {portValidator, gatewayValidator, netmaskValidator, fqdnValidator, emailValidator} from '$lib/helpers/validators';
    import Header from "$lib/components/header.svelte";
    import { postWithJWT } from '$lib/utils/requestUtils';
	import { isErrorOverlayOpen, isInfoOverlayOpen } from '../stores/OverlayStore';
	import ErrorOverlay from '$lib/components/ErrorOverlay.svelte';
	import InfoOverlay from '$lib/components/InfoOverlay.svelte';


    let portTitleErrorMessage: string, gatewayTitleErrorMessage: string, netmaskTitleErrorMessage: string, portBodyErrorMessage: string, gatewayBodyErrorMessage: string, netmaskBodyErrorMessage: string, postError: string;
    let smtpHostTitleMessage: string
    let smtpHostErrorMessage: string
    let networkTitleErrorMessage: string;
    let networkErrorMessage: string;
    let usernameBodyErrorMessage: string
    let usernameTitleErrorMessage: string
    let username: string
    let password: string
    let port: string, gateway: string, netmask: string, server: string, smtpHost: string, smtpPort: string;
    let validData: boolean = false;
    let isLoading: boolean = false;
    let validPort: boolean = false;
    let validSmtpHost: boolean = false;
    let validGateway: boolean = false;
    let validNetmask: boolean = false;
    let vpnSettings: boolean = true;
    let validEmailData: boolean = false;
    let validUsername: boolean = false;
    let validNetwork: boolean = false;
    let infoMessage: string;
    let isTls: boolean = false;
    const timeOutMs: number = 500;
    let isLoading2: boolean = false;
    let portTimeOut, gatewayTimeOut, netmaskTimeOut, networkTimeOut, smtpHostTimeOut, usernameTimeOut, emailInputTimeOut;
    portTitleErrorMessage = gatewayTitleErrorMessage = netmaskTitleErrorMessage = portBodyErrorMessage = gatewayBodyErrorMessage = netmaskBodyErrorMessage = postError = networkTitleErrorMessage = networkErrorMessage = null;

    const validatePort = (port: string) => {
        portTitleErrorMessage = portBodyErrorMessage = null;
        clearTimeout(portTimeOut);
        validPort = portValidator(port);
        portTimeOut = setTimeout(() => {
            portTitleErrorMessage = validPort ? null : "Port is not valid.";
            portBodyErrorMessage = validPort ? null : "Port must be a number between 0 and 65536.";
        }, timeOutMs);
    }

    const validateGateway = () => {
        gatewayTitleErrorMessage = gatewayBodyErrorMessage = null;
        clearTimeout(gatewayTimeOut);
        validGateway = gatewayValidator(gateway);
        gatewayTimeOut = setTimeout(() => {
            gatewayTitleErrorMessage = validGateway ? null : "Gateway is not valid.";
            gatewayBodyErrorMessage = validGateway ? null : "Gateway should look like this: \"192.168.0.1\".";
        }, timeOutMs);
    }

    const validateNetmask = () => {
        netmaskTitleErrorMessage = netmaskBodyErrorMessage = null;
        clearTimeout(netmaskTimeOut);
        validNetmask = netmaskValidator(netmask);
        netmaskTimeOut = setTimeout(() => {
            netmaskTitleErrorMessage = validNetmask ? null : "Netmask is not valid.";
            netmaskBodyErrorMessage = validNetmask ? null : "Netmask should look like this: \"255.255.255.0\".";
        }, timeOutMs);
    }

    const validateNetwork = () => {
        networkTitleErrorMessage = networkErrorMessage = null;
        clearTimeout(networkTimeOut);
        validNetwork = gatewayValidator(gateway);
        networkTimeOut = setTimeout(() => {
            networkTitleErrorMessage = validNetwork ? null : "Server public ip is not valid.";
            networkErrorMessage = validNetwork ? null : "Server public ip should look like this: \"202.3.45.100\".";
        }, timeOutMs);
    }

    const validateSmtpHost = (): void => {
        smtpHostTitleMessage = smtpHostErrorMessage = null;
        clearTimeout(smtpHostTimeOut);
        validSmtpHost = fqdnValidator(smtpHost);
        networkTimeOut = setTimeout(() => {
            smtpHostTitleMessage = validSmtpHost ? null : "Smtp host name is not valid.";
            smtpHostErrorMessage = validSmtpHost ? null : "Smtp host name should look like this: \"mail.mydomain.com\".";
        }, timeOutMs); 
    }

    const validateUsername = (): void => {
        usernameTitleErrorMessage = usernameBodyErrorMessage = null;
        clearTimeout(usernameTimeOut);
        validUsername = emailValidator(username)
        networkTimeOut = setTimeout(() => {
            usernameTitleErrorMessage = validUsername ? null : "Usernameis not valid.";
            usernameBodyErrorMessage = validUsername ? null : "Username should look like this: \"myemail@mydomain.com\".";
        }, timeOutMs); 
    }

    const validateEmailInput = (): void => {
        validEmailData = validSmtpHost && validPort && validUsername
    }

    async function sendData(){
        
        isLoading = true;

        const data: string = JSON.stringify({
            port,
            gateway,
            subnet : netmask,
            server
        });
        const [, error] = await postWithJWT('http://localhost:8082/api/setup', 201, data)
        isLoading = false;
        if (!error) {
            infoMessage = "VPN config was saved";
            isInfoOverlayOpen.set(true);
            return;
        } 
        if (error.message.includes("token")) {
            goto("/sign-in")
        }
        postError = error.message;
        isErrorOverlayOpen.set(true);
        
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
        const [, error] = await postWithJWT('http://localhost:8082/api/mail', 200, data)
        isLoading = false;
        if (!error) {
            infoMessage = "Email config was saved";
            isInfoOverlayOpen.set(true);
            return;
        } 
        if (error.message.includes("token")) {
            goto("/sign-in")
        }
        postError = error.message;
        isErrorOverlayOpen.set(true);
    }

    const getPublicIp = async ():Promise<void> => {
        isLoading2 = true;
		let error: true;
		const endpoint = 'https://api.ipify.org?format=json'
		await fetch(endpoint, {
                method: 'GET'
        }).then(res => {
			if (res.ok) {
				return res;
			} else {
				error = true;
				return res.json();
			}
		})
		.then(async res => {
			if (error) {
				postError = res.message;
				isErrorOverlayOpen.set(true);
			} else {
                const resJson = await res.json();
                server = resJson.ip;
            }
		})
		.catch(() =>  {
			if (!error) {
				postError = `Cannot guess public ip`;
				isErrorOverlayOpen.set(true);
			}
		})
        isLoading2 = false;
    }
    
</script>

<svelte:head>
	<title>OVPN4ALL - Setup</title>
	<meta name="description" content="OVPN4ALL setup page" />
</svelte:head>

<Header navbar={true}/>
{#if $isErrorOverlayOpen}
    <ErrorOverlay errorTitle="Server error" errorMessage={postError} />
{/if}
{#if $isInfoOverlayOpen}
    <InfoOverlay infoTitle="Config saved" infoMessage={infoMessage} linkMessage="" link=""/>
{/if}
<div class="h-full">
    <div class="w-1/12 my-5 px-5">
        <div on:click={() => vpnSettings = true} class="{vpnSettings ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center mb-4 pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
            </svg>  
            <p>VPN Settings</p>	
        </div>
        <div on:click={() => vpnSettings = false} class="{!vpnSettings ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
            </svg>               
            <p>Mail Settings</p>	
        </div>
    </div>
    <div class="flex flex-col items-center justify-center">
        {#if vpnSettings}
            <form on:submit|preventDefault={sendData} class= "px-8 pt-6 pb-8 mb-4">
                <div class="flex flex-col items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
                    </svg> 
                    <h2 class="text-center"> VPN Settings </h2>
                </div>
                <div class="relative my-3">
                    <input on:change={() => validatePort(port)} required bind:value={port} type="text" id="port" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="port" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">VPN port (444)</label>
                </div>
                <div class="relative my-3">
                    <input on:change={validateGateway} required bind:value={gateway} type="text" id="gateway" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="gateway" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Gateway (192.168.0.1)</label>
                </div>
                <div class="relative my-3">
                    <input on:change={validateNetmask} required bind:value={netmask} type="text" id="netmask" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="netmask" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Subnet mask (255.255.255.0)</label>
                </div>
                <div class="relative my-3">
                    <input on:change={validateNetwork} required bind:value={server} type="text" id="server" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                    <label for="server" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Server public IP (205.23.4.1)</label>
                </div>
                <div class="flex flex-col items-center align-middle">
                    <button type="submit" class="my-3 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
                        {#if isLoading}
                            <div class="border border-t-4 border-b-2 border-primary rounded-full  animate-spin">
                                <div class="p-2"></div>
                            </div>
                        {:else}
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="mr-2 w-6 h-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 6.375c0 2.278-3.694 4.125-8.25 4.125S3.75 8.653 3.75 6.375m16.5 0c0-2.278-3.694-4.125-8.25-4.125S3.75 4.097 3.75 6.375m16.5 0v11.25c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125V6.375m16.5 0v3.75m-16.5-3.75v3.75m16.5 0v3.75C20.25 16.153 16.556 18 12 18s-8.25-1.847-8.25-4.125v-3.75m16.5 0c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125" />
                          </svg>
                            Save settings
                        {/if}
                    </button>
                    <div on:click={getPublicIp} class="font-semibold bg-dark flex flex-row items-center justify-center mb-4 pb-2 rounded-lg border-2 border-light p-2 hover:text-primary hover:border-primary hover:cursor-pointer">                                                       
                          {#if isLoading2}
                          <div class="border border-t-4 border-b-2 border-primary rounded-full  animate-spin">
                              <div class="p-2"></div>
                          </div>
                        {:else}
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="mr-2 w-6 h-6">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 15a4.5 4.5 0 004.5 4.5H18a3.75 3.75 0 001.332-7.257 3 3 0 00-3.758-3.848 5.25 5.25 0 00-10.233 2.33A4.502 4.502 0 002.25 15z" />
                            </svg>  
                            <p class="">Guess public ip</p>
                        {/if}
                    </div>
                </div>
            </form>
            {#if portTitleErrorMessage}
                <ErrorMessage title={portTitleErrorMessage} body={portBodyErrorMessage} />
            {/if}
            {#if gatewayTitleErrorMessage}
                <ErrorMessage title={gatewayTitleErrorMessage} body={gatewayBodyErrorMessage} />
            {/if}
            {#if netmaskTitleErrorMessage}
                <ErrorMessage title={netmaskTitleErrorMessage} body={netmaskBodyErrorMessage} />
            {/if}
        {:else}
        <form on:change={validateEmailInput} on:submit|preventDefault={sendEmailData} class= "px-8 pt-6 pb-8 mb-4">
            <div class="flex flex-col items-center">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
                </svg>  
                <h2 class="text-center"> Mail Settings </h2>
            </div>
            <div class="relative my-3">
                <input on:change={validateSmtpHost} required bind:value={smtpHost} type="text" id="smtpHost" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                <label for="smtpHost" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Smtp host (mail.domain.com)</label>
            </div>
            <div class="relative my-3">
                <input on:change={() => validatePort(smtpPort)} required bind:value={smtpPort} type="text" id="smtpPort" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                <label for="smtpPort" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Smtp port (25)</label>
            </div>
            <div class="relative my-3">
                <input on:change={validateUsername} required bind:value={username} type="text" id="username" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
                <label for="username" class="absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-2.5 peer-focus:text-blue-600 peer-focus:dark:text-primary peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4">Email (myemail@domain.com)</label>
            </div>
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
            <button type="submit" class="my-3 mx-auto mt-5 w-36 py-2 flex justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
                {#if isLoading}
                    <div class="border border-t-4 border-b-2 border-primary rounded-full  animate-spin">
                        <div class="p-2"></div>
                    </div>
                {:else}
                    Save settings
                {/if}
            </button>
        </form>
            {#if smtpHostErrorMessage}
                <ErrorMessage title={smtpHostTitleMessage} body={smtpHostErrorMessage} />
            {/if}
            {#if portTitleErrorMessage}
                <ErrorMessage title={portTitleErrorMessage} body={portBodyErrorMessage} />
            {/if}
            {#if usernameTitleErrorMessage}
                <ErrorMessage title={usernameTitleErrorMessage} body={usernameBodyErrorMessage} />
            {/if}
        {/if}
    </div>
  </div>
