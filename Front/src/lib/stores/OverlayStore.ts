import { writable } from "svelte/store";
export const isOverlayOpen = writable(false);
export const isErrorOverlayOpen = writable(false);
export const isInfoOverlayOpen = writable(false);
export const isModalOverlayOpen = writable(false);