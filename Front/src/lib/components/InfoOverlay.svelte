<script>
    import { stop_propagation } from "svelte/internal";
    import {isInfoOverlayOpen} from "../../routes/stores/OverlayStore";
    import { fade } from 'svelte/transition';
    export let infoTitle;
    export let infoMessage;
    export let link;
    export let linkMessage;
    if (isInfoOverlayOpen) {
        
        setTimeout(() => {
            isInfoOverlayOpen.set(false);
        }, 10000
    )
    }
    </script>
    <div transition:fade class="w-screen h-screen fixed top-0 left-0 flex justify-center items-center z-10" on:click={() => isInfoOverlayOpen.set(false)}> 
        <div class="bg-blue-800 px-8 py-10 relative max-w-lg opacity-95" on:click|stopPropagation>
            <div>
                <button class="absolute top-2 right-3 text-4xl text-gray-300 hover:-translate-y-0.5 transition-transform" on:click={() => isInfoOverlayOpen.set(false)}>
                    &times;
                </button>
                <h1 class="text-2xl mb-2">{infoTitle}</h1>
                <p class="mb-5">{infoMessage}</p>
                <a class="bg-blue-900 hover:text-secondary hover:cursor-pointer border rounded-xl p-4" href="{link}">{linkMessage}</a>
            </div>
        </div>
    </div>