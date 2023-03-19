import { getWithJWT } from '$lib/utils/requestUtils';
import Cookies from 'js-cookie';
import { PUBLIC_SERVER_URL } from '$env/static/public';

export const load = async ({ fetch }) => {
    
    
    const fetchSetup = async () => {
        const [setup, error] = await getWithJWT(`${PUBLIC_SERVER_URL}/api/setup`, 200);
        return [setup, error]
    }

    const fetchState = async ():Promise<any> => {
        const [status, error] = await getWithJWT(`${PUBLIC_SERVER_URL}/api/status`, 200);
        return [status, error]
    }
    
    return {
        setup: fetchSetup(),
        state: fetchState()
    }
}
