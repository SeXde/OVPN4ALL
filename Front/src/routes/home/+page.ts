export const load = async ({ fetch }) => {
    
    const fetchSetup = async () => {
        let setup;
        await fetch("http://localhost:8082/api/setup/data")
        .then(res => res.json()) 
        .then(res => setup = res)
        .catch(e => {
            console.log(e); 
            setup = null;
        });
        return setup;
    }
        
    return {
        setup: fetchSetup()
    }
}
