<script lang="ts">
    export let title: string;
    export let body: string;
</script>

<div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
    <strong class="font-bold">{title}</strong>
    <span class="block sm:inline">{body}</span>
</div>