<script>
    import { stop_propagation } from "svelte/internal";
    import {isErrorOverlayOpen} from "../../routes/stores/OverlayStore";
    import { fade } from 'svelte/transition';
    export let errorTitle;
    export let errorMessage;
    if (isErrorOverlayOpen) {
        
        setTimeout(() => {
            isErrorOverlayOpen.set(false);
        }, 5000
    )
    }
    </script>
    <div transition:fade class="fixed flex self-end justify-center items-center z-10" on:click={() => isErrorOverlayOpen.set(false)}> 
        <div class="bg-red-700 px-8 py-10 relative max-w-lg opacity-95" on:click|stopPropagation>
            <div>
                <h1 class="text-5xl mb-2">{errorTitle}</h1>
                <p>{errorMessage}</p>
            </div>
        </div>
    </div>