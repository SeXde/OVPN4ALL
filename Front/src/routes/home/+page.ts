import { getWithJWT } from '$lib/utils/requestUtils';
import Cookies from 'js-cookie';

export const load = async ({ fetch }) => {
    
    
    const fetchSetup = async () => {
        const [setup, error] = await getWithJWT("http://localhost:8082/api/setup", 200);
        return [setup, error]
    }

    const fetchState = async ():Promise<any> => {
        const [status, error] = await getWithJWT("http://localhost:8082/api/status", 200);
        return [status, error]
    }
    
    const fetchUsers = async ():Promise<any> => {
        const [users, error] = await getWithJWT("http://localhost:8082/api/logs/users/connections", 200);
        return [users, error]
    }
    
    return {
        setup: fetchSetup(),
        state: fetchState(),
        users: fetchUsers()
    }
}
