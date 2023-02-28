<script lang="ts">
	import Header from "$lib/components/header.svelte";
	import { saveAs } from 'file-saver';
    import Cookies from 'js-cookie';
	import Spinner from "$lib/components/Spinner.svelte";
	import ErrorOverlay from "$lib/components/ErrorOverlay.svelte";
	import InfoOverlay from "$lib/components/InfoOverlay.svelte";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import ModalOverlay from "$lib/components/ModalOverlay.svelte";
	import { isErrorOverlayOpen, isInfoOverlayOpen, isModalOverlayOpen } from "$lib/stores/OverlayStore";
	
	export let data
	let [setup, dataError] = data.setup
	let errorTitle: string = "Server error"
	let errorMessage: string = "";
	if (dataError) {
		isInfoOverlayOpen .set(true);
	}
	let [connected, connectError] = data.state
	if (connectError) {
		errorMessage = errorMessage + "\n" + connectError.message;
	}
	
	let port: string = "---";
	let gateway: string = "---";
	let subnet: string = "---";
	let wanIp: string = "---";
	let loading: boolean = false;
	let bandwidthData: any = {
		'in': 0,
		'out': 0
	};
	
	let stompClient = null;
	let logs: Array<Array<string>> = [];
	let usersInfo = [];

	let selectedPage: Array<boolean> = [true, false, false, false, false, false];
	let logsName: Array<string> = ["createServerConfigLog", "createUserCertLog", "createUserVPNFileLog", "deleteUserLog", "OVPNLog"];
	let logTopics: Array<string> = ["/topic/log/createServerConfig", "/topic/log/createUserCert", "/topic/log/createUserVPNFile", "/topic/log/deleteUser", "/topic/log/OVPN"];
	let lines: number = 500;
	let values: Array<number> = [100, 200, 500, 800, 1200, 2000];
	let searchedValue: string = "";
	let userToDisconnect;
	let modalAction;
	let modalParams;

	if (setup != null) {
		port = setup.port;
		gateway = setup.gateway;
		subnet = setup.subnet;
		wanIp = setup.server;
	}

	if (errorMessage !== "") {
		isErrorOverlayOpen.set(true);
	}
	
	const downloadLogs = async (): Promise<void> => {
		loading = true;
		await fetch('http://localhost:8082/api/logs/', {
                method: 'GET',
                mode: 'cors',
                headers: {
                    Authorization: 'Bearer '+Cookies.get('jwt')
                }
        }).then(async res => {

            if (res.ok) {
                saveAs(new File([await res.blob()], `OVPN4ALL_Logs.zip`, {type: "application/gzip"}))
                return null
            } else if(res.status == 403) {
				goto("/sign-in")
			} else {
                return res.json()
            }
        })
        .then(res => {
            if (res) {
				errorMessage = res.message;
				isErrorOverlayOpen.set(true);
            }
        })
        .catch(() => {
            errorMessage = "Cannot connect to the server";
			isErrorOverlayOpen.set(true);
        })
		loading = false;
	}

	const changeVpnStatus = async () => {
		loading = true;
		let error: true;
		const endpoint = connected ? 'http://localhost:8082/api/status/off' : 'http://localhost:8082/api/status/on'
		await fetch(endpoint, {
                method: 'GET',
                mode: 'cors',
                headers: {
                    Authorization: 'Bearer '+Cookies.get('jwt')
                }
        }).then(res => {
			if (res.ok) {
				connected = !connected
			} else if(res.status == 403) {
				goto("/sign-in")
			} else {
				error = true;
				return res.json();
			}
		})
		.then(res => {
			if (error) {
				errorMessage = res.message;
				isErrorOverlayOpen.set(true);
			}
		})
		.catch(() =>  {
			if (!error) {
				errorMessage = `Cannot ${connected ? 'shutdown' : 'turn on'} vpn`;
				isErrorOverlayOpen.set(true);
			}
		})
		loading = false;
	}

	const correctUnits = (bytes: number): string => {
		if (bytes < 1000) {
			return `: ${bytes} B`;
		}
		if (bytes < 1000000) {
			return `: ${Math.round((bytes / 1000)*100) / 100} KB`;
		}
		if (bytes < 1000000000) {
			return `: ${Math.round((bytes / 1000000)*100) / 100} MB`;
		}
		return `: ${Math.round((bytes / 1000000000)*100) / 100} GB`;
	}

	const webSocketConnect = () => {
		let socket = new SockJS(`http://localhost:8082/ovpn4all-ws?ws-token=Bearer ${Cookies.get('jwt')}`);
        stompClient = Stomp.over(socket);
        stompClient.connect({}, (frame) => {
			for (let i = 0; i < logTopics.length; i++) {
					stompClient.subscribe(logTopics[i], (log) => {
					logs[i] = JSON.parse(log.body).filter(line => line.toLowerCase().includes(searchedValue.toLowerCase()));
				});
			}
			stompClient.subscribe('/topic/users/info', (info) => {
				usersInfo = JSON.parse(info.body);
            });
			stompClient.subscribe('/topic/server/info', (info) => {
                bandwidthData = JSON.parse(info.body);
            });
        });
	}

	const fetchLogs = () => {
		setInterval(() => {
            stompClient.send(`/app/log/createServerConfig/${lines}`, {}, {});
			stompClient.send(`/app/log/createUserCert/${lines}`, {}, {});
			stompClient.send(`/app/log/createUserVPNFile/${lines}`, {}, {});
			stompClient.send(`/app/log/deleteUser/${lines}`, {}, {});
			stompClient.send(`/app/log/OVPN/${lines}`, {}, {});
        }, 2000);
	}

	const selectPage = (index: number): void => {
		selectedPage.fill(false);
		selectedPage[index] = true;
	}

	const copyText = (index: number): void => {
		navigator.clipboard.writeText(logs[index].join("\n"));
	}

	const downloadText = (index: number): void => {
		saveAs(new File([logs[index].join("\n")], `${logsName[index]}.txt`, {type: "text/plain"}))
	}

	const disconnectUser = async (userName: string): Promise<void> => {
		loading = true;
		let error: true;
		const endpoint = `http://localhost:8082/api/users/disconnect/${userName}`;
		await fetch(endpoint, {
                method: 'GET',
                mode: 'cors',
                headers: {
                    Authorization: 'Bearer '+Cookies.get('jwt')
                }
        }).then(res => {
			if (res.ok) {
				return;
			} else if(res.status == 403) {
				goto("/sign-in")
			} else {
				error = true;
				return res.json();
			}
		})
		.then(res => {
			if (error) {
				errorMessage = res.message;
				isErrorOverlayOpen.set(true);
			}
		})
		.catch(() =>  {
			if (!error) {
				errorMessage = `Cannot disconnect user ${userName}, something went wrong :(`;
				isErrorOverlayOpen.set(true);
			}
		})
		loading = false;
	};

	onMount(() => {
		webSocketConnect();
		fetchLogs();
	});



</script>

<svelte:head>
	<title>OVPN4ALL - Home</title>
	<meta name="description" content="OVPN4ALL setup page" />
</svelte:head>

<Header navbar={true}/>
	{#if $isErrorOverlayOpen}
		<ErrorOverlay errorTitle={errorTitle} errorMessage={errorMessage}/>
	{/if}
	{#if $isInfoOverlayOpen}
		<InfoOverlay infoTitle="Config not detected" infoMessage="Please, fill vpn configuration" link="/setup" linkMessage="Go to config setup" />
	{/if}
	{#if $isModalOverlayOpen}
		<ModalOverlay content={`You're going to disconnect user '${userToDisconnect}', is it correct?`} action={modalAction} params={modalParams}/>
	{/if}
	<div class="flex flex-row m-5">
		<div class="flex flex-col">
			<div on:click={() => selectPage(0)} class="{selectedPage[0] ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center mb-4 pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" />
				  </svg>				   
				<p>VPN Info</p>	
			</div>
			<div on:click={() => selectPage(1)} class="{selectedPage[1] ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center mb-4 pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
				</svg>							   
				<p>Server Config Log</p>	
			</div>
			<div on:click={() => selectPage(2)} class="{selectedPage[2] ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center mb-4 pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
				</svg>							   
				<p>User Cert Log</p>	
			</div>
			<div on:click={() => selectPage(3)} class="{selectedPage[3] ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center mb-4 pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
				</svg>							   
				<p>User Config Log</p>	
			</div>
			<div on:click={() => selectPage(4)} class="{selectedPage[4] ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center mb-4 pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
				</svg>							   
				<p>Delete User Log</p>	
			</div>
			<div on:click={() => selectPage(5)} class="{selectedPage[5] ? 'text-secondary border-secondary' : ''} bg-light_dark flex flex-col items-center justify-center mb-4 pb-2 border rounded-md p-2 hover:text-secondary hover:border-secondary hover:cursor-pointer">
				<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
					<path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
				</svg>							   
				<p>OVNP4ALL Log</p>
			</div>
		</div>
		{#if selectedPage[0]}
		<div class="flex flex-col items-center my-auto m-auto">
			<div class="mt-5 bg-light_dark px-5 py-5 border rounded-lg">
				<div class="flex flex-col items-center mb-2 pb-2 border-b">
					<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
						<path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" />
					  </svg>				  
					<p>Info</p>	
				</div>
				<p>					
					VPN port: <strong><i>{port}</i></strong>
				</p>
				<p>
					Gateway: <strong><i>{gateway}</i></strong>
				</p>
				<p>
					Subnet: <strong><i>{subnet}</i></strong>
				</p>
				<p>
					WAN ip: <strong><i>{wanIp}</i></strong>
				</p>
				<p>			  
					VPN current state: 
					{#if connected}
					<strong><i><span class="text-green-500">Connected</span></i></strong>
						<p>Users connected: <strong><i>{usersInfo.length}</i></strong></p>
						<p>In <strong><i>{correctUnits(bandwidthData.in)}</i></strong></p>
						<p>Out <strong><i>{correctUnits(bandwidthData.out)}</i></strong></p>
					{:else}
					<strong><i><span class="text-red-500">Disconnected</span></i></strong>
					{/if}
				</p>
			</div>
			<div class="flex items-center align-middle mt-3">
				{#if !dataError}
					<button on:click={() => changeVpnStatus()} class="mr-3 my-3 mt-5 w-36 py-2 flex flex-col items-center justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
						{#if !connected}
							<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
								<path stroke-linecap="round" stroke-linejoin="round" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
								<path stroke-linecap="round" stroke-linejoin="round" d="M15.91 11.672a.375.375 0 010 .656l-5.603 3.113a.375.375 0 01-.557-.328V8.887c0-.286.307-.466.557-.327l5.603 3.112z" />
							</svg>
							Turn on
						{:else}
							<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
								<path stroke-linecap="round" stroke-linejoin="round" d="M14.25 9v6m-4.5 0V9M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
							</svg>
							Turn off		
						{/if}
					</button>
				{/if}
				<button on:click={() => downloadLogs()} class="my-3 mt-5 w-36 py-2 flex flex-col items-center justify-center text-light rounded-lg border-2 border-light hover:text-primary hover:border-primary disabled:border-stone-500 disabled:text-stone-500 font-semibold transition-colors">
					<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
						<path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 12L12 16.5m0 0L7.5 12m4.5 4.5V3" />
					</svg>
					Download logs
				</button>
			</div>
			<Spinner loading={loading}></Spinner>
			{#if usersInfo.length > 0}
				<div class="my-5">
					<table class="w-full text-sm text-left text-gray-500 dark:text-gray-400 rounded-lg">
						<thead class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
							<tr>
								<th scope="col" class="py-3 px-6">
									<div class="flex flex-col items-center">
										<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
											<path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
										  </svg>																						
										Username
									</div>
								</th>
								<th scope="col" class="py-3 px-6">
									<div class="flex flex-col items-center">
										<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
											<path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
										</svg>                                                      
										Ip
									</div>
								</th>
								<th scope="col" class="py-3 px-6">
									<div class="flex flex-col items-center">
										<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
											<path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15M12 9l-3 3m0 0l3 3m-3-3h12.75" />
										</svg>                                                                             
										Bytes in
									</div>
								</th>
								<th scope="col" class="py-3 px-6">
									<div class="flex flex-col items-center">
										<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
											<path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9" />
										</svg>                                            
										Bytes out
									</div>
								</th>
								<th scope="col" class="py-3 px-6">
									<div class="flex flex-col items-center">
										<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
											<path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
										  </svg>																				
										Connected since
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
						<tbody class="bg-other_dark">
							{#each usersInfo as entry}
								<tr class="bg-white dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600 border-t-2 mt-2">
									<td class="text-center py-4 px-6 text-gray-900 whitespace-nowrap dark:text-white">
										{entry.userName}
									</td>
									<td class="py-4 px-6 text-center">
										{entry.socket}
									</td>
									<td class="py-4 px-6 text-center">
										In {correctUnits(entry.bytesIn)}
									</td>
									<td class="py-4 px-6 text-center">
										Out {correctUnits(entry.bytesOut)}
									</td>
									<td class="py-4 px-6 text-center">
										{entry.connectedSince}
									</td>
									<td on:click={()  => {userToDisconnect=entry.userName;modalAction=disconnectUser;modalParams=entry.userName;isModalOverlayOpen.set(true)}} class="flex flex-col items-center justify-center py-4 px-6 text-center text-red-500 hover:text-secondary hover:cursor-pointer">
										<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 ">
											<path stroke-linecap="round" stroke-linejoin="round" d="M3 3l8.735 8.735m0 0a.374.374 0 11.53.53m-.53-.53l.53.53m0 0L21 21M14.652 9.348a3.75 3.75 0 010 5.304m2.121-7.425a6.75 6.75 0 010 9.546m2.121-11.667c3.808 3.807 3.808 9.98 0 13.788m-9.546-4.242a3.733 3.733 0 01-1.06-2.122m-1.061 4.243a6.75 6.75 0 01-1.625-6.929m-.496 9.05c-3.068-3.067-3.664-7.67-1.79-11.334M12 12h.008v.008H12V12z" />
										</svg>										  
										Disconnect
									</td>
								</tr>
							
							{/each}
						</tbody>
					</table>
				</div>
			{/if}
		</div>
		{/if}
		{#each logs as log, i}
			{#if selectedPage[i+1]}
			<label for="table-search" class="sr-only">Search</label>
			<div class="flex flex-col items-center justify-center w-4/5">
				<div class="relative mb-5">
					<div class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
						<svg class="w-5 h-5 text-gray-500 dark:text-gray-400" aria-hidden="true" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"></path></svg>
					</div>
					<input bind:value={searchedValue} type="text" id="table-search-users" class="block p-2 pl-10 w-80 text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-secondary dark:focus:border-secondary" placeholder="Search words">
				</div>
				<div class="mx-auto text-green-500 bg-light_dark w-3/5 p-14 rounded-lg relative overflow-scroll h-[38rem]">
					<div class="z-10 top-[11rem] right-[33rem] fixed text-slate-700">
						<div class="flex flex-row">
							<div on:click={() => copyText(i)} class="flex flex-col items-center justify-center m-5 hover:text-slate-200 hover:cursor-pointer">
								<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
									<path stroke-linecap="round" stroke-linejoin="round" d="M8.25 7.5V6.108c0-1.135.845-2.098 1.976-2.192.373-.03.748-.057 1.123-.08M15.75 18H18a2.25 2.25 0 002.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 00-1.123-.08M15.75 18.75v-1.875a3.375 3.375 0 00-3.375-3.375h-1.5a1.125 1.125 0 01-1.125-1.125v-1.5A3.375 3.375 0 006.375 7.5H5.25m11.9-3.664A2.251 2.251 0 0015 2.25h-1.5a2.251 2.251 0 00-2.15 1.586m5.8 0c.065.21.1.433.1.664v.75h-6V4.5c0-.231.035-.454.1-.664M6.75 7.5H4.875c-.621 0-1.125.504-1.125 1.125v12c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V16.5a9 9 0 00-9-9z" />
								</svg>														 
								<p>Copy</p>
							</div>
							<div on:click={() => downloadText(i)} class="flex flex-col items-center justify-center m-5 hover:text-slate-200 hover:cursor-pointer">
								<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
									<path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 12L12 16.5m0 0L7.5 12m4.5 4.5V3" />
								</svg>														 
								<p>Download</p>
							</div>
						</div>
					</div>
					<br>
					{#each log as line, j}
						<p class="p-1 hover:bg-gray-700 rounded-md hover:text-secondary"><span class="text-slate-600 mr-5">{j+1}</span>  {line}</p>
					{/each}
				</div>
			</div>
			{/if}
		{/each}
	</div>
