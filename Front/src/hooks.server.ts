import type { Handle } from '@sveltejs/kit';
import Cookies from 'js-cookie';
 
export const handle: Handle = async ({ event, resolve }) => {
    console.log("Hit: "+event.url.pathname)
    return await resolve(event)
}