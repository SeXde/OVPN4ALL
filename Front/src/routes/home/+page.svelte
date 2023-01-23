<script lang="ts">
	import Header from "$lib/components/header.svelte";
	import { saveAs } from 'file-saver';
    import Cookies from 'js-cookie';
	import Spinner from "$lib/components/Spinner.svelte";
	import { isErrorOverlayOpen, isInfoOverlayOpen } from "../stores/OverlayStore";
	import ErrorOverlay from "$lib/components/ErrorOverlay.svelte";
	import InfoOverlay from "$lib/components/InfoOverlay.svelte";
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";

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
	let createServerConfigLog = {
		'lineNumber' : 1,
		'content' : ''
	};
	let createUserCertLog = {
		'lineNumber' : 1,
		'content' : ''
	};
	let createUserVPNFileLog = {
		'lineNumber' : 1,
		'content' : ''
	};
	let deleteUserLog = {
		'lineNumber' : 1,
		'content' : ''
	};
	let OVPNLog = {
		'lineNumber' : 1,
		'content' : ''
	};
	let usersInfo = [];
	let ussage;

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

	const updateUsers = (newUsers: Array<any>) => {
		newUsers.forEach(user => {
			if (!usersInfo.some(oldUser => oldUser.userName === user.userName)) {
				usersInfo.push(user);
			}
		})
	}

	const webSocketConnect = () => {
		let socket = new SockJS(`http://localhost:8082/ovpn4all-ws?ws-token=Bearer ${Cookies.get('jwt')}`);
        stompClient = Stomp.over(socket);
        stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/log/createServerConfig', (log) => {
                const logParsed = JSON.parse(log.body);
				console.log("El logggg del create: ", logParsed.content);
				createServerConfigLog.lineNumber = logParsed.lineNumber;
				createServerConfigLog.content = createServerConfigLog.content + logParsed.content;
				console.log("El contenidoooo del create: ", createServerConfigLog.content);
            });
            stompClient.subscribe('/topic/log/createUserCert', (log) => {
				const logParsed = JSON.parse(log.body);
				console.log("El logggg: ", logParsed.content);
				createUserCertLog.lineNumber = logParsed.lineNumber;
				createUserCertLog.content = createUserCertLog.content + logParsed.content;
				console.log("El contenidoooo: ", createUserCertLog.content);
            });
            stompClient.subscribe('/topic/log/createUserVPNFile', (log) => {
				const logParsed = JSON.parse(log.body);
				console.log("El logggg: ", logParsed.content);
				createUserVPNFileLog.lineNumber = logParsed.lineNumber;
				createUserVPNFileLog.content = createUserVPNFileLog.content + logParsed.content;
				console.log("El contenidoooo: ", createUserVPNFileLog.content);
            });
			stompClient.subscribe('/topic/log/deleteUser', (log) => {
				const logParsed = JSON.parse(log.body);
				console.log("El logggg: ", logParsed.content);
				deleteUserLog.lineNumber = logParsed.lineNumber;
				deleteUserLog.content = deleteUserLog.content + logParsed.content;
				console.log("El contenidoooo: ", deleteUserLog.content);
            });
			stompClient.subscribe('/topic/log/OVPN', (log) => {
				const logParsed = JSON.parse(log.body);
				console.log("El logggg: ", logParsed.content);
				OVPNLog.lineNumber = logParsed.lineNumber;
				OVPNLog.content = OVPNLog.content + logParsed.content;
            });
			stompClient.subscribe('/topic/users/info', (info) => {
				usersInfo = JSON.parse(info.body);
            });
			stompClient.subscribe('/topic/server/info', (info) => {
                const infoParsed = JSON.parse(info.body);
                bandwidthData = infoParsed;
            });
        });
	}

	const fetchLogs = () => {
		setInterval(() => {
            stompClient.send(`/app/log/createServerConfig/${createServerConfigLog.lineNumber}`, {}, {});
			stompClient.send(`/app/log/createUserCert/${createUserCertLog.lineNumber}`, {}, {});
			stompClient.send(`/app/log/createUserVPNFile/${createUserVPNFileLog.lineNumber}`, {}, {});
			stompClient.send(`/app/log/deleteUser/${deleteUserLog.lineNumber}`, {}, {});
			stompClient.send(`/app/log/OVPN/${OVPNLog.lineNumber}`, {}, {});
        }, 2000);
	}

	onMount(() => {
		webSocketConnect();
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
	<div class="flex flex-col items-center my-auto mr-5">
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
		{#if connected}
			<div class="bg-blacky my-5 border rounded-lg">
				<table class="w-full text-sm text-left px-5">
					<thead class="text-xs">
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
						</tr>
					</thead>
					<tbody class="bg-other_dark">
						{#each usersInfo as entry}
							<tr class="hover:bg-gray-700 border-t-2 mt-2">
								<td class="flex flex-col items-center py-4 px-6 text-gray-900 whitespace-nowrap dark:text-white">
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
							</tr>
						
						{/each}
					</tbody>
				</table>
			</div>
		{/if}
		<div class="flex items-center align-middle mt-2">
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
	</div>