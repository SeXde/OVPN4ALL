import { getWithJWT } from '$lib/utils/requestUtils';
import { PUBLIC_SERVER_URL } from '$env/static/public';
export const load = async ({ fetch }) => {
    
    const fetchUsers = async (): Promise<[Object, string]> => {
        const [users, error] = await getWithJWT(`${PUBLIC_SERVER_URL}/api/users?page=0`, 200)
        return [users, error]
    }
        
    return {
        users: fetchUsers()
    }
}
