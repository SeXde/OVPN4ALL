<script lang="ts">
	import { goto } from '$app/navigation';
    import ErrorMessage from '$lib/components/errorMessage.svelte';
    import {portValidator, gatewayValidator, netmaskValidator} from '$lib/helpers/validators';
    import Header from "$lib/components/header.svelte";
    import { postWithJWT } from '../../utils/requestUtils';


    let portTitleErrorMessage: string, gatewayTitleErrorMessage: string, netmaskTitleErrorMessage: string, portBodyErrorMessage: string, gatewayBodyErrorMessage: string, netmaskBodyErrorMessage: string, postError: string;
    let port: string, gateway:string, netmask: string;
    let validData: boolean = false;
    let isLoading: boolean = false;
    let validPort: boolean = false;
    let validGateway: boolean = false;
    let validNetmask: boolean = false;
    portTitleErrorMessage = gatewayTitleErrorMessage = netmaskTitleErrorMessage = portBodyErrorMessage = gatewayBodyErrorMessage = netmaskBodyErrorMessage = postError = null;

    const validatePort = () => {
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

    const validateInput = () => {validData = validPort && validGateway && validNetmask;}

    async function sendData(){
        
        isLoading = true;

        const data: string = JSON.stringify({
            'port' : port,
            'gateway' : gateway,
            'subnet' : netmask
        });
        const [res, error] = await postWithJWT('http://localhost:8082/api/setup', 201, data)
        console.log(res)
        console.log(error)
        isLoading = false;
        if (error.message) return;
        goto("/home")

    };
    
</script>

<svelte:head>
	<title>OVPN4ALL - Setup</title>
	<meta name="description" content="OVPN4ALL setup page" />
</svelte:head>

<Header navbar={true}/>
<div class="mx-auto my-auto w-full max-w-xs">
    <form on:change={validateInput} on:submit|preventDefault={sendData} class= "px-8 pt-6 pb-8 mb-4">
        <div class="flex flex-col items-center">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                <path stroke-linecap="round" stroke-linejoin="round" d="M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 11-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 004.486-6.336l-3.276 3.277a3.004 3.004 0 01-2.25-2.25l3.276-3.276a4.5 4.5 0 00-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437l1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008z" />
            </svg>  
            <h2 class="text-center"> VPN Settings </h2>
        </div>
        <div class="relative my-3">
            <input on:keyup={validatePort} required bind:value={port} type="text" id="port" class="block px-2.5 pb-2.5 pt-5 w-full text-sm text-gray-900 bg-gray-50 dark:bg-gray-700 rounded-lg border-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-primary focus:outline-none focus:ring-0 focus:border-blue-600 peer" placeholder=" " />
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
  </div>
