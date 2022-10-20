export const load = async ({ fetch }) => {
    
    const fetchUsers = async () => {
        let users;
        await fetch("http://localhost:8082/api/users")
        .then(res => res.json()) 
        .then(res => users = res)
        .catch(e => {
            console.log(e); 
            users = {};
        });
        return users;
    }
        
    return {
        users: fetchUsers()
    }
}
