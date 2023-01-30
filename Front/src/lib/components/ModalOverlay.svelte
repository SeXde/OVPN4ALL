<script lang="ts">
	import { isModalOverlayOpen } from "$lib/stores/OverlayStore";
	import { get } from "svelte/store";


    export let content: string;
    export let action;
    export let params;
</script>
<div class="w-screen h-screen fixed top-0 left-0 flex justify-center items-center z-10 "> 
    <div class="bg-light_dark px-8 py-10 relative max-w-lg opacity-95">
        <div class="flex flex-col items-center justify-center text-white">
            <h1 class="font-bold text-center text-2xl">{content}</h1>
            <div class="flex flex-row items-center justify-center p-5 text-2xl">
                <div on:click={() => {if (params) action(params); else action(); isModalOverlayOpen.set(false)}} class="hover:cursor-pointer hover:-translate-y-0.5 flex flex-col items-center justify-center text-green-600 mx-5 p-1">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
                        </svg>                  
                    Yes
                </div>
                <div on:click={() => {isModalOverlayOpen.set(false); console.log("stadito: ", isModalOverlayOpen);}} class="hover:cursor-pointer hover:-translate-y-0.5 flex flex-col items-center justify-center text-red-600 mx-5 p-1">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                        </svg>                  
                    No
                </div>
            </div>
        </div>
    </div>
</div>
