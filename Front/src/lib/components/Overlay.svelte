<script>
    import { stop_propagation } from "svelte/internal";
import {isOverlayOpen} from "../../routes/stores/OverlayStore"
</script>
<div class="w-screen h-screen fixed top-0 left-0 flex justify-center items-center z-10 " on:click={() => isOverlayOpen.set(false)}> 
    <div class="bg-light_dark px-8 py-10 relative max-w-lg opacity-95" on:click|stopPropagation>
        <div>
            <button class="absolute top-2 right-3 text-4xl text-gray-300 hover:-translate-y-0.5 transition-transform" on:click={() => isOverlayOpen.set(false)}>
                &times;
            </button>
            <slot></slot>
        </div>
    </div>
</div>