import { PUBLIC_INTERNAL_SERVER_URL } from '$env/static/public';

export const load = async ({ fetch }) => {
    
    const fetchFirstUser = async () => {
        let error: string = null;
        let response: Boolean;
            await fetch(`${PUBLIC_INTERNAL_SERVER_URL}/api/users/noUsers`)
            .then(async res => {
                if (res.ok) {
                    response = await res.json();
                } else {
                    return res.json();
                }
            }).then(res => {
                if (res) {
                    error = res.message;
                }
            }). catch(() => {
                error = "Cannot contact with server";
            });
        return [error, response];
    }
        
    return {
        firstUser: fetchFirstUser()
    }
}
