import { getWithJWT } from '$lib/utils/requestUtils';
export const load = async ({ fetch }) => {
    
    const fetchUsers = async (): Promise<[Object, string]> => {
        const [users, error] = await getWithJWT('http://localhost:8082/api/users?page=0', 200)
        return [users, error]
    }
        
    return {
        users: fetchUsers()
    }
}
